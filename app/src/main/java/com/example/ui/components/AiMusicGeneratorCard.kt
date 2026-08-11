package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
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
    val uriHandler = LocalUriHandler.current
    val tracks by viewModel.generatedTracks.collectAsState()
    val currentTrack by viewModel.currentPlayingTrack.collectAsState()
    val isPlaying by viewModel.isPlayingMusic.collectAsState()
    val isGenerating by viewModel.isGeneratingMusic.collectAsState()

    var showGenerateModal by remember { mutableStateOf(false) }
    var showYouTubeModal by remember { mutableStateOf(false) }

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
                            text = "Lyria 3 Models & Custom Gym Beats",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { showYouTubeModal = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f))
                    ) {
                        Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+ YouTube", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showGenerateModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("create_music_btn")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "GENERATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Play / Pause Button
                            IconButton(
                                onClick = {
                                    viewModel.toggleMusicPlayback()
                                    track.youtubeUrl?.let { url ->
                                        try { uriHandler.openUri(url) } catch (_: Exception) {}
                                    }
                                },
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
                                        color = if (track.youtubeUrl != null) Color(0xFFFF0000).copy(alpha = 0.2f) else NeonGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = track.modelUsed,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (track.youtubeUrl != null) Color(0xFFFF4D4D) else NeonGreen,
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

                                if (isPlaying) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    AnimatedEqualizerBars()
                                }
                            }
                        }

                        // YouTube Action Button if YouTube URL present
                        track.youtubeUrl?.let { url ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.playTrack(track)
                                    try { uriHandler.openUri(url) } catch (_: Exception) {}
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000), contentColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("OPEN SONG ON YOUTUBE 📺", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Playlist / Featured Tracks
            if (tracks.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "WORKOUT TRACKS & PLAYLIST:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tracks.forEach { tr ->
                        val isSelected = tr.id == currentTrack?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.playTrack(tr)
                                    tr.youtubeUrl?.let { url ->
                                        try { uriHandler.openUri(url) } catch (_: Exception) {}
                                    }
                                },
                            color = if (isSelected) NeonGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (tr.youtubeUrl != null) Icons.Default.VideoLibrary else Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (tr.youtubeUrl != null) Color(0xFFFF4D4D) else NeonGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = tr.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${tr.genre} • ${tr.bpm} BPM",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (tr.youtubeUrl != null) {
                                    IconButton(
                                        onClick = {
                                            viewModel.playTrack(tr)
                                            try { uriHandler.openUri(tr.youtubeUrl) } catch (_: Exception) {}
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInNew,
                                            contentDescription = "Open YouTube",
                                            tint = Color(0xFFFF4D4D),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
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
        var isShortClip by remember { mutableStateOf(true) }
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

    // Modal to add custom YouTube Track
    if (showYouTubeModal) {
        var ytTitle by remember { mutableStateOf("Ultimate Gym Motivation Beat ⚡") }
        var ytUrl by remember { mutableStateOf("https://www.youtube.com/watch?v=qk5cd5_eLdc&list=PPSV&t=4s") }

        AlertDialog(
            onDismissRequest = { showYouTubeModal = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (ytUrl.isNotBlank()) {
                            viewModel.addCustomYouTubeTrack(ytTitle, ytUrl)
                            showYouTubeModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000), contentColor = Color.White)
                ) {
                    Text("ADD TO PLAYLIST", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showYouTubeModal = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFFFF0000))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add YouTube Workout Song", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Song Title:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = ytTitle,
                        onValueChange = { ytTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("YouTube Video / Song URL:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = ytUrl,
                        onValueChange = { ytUrl = it },
                        placeholder = { Text("https://www.youtube.com/watch?v=...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
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
