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
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.components.AiCameraCoachModal
import com.example.ui.components.CelebratoryConfettiOverlay
import com.example.ui.components.ExerciseGraphicImage
import com.example.ui.components.VoiceWorkoutLoggerModal
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
    val activeSessionNotes by viewModel.activeSessionNotes.collectAsState()

    val currentExercise = remember(currentIndex, exercises) {
        exercises.getOrNull(currentIndex % exercises.size.coerceAtLeast(1))
    }

    var showAiCameraCoach by remember { mutableStateOf(false) }
    var showVoiceLoggerModal by remember { mutableStateOf(false) }

    // Celebratory Animation State
    var showCelebration by remember { mutableStateOf(false) }
    var celebrationTitle by remember { mutableStateOf("EXERCISE COMPLETED!") }
    var celebrationSubtitle by remember { mutableStateOf("Great form & solid work! 🔥") }

    // Track completed sets for current exercise
    var completedSets by remember(currentIndex) { mutableStateOf(setOf<Int>()) }

    val minutes = timerSec / 60
    val seconds = timerSec % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    if (showAiCameraCoach && currentExercise != null) {
        AiCameraCoachModal(
            exercise = currentExercise,
            onDismiss = { showAiCameraCoach = false }
        )
    }

    if (showVoiceLoggerModal) {
        VoiceWorkoutLoggerModal(
            exerciseName = currentExercise?.name,
            loggedNotes = activeSessionNotes,
            onAddNote = { viewModel.addVoiceNoteToSession(it) },
            onDeleteNote = { viewModel.removeVoiceNoteFromSession(it) },
            onDismiss = { showVoiceLoggerModal = false }
        )
    }

    // Celebratory Pop-up Confetti Overlay
    CelebratoryConfettiOverlay(
        isVisible = showCelebration,
        title = celebrationTitle,
        subtitle = celebrationSubtitle,
        onDismiss = { showCelebration = false }
    )

    Box(modifier = Modifier.fillMaxSize()) {
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showVoiceLoggerModal = true },
                        modifier = Modifier.testTag("open_voice_logger_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Logger", tint = NeonGreen)
                    }
                    IconButton(onClick = { viewModel.triggerRestTimer(45) }) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = "Rest", tint = AccentCyan)
                    }
                }
            }

            // Timer Circle Display
            Box(
                modifier = Modifier
                    .size(170.dp)
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
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Surface(shape = RoundedCornerShape(8.dp), color = NeonGreen, contentColor = Color.Black) {
                                    Text(
                                        text = ex.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = ex.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Target: ${ex.targetMuscle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Prominent 3D exercise graphic illustration
                            ExerciseGraphicImage(
                                exercise = ex,
                                size = 90.dp,
                                showMuscleGlowTag = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Set Trackers
                        Text(
                            text = "MARK SETS COMPLETED:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..ex.sets).forEach { setNum ->
                                val isDone = completedSets.contains(setNum)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDone) NeonGreen else MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = if (isDone) NeonGreen else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clickable {
                                            if (isDone) {
                                                completedSets = completedSets - setNum
                                            } else {
                                                completedSets = completedSets + setNum
                                                celebrationTitle = "SET $setNum COMPLETED! 🎉"
                                                celebrationSubtitle = "${ex.name} • Set $setNum crushed with solid form!"
                                                showCelebration = true
                                            }
                                        }
                                        .testTag("set_chip_$setNum")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = "SET $setNum",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isDone) Color.Black else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showVoiceLoggerModal = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_voice_note_card")
                            ) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Note", tint = NeonGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("VOICE NOTE 🎙️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showAiCameraCoach = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("btn_launch_ai_camera_session")
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "AI Camera", tint = AccentCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("FORM COACH 📷", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        if (activeSessionNotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NeonGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showVoiceLoggerModal = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${activeSessionNotes.size} Voice note(s) logged: \"${activeSessionNotes.last()}\"",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonGreen,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                    }
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
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
                    onClick = {
                        val isLast = (plan != null && currentIndex >= plan!!.totalExercises - 1)
                        if (isLast) {
                            celebrationTitle = "WORKOUT COMPLETE! 🏆"
                            celebrationSubtitle = "Phenomenal workout! All exercises logged successfully."
                        } else {
                            celebrationTitle = "EXERCISE COMPLETED! 🔥"
                            celebrationSubtitle = "${currentExercise?.name ?: "Exercise"} finished! Get ready for next set."
                        }
                        showCelebration = true
                        viewModel.nextExercise()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(52.dp)
                        .testTag("next_exercise_btn")
                ) {
                    Text("MARK COMPLETE ➔", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
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
