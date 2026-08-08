package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WaterLog
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerCard(
    waterLog: WaterLog,
    onAddWater: (Int) -> Unit,
    onSetGoal: (Int) -> Unit,
    onResetWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }

    val percentage = if (waterLog.goalMl > 0) {
        (waterLog.currentMl.toFloat() / waterLog.goalMl.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 800),
        label = "water_progress"
    )

    val hydrationStatus = when {
        percentage >= 1f -> "Goal Reached! 🎉"
        percentage >= 0.75f -> "Optimal Hydration 💧"
        percentage >= 0.50f -> "Halfway There 💪"
        percentage >= 0.25f -> "Keep Drinking 🌊"
        else -> "Start Hydrating Today!"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("water_tracker_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Title & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AccentCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water Tracker",
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DAILY WATER TRACKER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentCyan,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = hydrationStatus,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = { showGoalDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_edit_water_goal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Set Goal",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { onResetWater() },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_reset_water")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Water",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Compact Gauge & Numerical Details Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Compact Circular Gauge
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 7.dp.toPx()
                        // Track
                        drawArc(
                            color = Color(0xFF1E2E3D),
                            startAngle = 140f,
                            sweepAngle = 260f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Progress Arc
                        drawArc(
                            color = AccentCyan,
                            startAngle = 140f,
                            sweepAngle = 260f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(percentage * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "of goal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Compact Numeric Details
                val remaining = (waterLog.goalMl - waterLog.currentMl).coerceAtLeast(0)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Consumed", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${waterLog.currentMl} ml",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = AccentCyan
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Daily Goal", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${waterLog.goalMl} ml",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Remaining", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$remaining ml",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (remaining == 0) NeonGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "QUICK ADD INTAKE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Add Preset Buttons - Compact Single Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickWaterButton(
                    amount = 250,
                    label = "+250ml",
                    icon = Icons.Outlined.WaterDrop,
                    onClick = { onAddWater(250) },
                    modifier = Modifier.weight(1f).testTag("water_btn_250")
                )

                QuickWaterButton(
                    amount = 500,
                    label = "+500ml",
                    icon = Icons.Default.LocalDrink,
                    onClick = { onAddWater(500) },
                    modifier = Modifier.weight(1f).testTag("water_btn_500")
                )

                QuickWaterButton(
                    amount = 750,
                    label = "+750ml",
                    icon = Icons.Default.FitnessCenter,
                    onClick = { onAddWater(750) },
                    modifier = Modifier.weight(1f).testTag("water_btn_750")
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { showCustomDialog = true }
                        .testTag("water_btn_custom")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Custom", tint = AccentCyan, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "Custom", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AccentCyan)
                    }
                }
            }
        }
    }

    // Goal Dialog
    if (showGoalDialog) {
        var newGoalText by remember { mutableStateOf(waterLog.goalMl.toString()) }

        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Daily Water Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter your recommended daily target water intake in milliliters (ml):", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = newGoalText,
                        onValueChange = { newGoalText = it },
                        label = { Text("Goal (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(2000, 2500, 3000, 3500, 4000).forEach { preset ->
                            FilterChip(
                                selected = newGoalText == preset.toString(),
                                onClick = { newGoalText = preset.toString() },
                                label = { Text("${preset / 1000}L", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = newGoalText.toIntOrNull() ?: 3000
                        onSetGoal(parsed)
                        showGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color.Black)
                ) {
                    Text("Save Goal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Custom Intake Dialog
    if (showCustomDialog) {
        var customMlText by remember { mutableStateOf("330") }

        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Log Custom Water Intake", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Specify the exact volume of water consumed:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = customMlText,
                        onValueChange = { customMlText = it },
                        label = { Text("Volume (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = customMlText.toIntOrNull() ?: 0
                        if (parsed > 0) {
                            onAddWater(parsed)
                        }
                        showCustomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color.Black)
                ) {
                    Text("Add Water", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun QuickWaterButton(
    amount: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AccentCyan.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f)),
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = AccentCyan, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "+${amount}ml",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = AccentCyan
            )
        }
    }
}
