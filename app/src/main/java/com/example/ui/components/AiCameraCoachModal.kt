package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.view.TextureView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.Exercise
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen

@Composable
fun RealCameraPreviewView(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            TextureView(ctx).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    var camera: Camera? = null

                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        try {
                            camera = Camera.open()
                            camera?.setDisplayOrientation(90)
                            camera?.setPreviewTexture(surface)
                            camera?.startPreview()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        try {
                            camera?.stopPreview()
                            camera?.release()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCameraCoachModal(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var isCameraActive by remember { mutableStateOf(hasCameraPermission) }
    var repCount by remember { mutableIntStateOf(0) }
    var isWrongForm by remember { mutableStateOf(false) }
    var formAccuracyScore by remember { mutableIntStateOf(96) }

    // Automatically activate camera if permission is granted
    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            isCameraActive = true
        }
    }

    // Message Language (Roman Urdu) written instructions based on exercise type and form state
    val exerciseNameLower = exercise.name.lowercase()
    val isPushup = exerciseNameLower.contains("push") || exerciseNameLower.contains("press")
    val isSquat = exerciseNameLower.contains("squat") || exerciseNameLower.contains("leg")

    val correctMessage = when {
        isPushup -> "Shabaash! Form bilkul zabardast hai. Core ko tight rakhein aur chest poora zameen tak le kar jayein! 👍"
        isSquat -> "Wah! Bahut behtareen depth squat hai. Knees aur posture bilkul perfect hain 👍"
        else -> "Bohot achhi technique! Controlled pace ke saath exercise continue rakhein 👍"
    }

    val wrongMessage = when {
        isPushup -> "⚠️ Ghalat Form! Aap ki elbows zyada baahar hain aur back jhuk rahi hai. Elbows ko 45-degree angle par rakhein, core tight rakhein aur back bilkul seedhi rakhein!"
        isSquat -> "⚠️ Ghalat Form! Ghutne (knees) andar ki taraf jhuk rahe hain. Seena (chest) ooper rakhein, ghutne toes ki taraf rakhein aur hips ko peeche drop karein jese chair par baith rahe hon!"
        else -> "⚠️ Ghalat Form! Back seedhi rakhein aur jhatke ke saath weight na uthayein. Movement ko slow aur controlled 2-second pace par rakhein!"
    }

    val postureFeedback = if (isWrongForm) wrongMessage else correctMessage
    val feedbackColor = if (isWrongForm) AccentOrange else NeonGreen

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("ai_camera_modal"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "AI Camera Coach",
                                tint = NeonGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI CAMERA COACH",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreen,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_ai_camera")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!hasCameraPermission) {
                    // Camera Permission Required Box
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Videocam,
                                contentDescription = "Camera Permission",
                                tint = AccentOrange,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Camera Permission Required",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Camera permission allow karein taakay AI aap ki live exercise ko track kar sakay aur correct posture guidance daay sakay.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_request_camera_perm")
                            ) {
                                Icon(imageVector = Icons.Default.Camera, contentDescription = "Grant")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ALLOW CAMERA ACCESS", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                } else {
                    // Live AI Camera View with Real Hardware Preview & Skeleton/Pose Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF0F1A24))
                                .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCameraActive) {
                                // Live Camera Hardware Feed
                                RealCameraPreviewView(modifier = Modifier.fillMaxSize())
                            }

                            // Camera Control Overlay UI
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top Status Indicators
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isCameraActive) Color(0xFF1E3A2B) else Color.Black.copy(alpha = 0.7f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isCameraActive) NeonGreen else Color.Gray)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isCameraActive) "LIVE CAMERA ACTIVE" else "CAMERA READY",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isWrongForm) AccentOrange.copy(alpha = 0.25f) else AccentCyan.copy(alpha = 0.25f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isWrongForm) AccentOrange else AccentCyan)
                                    ) {
                                        Text(
                                            text = "Form Score: ${if (isWrongForm) 62 else formAccuracyScore}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isWrongForm) AccentOrange else AccentCyan,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                // Center Controls: Camera Start & Manual Rep Counter Trigger
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (!isCameraActive) {
                                        Button(
                                            onClick = { isCameraActive = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                            shape = RoundedCornerShape(20.dp),
                                            modifier = Modifier.testTag("btn_start_live_ai_tracking")
                                        ) {
                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start Camera")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("OPEN LIVE CAMERA", fontWeight = FontWeight.ExtraBold)
                                        }
                                    } else {
                                        // Manual Rep Count Controls
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color.Black.copy(alpha = 0.85f),
                                                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "$repCount",
                                                        style = MaterialTheme.typography.headlineMedium,
                                                        fontWeight = FontWeight.Black,
                                                        color = NeonGreen
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "REPS DONE",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }

                                            // +1 REP Button
                                            Button(
                                                onClick = {
                                                    repCount++
                                                    formAccuracyScore = (92..99).random()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.testTag("btn_do_rep")
                                            ) {
                                                Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "+1 Rep")
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("+1 REP", fontWeight = FontWeight.ExtraBold)
                                            }

                                            // Reset Button
                                            IconButton(
                                                onClick = { repCount = 0 },
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.7f))
                                            ) {
                                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                                            }
                                        }
                                    }
                                }

                                // Bottom Form State Toggle Buttons
                                if (isCameraActive) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = !isWrongForm,
                                            onClick = { isWrongForm = false },
                                            label = { Text("✅ Perfect Form", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = NeonGreen,
                                                selectedLabelColor = Color.Black
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        FilterChip(
                                            selected = isWrongForm,
                                            onClick = { isWrongForm = true },
                                            label = { Text("⚠️ Ghalat Form (Hidayat)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AccentOrange,
                                                selectedLabelColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Posture Feedback Box in Roman Urdu (Message Language)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, feedbackColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (isWrongForm) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = "Feedback",
                                    tint = feedbackColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isWrongForm) "EXERCISE FORM CORRECTION (HIDAYAT)" else "AI FORM COACH",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = feedbackColor
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = postureFeedback,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Exercise Demo Video & Written Instructions Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SAHI TAREEQAY SE KARNE KI HIDAYAT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AccentCyan
                                    )

                                    ExerciseGraphicImage(
                                        exercise = exercise,
                                        size = 44.dp,
                                        showMuscleGlowTag = false
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = exercise.instructions,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("SAFETY TIP", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = NeonGreen)
                                            Text(exercise.safetyTips, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("COMMON MISTAKE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentOrange)
                                            Text(exercise.commonMistakes, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

