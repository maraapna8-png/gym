package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorScreen(
    viewModel: FitProViewModel,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var heightCm by remember { mutableStateOf(userProfile.heightCm) }
    var weightKg by remember { mutableStateOf(userProfile.weightKg) }

    val bmiResult = remember(heightCm, weightKg) {
        viewModel.calculateBmi(weightKg, heightCm)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("bmi_calculator_screen")
    ) {
        TopAppBar(
            title = { Text("BMI Calculator & Advice", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // BMI Result Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOUR BODY MASS INDEX",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${bmiResult.bmiValue}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (bmiResult.category) {
                        "Normal Weight" -> NeonGreen
                        "Underweight" -> AccentCyan
                        "Overweight" -> AccentOrange
                        else -> MaterialTheme.colorScheme.error
                    },
                    contentColor = Color.Black
                ) {
                    Text(
                        text = bmiResult.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Healthy Weight Range:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${bmiResult.idealWeightMinKg} - ${bmiResult.idealWeightMaxKg} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sliders Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Height (cm):", fontWeight = FontWeight.Bold)
                    Text("${heightCm.toInt()} cm", fontWeight = FontWeight.Bold, color = NeonGreen)
                }
                Slider(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    valueRange = 120f..220f,
                    colors = SliderDefaults.colors(thumbColor = NeonGreen, activeTrackColor = NeonGreen)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Weight (kg):", fontWeight = FontWeight.Bold)
                    Text("${weightKg.toInt()} kg", fontWeight = FontWeight.Bold, color = NeonGreen)
                }
                Slider(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    valueRange = 40f..150f,
                    colors = SliderDefaults.colors(thumbColor = NeonGreen, activeTrackColor = NeonGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Personalized Advice Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "FITNESS ADVICE & RECOMMENDATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bmiResult.advice,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
