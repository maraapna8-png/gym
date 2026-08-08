package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
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
import com.example.data.model.GeneratedMusicTrack
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMusicGeneratorCard(
    viewModel: FitProViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.generatedTracks.collectAsState()
    val currentTrack by viewModel.currentPlayingTrack.collectAsState()
    val isPlaying by viewModel.isPlayingMusic.collectAsState()
    val isGenerating by viewModel.isGeneratingMusic.collectAsState()

    var showGenerateModal by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_music_generator_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = NeonGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI WORKOUT MUSIC GENERATOR",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonGreen
                        )
                        Text(
                            text = "Powered by Lyria 3 Models",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showGenerateModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("create_music_btn")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "GENERATE", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Currently Playing Track Audio Player Bar
            currentTrack?.let { track ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated Equalizer or Play/Pause Button
                        IconButton(
                            onClick = { viewModel.toggleMusicPlayback() },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = track.title,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = track.modelUsed,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${track.genre} • ${track.bpm} BPM • ${track.durationSeconds}s",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Animated Equalizer bars when playing
                            if (isPlaying) {
                                Spacer(modifier = Modifier.height(6.dp))
                                AnimatedEqualizerBars()
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet / Dialog to Generate Music
    if (showGenerateModal) {
        var selectedGenre by remember { mutableStateOf("Synthwave") }
        var isShortClip by remember { mutableStateOf(true) } // true = lyria-3-clip-preview (up to 30s), false = lyria-3-pro-preview (full track)
        var customPrompt by remember { mutableStateOf("") }

        val genres = listOf("Synthwave", "HIIT", "Heavy Metal", "Cyberpunk", "Yoga Chill")

        AlertDialog(
            onDismissRequest = { if (!isGenerating) showGenerateModal = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateWorkoutMusic(
                            prompt = if (customPrompt.isBlank()) "High energy $selectedGenre workout pump beat" else customPrompt,
                            genre = selectedGenre,
                            isShortClip = isShortClip
                        )
                        showGenerateModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_generate_music")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                    } else {
                        Text("GENERATE TRACK", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateModal = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Music Generator", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("1. Select Music Model & Format:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isShortClip,
                            onClick = { isShortClip = true },
                            label = { Text("Short Clip (30s)") },
                            trailingIcon = { Text("lyria-3-clip-preview", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isShortClip) Color.Black else NeonGreen) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                        )

                        FilterChip(
                            selected = !isShortClip,
                            onClick = { isShortClip = false },
                            label = { Text("Full Track (3m)") },
                            trailingIcon = { Text("lyria-3-pro-preview", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (!isShortClip) Color.Black else NeonGreen) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                        )
                    }

                    Text("2. Workout Genre Preset:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genres.take(3).forEach { g ->
                            FilterChip(
                                selected = selectedGenre == g,
                                onClick = { selectedGenre = g },
                                label = { Text(g, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genres.drop(3).forEach { g ->
                            FilterChip(
                                selected = selectedGenre == g,
                                onClick = { selectedGenre = g },
                                label = { Text(g, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("3. Custom Music Style Prompt (Optional):", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = customPrompt,
                        onValueChange = { customPrompt = it },
                        placeholder = { Text("e.g., 140 BPM heavy bass drops for deadlifts") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun AnimatedEqualizerBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    val heights = listOf(
        infiniteTransition.animateFloat(
            initialValue = 4f, targetValue = 18f,
            animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse), label = "h1"
        ),
        infiniteTransition.animateFloat(
            initialValue = 16f, targetValue = 6f,
            animationSpec = infiniteRepeatable(tween(250, easing = LinearEasing), RepeatMode.Reverse), label = "h2"
        ),
        infiniteTransition.animateFloat(
            initialValue = 8f, targetValue = 20f,
            animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "h3"
        ),
        infiniteTransition.animateFloat(
            initialValue = 18f, targetValue = 4f,
            animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse), label = "h4"
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(20.dp)
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(h.value.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NeonGreen)
            )
        }
    }
}
