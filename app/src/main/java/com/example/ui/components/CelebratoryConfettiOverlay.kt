package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val angle: Double,
    val speed: Float,
    val size: Float,
    val color: Color,
    val isCircle: Boolean,
    val rotation: Float
)

@Composable
fun CelebratoryConfettiOverlay(
    isVisible: Boolean,
    title: String = "EXERCISE COMPLETED!",
    subtitle: String = "Awesome effort! Keep pushing your limits 🔥",
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val scaleAnim = remember { Animatable(0f) }
    val confettiProgress = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0.8f) }
    val rippleAlpha = remember { Animatable(1f) }

    // Generate random confetti particles
    val particles = remember {
        val colors = listOf(NeonGreen, AccentCyan, AccentOrange, AccentRed, AccentPurple, AccentGold, Color.White)
        List(55) {
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val speed = Random.nextFloat() * 0.45f + 0.15f
            val size = Random.nextFloat() * 12f + 8f
            val color = colors[Random.nextInt(colors.size)]
            val isCircle = Random.nextBoolean()
            val rotation = Random.nextFloat() * 360f
            ConfettiParticle(angle, speed, size, color, isCircle, rotation)
        }
    }

    LaunchedEffect(isVisible) {
        launch {
            scaleAnim.snapTo(0f)
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            confettiProgress.snapTo(0f)
            confettiProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
            )
        }
        launch {
            rippleScale.animateTo(
                targetValue = 1.6f,
                animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
            )
        }
        launch {
            rippleAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
            )
        }

        // Auto dismiss after 2.2 seconds
        delay(2200)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onDismiss() }
            .testTag("celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Confetti Canvas Particle Burst
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val progress = confettiProgress.value

            particles.forEach { p ->
                val dist = p.speed * progress * size.width * 0.8f
                val currentX = centerX + (cos(p.angle) * dist).toFloat()
                // Gravity effect
                val gravity = progress * progress * 400f
                val currentY = centerY + (sin(p.angle) * dist).toFloat() + gravity

                val alpha = (1f - progress * 0.85f).coerceIn(0f, 1f)

                if (p.isCircle) {
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size,
                        center = Offset(currentX, currentY)
                    )
                } else {
                    val halfSize = p.size
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(currentX - halfSize, currentY - halfSize),
                        size = androidx.compose.ui.geometry.Size(p.size * 1.5f, p.size)
                    )
                }
            }
        }

        // Central Pop-up Celebration Badge
        Column(
            modifier = Modifier
                .scale(scaleAnim.value)
                .padding(24.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, NeonGreen, RoundedCornerShape(28.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsing Checkmark Ring
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(rippleScale.value)
                        .clip(CircleShape)
                        .background(NeonGreen.copy(alpha = rippleAlpha.value * 0.4f))
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NeonGreen)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.Black,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = NeonGreen,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp)
                    .testTag("celebration_continue_btn")
            ) {
                Text(
                    text = "CONTINUE ➔",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
