package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

            // Daily Notification Reminders Scheduler Card
            item {
                val context = androidx.compose.ui.platform.LocalContext.current

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("notifications_reminders_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DAILY LOCAL REMINDERS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonGreen
                                )
                                Text(
                                    text = "Scheduled Alerts & Daily Reminders",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = NeonGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. Workout Reminder Section
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = NeonGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Workout Routine Alert 🏋️", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "Set time: ${userProfile.workoutReminderTime}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = userProfile.workoutReminderEnabled,
                                        onCheckedChange = { isChecked ->
                                            val updated = userProfile.copy(workoutReminderEnabled = isChecked)
                                            viewModel.updateProfile(updated)
                                            if (isChecked) {
                                                val parts = userProfile.workoutReminderTime.split(":")
                                                val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
                                                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                                com.example.util.FitProNotificationScheduler.scheduleDailyReminder(context, "workout", h, m)
                                                android.widget.Toast.makeText(context, "Daily workout reminder set for ${userProfile.workoutReminderTime}", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                com.example.util.FitProNotificationScheduler.cancelReminder(context, "workout")
                                                android.widget.Toast.makeText(context, "Workout reminder canceled", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonGreen),
                                        modifier = Modifier.testTag("switch_workout_reminder")
                                    )
                                }

                                if (userProfile.workoutReminderEnabled) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Select Workout Reminder Time:", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("07:00", "08:00", "17:00", "19:00").forEach { time ->
                                            FilterChip(
                                                selected = userProfile.workoutReminderTime == time,
                                                onClick = {
                                                    val updated = userProfile.copy(workoutReminderTime = time)
                                                    viewModel.updateProfile(updated)
                                                    val parts = time.split(":")
                                                    val h = parts[0].toInt()
                                                    val m = parts[1].toInt()
                                                    com.example.util.FitProNotificationScheduler.scheduleDailyReminder(context, "workout", h, m)
                                                    android.widget.Toast.makeText(context, "Workout reminder set for $time", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                label = { Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Meal & Water Reminder Section
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = com.example.ui.theme.AccentOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Meal & Water Logging Alert 🥗", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                "Set time: ${userProfile.mealReminderTime}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = userProfile.mealReminderEnabled,
                                        onCheckedChange = { isChecked ->
                                            val updated = userProfile.copy(mealReminderEnabled = isChecked)
                                            viewModel.updateProfile(updated)
                                            if (isChecked) {
                                                val parts = userProfile.mealReminderTime.split(":")
                                                val h = parts.getOrNull(0)?.toIntOrNull() ?: 13
                                                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                                com.example.util.FitProNotificationScheduler.scheduleDailyReminder(context, "meal", h, m)
                                                android.widget.Toast.makeText(context, "Daily meal reminder set for ${userProfile.mealReminderTime}", android.widget.Toast.LENGTH_SHORT).show()
                                            } else {
                                                com.example.util.FitProNotificationScheduler.cancelReminder(context, "meal")
                                                android.widget.Toast.makeText(context, "Meal reminder canceled", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = com.example.ui.theme.AccentOrange),
                                        modifier = Modifier.testTag("switch_meal_reminder")
                                    )
                                }

                                if (userProfile.mealReminderEnabled) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Select Meal Reminder Time:", style = MaterialTheme.typography.labelSmall, color = com.example.ui.theme.AccentOrange)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("12:00", "13:00", "18:00", "20:00").forEach { time ->
                                            FilterChip(
                                                selected = userProfile.mealReminderTime == time,
                                                onClick = {
                                                    val updated = userProfile.copy(mealReminderTime = time)
                                                    viewModel.updateProfile(updated)
                                                    val parts = time.split(":")
                                                    val h = parts[0].toInt()
                                                    val m = parts[1].toInt()
                                                    com.example.util.FitProNotificationScheduler.scheduleDailyReminder(context, "meal", h, m)
                                                    android.widget.Toast.makeText(context, "Meal reminder set for $time", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                label = { Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = com.example.ui.theme.AccentOrange, selectedLabelColor = Color.White)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Test Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    com.example.util.FitProNotificationScheduler.triggerInstantTestNotification(context, "workout")
                                    android.widget.Toast.makeText(context, "Triggered test workout notification 🔔", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).testTag("btn_test_workout_notif"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("TEST WORKOUT 🔔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                            }

                            OutlinedButton(
                                onClick = {
                                    com.example.util.FitProNotificationScheduler.triggerInstantTestNotification(context, "meal")
                                    android.widget.Toast.makeText(context, "Triggered test meal notification 🥗", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f).testTag("btn_test_meal_notif"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("TEST MEAL 🥗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.AccentOrange)
                            }
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

