package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FitProViewModel

@Composable
fun DashboardScreen(
    viewModel: FitProViewModel,
    onStartWorkoutPlan: (com.example.data.model.WorkoutPlan) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val waterLog by viewModel.waterLog.collectAsState()
    val stepLog by viewModel.stepLog.collectAsState()
    val workoutPlans by viewModel.workoutPlans.collectAsState()
    val workoutLogs by viewModel.workoutLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Welcome Banner
        item {
            HeroBannerCard(
                userName = userProfile.fullName,
                motto = "Push past your limits today. Consistency breeds champion strength!",
                streakDays = 7,
                onStartWorkout = {
                    workoutPlans.firstOrNull()?.let { onStartWorkoutPlan(it) }
                }
            )
        }

        // Section Title: Today's Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S METRICS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { viewModel.navigateToSubScreen("PROGRESS") }) {
                    Text("Analytics", color = NeonGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Metrics Grid Row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "Calories",
                    value = "580",
                    unit = "kcal",
                    subtitle = "Goal: 750 kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = AccentOrange,
                    progress = 580f / 750f,
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Water",
                    value = "${waterLog.currentMl}",
                    unit = "ml",
                    subtitle = "Goal: ${waterLog.goalMl} ml",
                    icon = Icons.Default.WaterDrop,
                    iconTint = AccentCyan,
                    progress = waterLog.currentMl.toFloat() / waterLog.goalMl.toFloat(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Metrics Grid Row 2
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "Steps Today",
                    value = "${stepLog.steps}",
                    unit = "steps",
                    subtitle = "Goal: ${stepLog.goalSteps} steps",
                    icon = Icons.Default.DirectionsWalk,
                    iconTint = NeonGreen,
                    progress = stepLog.steps.toFloat() / stepLog.goalSteps.toFloat(),
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Current Weight",
                    value = "${userProfile.weightKg}",
                    unit = if (userProfile.unitMetric) "kg" else "lbs",
                    subtitle = "Target: ${userProfile.targetWeightKg} ${if (userProfile.unitMetric) "kg" else "lbs"}",
                    icon = Icons.Default.MonitorWeight,
                    iconTint = Color(0xFFE040FB),
                    progress = 0.85f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Tracker Actions (Water & Steps)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUICK TRACKERS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = "Water", tint = AccentCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "+ Water Intake", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.addWaterIntake(250) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("add_water_250")
                            ) {
                                Text("+250ml", color = AccentCyan)
                            }

                            OutlinedButton(
                                onClick = { viewModel.addWaterIntake(500) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("add_water_500")
                            ) {
                                Text("+500ml", color = AccentCyan)
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = "Steps", tint = NeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "+ Log Walk/Steps", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.addSteps(1000) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_1000_steps")
                        ) {
                            Text("+1,000 Steps", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Title: Recommended Workout
        item {
            Text(
                text = "WORKOUT OF THE DAY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )
        }

        // Featured Workout Plan
        item {
            workoutPlans.firstOrNull()?.let { plan ->
                WorkoutPlanCard(
                    plan = plan,
                    onStart = { onStartWorkoutPlan(plan) }
                )
            }
        }

        // Section Title: Recent Activities
        item {
            Text(
                text = "RECENT ACTIVITIES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )
        }

        if (workoutLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No workout logs recorded yet. Complete a workout to see your progress here!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(workoutLogs.take(3)) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Completed", tint = NeonGreen)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = log.workoutTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${log.durationMinutes} min • ${log.caloriesBurned} kcal burned",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "COMPLETED",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
