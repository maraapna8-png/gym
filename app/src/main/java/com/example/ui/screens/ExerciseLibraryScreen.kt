package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Exercise
import com.example.ui.components.AiCameraCoachModal
import com.example.ui.components.ExerciseCardItem
import com.example.ui.components.ExerciseGraphicImage
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(viewModel: FitProViewModel) {
    val exercises by viewModel.filteredExercises.collectAsState()
    val searchQuery by viewModel.exerciseSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedExerciseForDetail by remember { mutableStateOf<Exercise?>(null) }

    val categories = listOf(
        "All", "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Legs", "Abs", "Cardio"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("exercise_library_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exercise_search_input"),
            placeholder = { Text("Search 200+ exercises or body parts...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = NeonGreen) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { viewModel.setCategoryFilter(cat) },
                    label = { Text(cat, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonGreen,
                        selectedLabelColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(exercises) { exercise ->
                ExerciseCardItem(
                    exercise = exercise,
                    onFavoriteToggle = { viewModel.toggleFavoriteExercise(exercise) },
                    onClick = { selectedExerciseForDetail = exercise }
                )
            }
        }
    }

    var exerciseForCameraCoach by remember { mutableStateOf<Exercise?>(null) }

    if (exerciseForCameraCoach != null) {
        AiCameraCoachModal(
            exercise = exerciseForCameraCoach!!,
            onDismiss = { exerciseForCameraCoach = null }
        )
    }

    // Exercise Detail Modal / Dialog
    selectedExerciseForDetail?.let { exercise ->
        AlertDialog(
            onDismissRequest = { selectedExerciseForDetail = null },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val current = exercise
                            selectedExerciseForDetail = null
                            exerciseForCameraCoach = current
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera Coach")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI CAMERA COACH", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { selectedExerciseForDetail = null }
                    ) {
                        Text("CLOSE", fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(exercise.name, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ExerciseGraphicImage(
                                exercise = exercise,
                                size = 140.dp,
                                showMuscleGlowTag = true
                            )
                        }
                    }

                    item {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Category: ${exercise.category} • Muscle: ${exercise.targetMuscle}",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonGreen,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        Text("Equipment: ${exercise.equipment} • ${exercise.sets} Sets × ${exercise.reps}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    }

                    item {
                        Text("Instructions:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(exercise.instructions, style = MaterialTheme.typography.bodyMedium)
                    }

                    item {
                        Text("Safety Tips:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NeonGreen)
                        Text(exercise.safetyTips, style = MaterialTheme.typography.bodyMedium)
                    }

                    item {
                        Text("Common Mistakes to Avoid:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(exercise.commonMistakes, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        )
    }
}
