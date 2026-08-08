package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiIntelligenceCard(
    viewModel: FitProViewModel,
    modifier: Modifier = Modifier
) {
    val instantQuote by viewModel.instantMotivationQuote.collectAsState()
    val healthReport by viewModel.aiHealthReport.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingProDiagnostic.collectAsState()

    var selectedTier by remember { mutableStateOf(0) } // 0 = Flash-Lite, 1 = Flash, 2 = Pro

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gemini_intelligence_card"),
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = NeonGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GEMINI INTELLIGENCE SUITE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonGreen
                        )
                        Text(
                            text = "Multi-Model AI Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Model Selection Tabs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = selectedTier == 0,
                    onClick = { selectedTier = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = NeonGreen,
                        activeContentColor = Color.Black
                    )
                ) {
                    Text("Flash-Lite", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                SegmentedButton(
                    selected = selectedTier == 1,
                    onClick = { selectedTier = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = NeonGreen,
                        activeContentColor = Color.Black
                    )
                ) {
                    Text("3.5 Flash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                SegmentedButton(
                    selected = selectedTier == 2,
                    onClick = { selectedTier = 2 },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = NeonGreen,
                        activeContentColor = Color.Black
                    )
                ) {
                    Text("3.1 Pro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content per tier
            when (selectedTier) {
                0 -> {
                    // Flash Lite: Fast Instant Motivation & Form Tips
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Fast Tasks (gemini-3.1-flash-lite-preview)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = NeonGreen)
                                }

                                IconButton(
                                    onClick = { viewModel.fetchInstantMotivation("Bench Press") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = instantQuote,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                1 -> {
                    // 3.5 Flash: General AI Assistant & Meal Macro Calculation
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("General Tasks (gemini-3.5-flash)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = NeonGreen)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "FitPro AI Trainer & Smart Macro Estimator are actively powered by gemini-3.5-flash for balanced speed & intelligence.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { viewModel.navigateToSubScreen("AI_CHAT") },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("OPEN AI TRAINER CHAT", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                2 -> {
                    // 3.1 Pro: Complex Diagnostics
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Complex Tasks (gemini-3.1-pro-preview)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = NeonGreen)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (healthReport == null) {
                                Text(
                                    text = "Run deep multi-factorial bio-performance diagnostic, recovery score analysis, and metabolic planning.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.fetchDeepProDiagnostic() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isAnalyzing) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black)
                                    } else {
                                        Text("RUN PRO DIAGNOSTIC", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                healthReport?.let { report ->
                                    Text(text = report.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = NeonGreen)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Recovery Score: ${report.recoveryScorePct}% • CNS State: ${report.fatigueLevel}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Recommended Split: ${report.recommendedWorkoutType}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = report.metabolicAdvice, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.fetchDeepProDiagnostic() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("RE-ANALYZE WITH PRO")
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
