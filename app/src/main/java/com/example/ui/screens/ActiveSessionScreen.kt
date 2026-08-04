package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@Composable
fun ActiveSessionScreen(viewModel: FitProViewModel) {
    val plan by viewModel.activePlan.collectAsState()
    val timerSec by viewModel.sessionTimerSeconds.collectAsState()
    val restSec by viewModel.restTimerSeconds.collectAsState()
    val currentIndex by viewModel.currentExerciseIndex.collectAsState()
    val exercises by viewModel.filteredExercises.collectAsState()

    val currentExercise = remember(currentIndex, exercises) {
        exercises.getOrNull(currentIndex % exercises.size.coerceAtLeast(1))
    }

    val minutes = timerSec / 60
    val seconds = timerSec % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("active_session_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Session Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.finishWorkoutSession() },
                modifier = Modifier.testTag("exit_session_btn")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = plan?.title ?: "WORKOUT SESSION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Exercise ${currentIndex + 1} of ${plan?.totalExercises ?: 6}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen
                )
            }

            IconButton(onClick = { viewModel.triggerRestTimer(45) }) {
                Icon(imageVector = Icons.Default.Timer, contentDescription = "Rest", tint = AccentCyan)
            }
        }

        // Timer Circle Display
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { if (restSec > 0) restSec / 45f else 1f },
                modifier = Modifier.fillMaxSize(),
                color = if (restSec > 0) AccentOrange else NeonGreen,
                strokeWidth = 10.dp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (restSec > 0) "REST" else "ELAPSED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (restSec > 0) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (restSec > 0) "${restSec}s" else timeFormatted,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        // Active Exercise Instruction Card
        currentExercise?.let { ex ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = NeonGreen, contentColor = Color.Black) {
                        Text(
                            text = ex.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = ex.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = "Target: ${ex.targetMuscle}", style = MaterialTheme.typography.bodySmall, color = AccentCyan)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = ex.instructions, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SessionPill("SETS", "${ex.sets}")
                        SessionPill("REPS", ex.reps)
                        SessionPill("REST", "45s")
                    }
                }
            }
        }

        // Bottom Action Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.triggerRestTimer(30) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("+30s REST", color = AccentCyan, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.nextExercise() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .testTag("next_exercise_btn")
            ) {
                Text("NEXT EXERCISE", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }
    }
}

@Composable
fun SessionPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NeonGreen)
    }
}
