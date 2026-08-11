package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.ui.components.AiCameraCoachModal
import com.example.ui.components.ExerciseCardItem
import com.example.ui.components.ExerciseGraphicImage
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(viewModel: FitProViewModel) {
    val exercises by viewModel.filteredExercises.collectAsState()
    val searchQuery by viewModel.exerciseSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedEquipmentFilter by remember { mutableStateOf("All Equipment") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    var selectedExerciseForDetail by remember { mutableStateOf<Exercise?>(null) }

    val categories = listOf(
        "All", "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Legs", "Abs", "Cardio"
    )

    val equipmentList = listOf("All Equipment", "Dumbbell", "Barbell", "Bodyweight", "Machine", "Cable")

    val finalFilteredExercises = remember(exercises, selectedEquipmentFilter, showFavoritesOnly) {
        exercises.filter { ex ->
            val matchEq = selectedEquipmentFilter == "All Equipment" || ex.equipment.contains(selectedEquipmentFilter, ignoreCase = true)
            val matchFav = !showFavoritesOnly || ex.isFavorite
            matchEq && matchFav
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("exercise_library_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EXERCISE LIBRARY",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Search by muscle group, equipment or form guides",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = CircleShape,
                color = NeonGreen.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = NeonGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exercise_search_input"),
            placeholder = { Text("Search exercise, muscle (Chest, Biceps, Bench)...") },
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

        // Target Muscle Group Categories Filter Chips
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

        Spacer(modifier = Modifier.height(8.dp))

        // Equipment & Favorites Sub-Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorites Only Toggle
            FilterChip(
                selected = showFavoritesOnly,
                onClick = { showFavoritesOnly = !showFavoritesOnly },
                leadingIcon = {
                    Icon(
                        imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                label = { Text("Favorites", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.error,
                    selectedLabelColor = Color.White
                )
            )

            // Equipment Selector Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(equipmentList) { eq ->
                    FilterChip(
                        selected = selectedEquipmentFilter == eq,
                        onClick = { selectedEquipmentFilter = eq },
                        label = { Text(eq, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Result Count or Filter Reset Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${finalFilteredExercises.size} Exercise${if (finalFilteredExercises.size == 1) "" else "s"} Found",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (searchQuery.isNotBlank() || selectedCategory != "All" || selectedEquipmentFilter != "All Equipment" || showFavoritesOnly) {
                TextButton(
                    onClick = {
                        viewModel.setSearchQuery("")
                        viewModel.setCategoryFilter("All")
                        selectedEquipmentFilter = "All Equipment"
                        showFavoritesOnly = false
                    }
                ) {
                    Text("Reset All Filters", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (finalFilteredExercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No exercises found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try clearing filters or adjusting your search term.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setCategoryFilter("All")
                            selectedEquipmentFilter = "All Equipment"
                            showFavoritesOnly = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("CLEAR SEARCH", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(finalFilteredExercises) { exercise ->
                    ExerciseCardItem(
                        exercise = exercise,
                        onFavoriteToggle = { viewModel.toggleFavoriteExercise(exercise) },
                        onClick = { selectedExerciseForDetail = exercise }
                    )
                }
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

    // Exercise Detail Modal / Form Execution Guide Dialog
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
                        Text("FORM COACH 📷", fontWeight = FontWeight.Bold)
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
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ExerciseGraphicImage(
                                exercise = exercise,
                                size = 150.dp,
                                showMuscleGlowTag = true
                            )
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "TARGET MUSCLE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonGreen,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "${exercise.category} • ${exercise.targetMuscle}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = exercise.equipment,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("PROPER FORM & EXECUTION", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = NeonGreen)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(exercise.instructions, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonGreen.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SAFETY & FORM TIPS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = NeonGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(exercise.safetyTips, style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentOrange.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("COMMON MISTAKES TO AVOID", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = AccentOrange)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(exercise.commonMistakes, style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }
                }
            }
        )
    }
}

