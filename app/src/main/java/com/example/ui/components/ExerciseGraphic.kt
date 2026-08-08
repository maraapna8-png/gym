package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Exercise
import com.example.ui.theme.AccentRed
import com.example.ui.theme.NeonGreen

object ExerciseGraphicMapper {

    @DrawableRes
    fun getGraphicResId(exerciseName: String, category: String): Int {
        val nameLower = exerciseName.lowercase()
        val catLower = category.lowercase()

        return when {
            nameLower.contains("bench") || nameLower.contains("push-up") || nameLower.contains("chest") || catLower == "chest" -> {
                R.drawable.img_ex_bench_press_1785986769266
            }
            nameLower.contains("pull") || nameLower.contains("row") || nameLower.contains("lat") || nameLower.contains("back") || catLower == "back" -> {
                R.drawable.img_ex_lat_pulldown_1785986785635
            }
            nameLower.contains("squat") || nameLower.contains("leg") || nameLower.contains("lunge") || catLower == "legs" || catLower == "glutes" -> {
                R.drawable.img_ex_barbell_squat_1785986797606
            }
            nameLower.contains("curl") || nameLower.contains("bicep") || nameLower.contains("tricep") || nameLower.contains("arm") || catLower == "biceps" || catLower == "triceps" -> {
                R.drawable.img_ex_bicep_curl_1785986809622
            }
            nameLower.contains("press") || nameLower.contains("shoulder") || catLower == "shoulders" -> {
                R.drawable.img_ex_bench_press_1785986769266
            }
            nameLower.contains("plank") || nameLower.contains("abs") || catLower == "abs" -> {
                R.drawable.img_ex_bicep_curl_1785986809622
            }
            else -> {
                R.drawable.img_ex_bench_press_1785986769266
            }
        }
    }

    fun getPrimaryMuscleTag(exercise: Exercise): String {
        val target = exercise.targetMuscle.ifEmpty { exercise.category }
        val primary = target.split(",").firstOrNull()?.trim() ?: exercise.category
        return primary.uppercase()
    }
}

@Composable
fun ExerciseGraphicImage(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    showMuscleGlowTag: Boolean = true
) {
    val resId = ExerciseGraphicMapper.getGraphicResId(exercise.name, exercise.category)
    val muscleTag = ExerciseGraphicMapper.getPrimaryMuscleTag(exercise)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2228),
                        Color(0xFF111317)
                    )
                )
            )
            .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
    ) {
        // Exercise 3D Illustration
        Image(
            painter = painterResource(id = resId),
            contentDescription = "${exercise.name} graphic",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        if (showMuscleGlowTag) {
            // Muscle Red Glow Tag on top right
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp), spotColor = AccentRed)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(AccentRed)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = muscleTag.take(8),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
