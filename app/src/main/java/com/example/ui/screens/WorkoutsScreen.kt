package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutPlan
import com.example.ui.components.WorkoutPlanCard
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    viewModel: FitProViewModel,
    onStartWorkout: (WorkoutPlan) -> Unit
) {
    val plans by viewModel.workoutPlans.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var selectedLevelTab by remember { mutableStateOf("All") }
    var showAiGeneratorDialog by remember { mutableStateOf(false) }

    val levelTabs = listOf("All", "Beginner", "Intermediate", "Advanced")

    val filteredPlans = remember(plans, selectedLevelTab) {
        if (selectedLevelTab == "All") plans else plans.filter { it.category.equals(selectedLevelTab, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("workouts_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // AI Workout Generator Hero Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = NeonGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FITPRO AI WORKOUT GENERATOR",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generate a hyper-customized workout plan based on your equipment & available time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showAiGeneratorDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("open_ai_generator_btn")
                ) {
                    Text(text = "GENERATE", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Tabs (All, Beginner, Intermediate, Advanced)
        ScrollableTabRow(
            selectedTabIndex = levelTabs.indexOf(selectedLevelTab),
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = NeonGreen,
            divider = {}
        ) {
            levelTabs.forEach { level ->
                Tab(
                    selected = selectedLevelTab == level,
                    onClick = { selectedLevelTab = level },
                    text = {
                        Text(
                            text = level,
                            fontWeight = if (selectedLevelTab == level) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedLevelTab == level) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredPlans) { plan ->
                WorkoutPlanCard(
                    plan = plan,
                    onStart = { onStartWorkout(plan) }
                )
            }
        }
    }

    // AI Generator Dialog
    if (showAiGeneratorDialog) {
        var selectedEquipment by remember { mutableStateOf("Dumbbells & Bodyweight") }
        var selectedDuration by remember { mutableStateOf(45) }

        val equipmentList = listOf("Full Gym Barbell/Machines", "Dumbbells & Bodyweight", "Bodyweight Only", "Kettlebells & Cable")
        val durations = listOf(20, 30, 45, 60)

        AlertDialog(
            onDismissRequest = { if (!isAiLoading) showAiGeneratorDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateAiWorkoutPlan(selectedEquipment, selectedDuration)
                        showAiGeneratorDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_ai_generator")
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    } else {
                        Text("GENERATE PLAN", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiGeneratorDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Configure AI Workout", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Available Equipment:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    equipmentList.forEach { eq ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedEquipment == eq,
                                onClick = { selectedEquipment = eq },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                            )
                            Text(text = eq, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Workout Duration (Minutes):", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durations.forEach { d ->
                            FilterChip(
                                selected = selectedDuration == d,
                                onClick = { selectedDuration = d },
                                label = { Text("${d}m") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonGreen,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}
