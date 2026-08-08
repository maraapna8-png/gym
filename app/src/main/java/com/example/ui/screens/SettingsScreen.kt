package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: FitProViewModel,
    onBack: () -> Unit
) {
    val darkTheme by viewModel.darkTheme.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Settings & Preferences", fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Theme Mode Section Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "APP DISPLAY THEME",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (darkTheme) "Dark Mode Active 🌙" else "Light Mode Active ☀️",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Switch(
                                checked = darkTheme,
                                onCheckedChange = { viewModel.toggleTheme() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonGreen),
                                modifier = Modifier.testTag("switch_dark_theme")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Explicit Light & Dark Selector Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Light Mode Option
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (!darkTheme) NeonGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (!darkTheme) 2.dp else 1.dp,
                                    color = if (!darkTheme) NeonGreen else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setDarkTheme(false) }
                                    .testTag("option_light_mode")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = "Light Mode",
                                        tint = if (!darkTheme) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Light Mode",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (!darkTheme) NeonGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Dark Mode Option
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (darkTheme) NeonGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (darkTheme) 2.dp else 1.dp,
                                    color = if (darkTheme) NeonGreen else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setDarkTheme(true) }
                                    .testTag("option_dark_mode")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DarkMode,
                                        contentDescription = "Dark Mode",
                                        tint = if (darkTheme) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Dark Mode",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (darkTheme) NeonGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Units Preference Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Measurement Units", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    if (userProfile.unitMetric) "Metric (Kilograms / Centimeters)" else "Imperial (Pounds / Feet)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            FilterChip(
                                selected = userProfile.unitMetric,
                                onClick = { viewModel.updateProfile(userProfile.copy(unitMetric = !userProfile.unitMetric)) },
                                label = { Text(if (userProfile.unitMetric) "KG / CM" else "LBS / FT") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                            )
                        }
                    }
                }
            }

            // About App Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About FitPro AI v2.0", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NeonGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Production-grade Android Gym & Fitness App powered by Jetpack Compose, Room Database, and Gemini AI.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

