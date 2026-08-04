package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

data class ProfileFeatureTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val subScreenTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndMoreScreen(viewModel: FitProViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    val featureTiles = listOf(
        ProfileFeatureTile("AI Personal Trainer", "Ask ChatGPT-style Coach", Icons.Default.Chat, NeonGreen, "AI_CHAT"),
        ProfileFeatureTile("BMI Calculator", "Body Mass Index & Advice", Icons.Default.Calculate, AccentCyan, "BMI_CALCULATOR"),
        ProfileFeatureTile("Progress & Charts", "Weight & Workout Logs", Icons.Default.Analytics, AccentOrange, "PROGRESS_CHARTS"),
        ProfileFeatureTile("Achievements & Badges", "Track Unlocked Medals", Icons.Default.EmojiEvents, Color(0xFFFFD700), "ACHIEVEMENTS"),
        ProfileFeatureTile("Admin Management", "Add & Edit Exercises", Icons.Default.AdminPanelSettings, Color(0xFFE040FB), "ADMIN_PANEL"),
        ProfileFeatureTile("Settings & Preferences", "Units, Theme & Reset", Icons.Default.Settings, Color.Gray, "SETTINGS")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profile_and_more_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.2f))
                                    .border(2.dp, NeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = userProfile.fullName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = userProfile.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonGreen,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "${userProfile.fitnessGoal.uppercase()} • ${userProfile.fitnessLevel.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = NeonGreen)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem("Age", "${userProfile.age} yrs")
                        ProfileStatItem("Height", "${userProfile.heightCm.toInt()} cm")
                        ProfileStatItem("Weight", "${userProfile.weightKg} kg")
                        ProfileStatItem("Target", "${userProfile.targetWeightKg} kg")
                    }
                }
            }
        }

        // Feature Tiles Grid Header
        item {
            Text(
                text = "EXPLORE & TOOLS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 1.sp
            )
        }

        items(featureTiles) { tile ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateToSubScreen(tile.subScreenTag) }
                    .testTag("tile_${tile.subScreenTag}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(tile.iconTint.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = tile.icon, contentDescription = tile.title, tint = tile.iconTint)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(text = tile.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = tile.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Navigate", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // Edit Profile Modal
    if (showEditProfileDialog) {
        var name by remember { mutableStateOf(userProfile.fullName) }
        var weight by remember { mutableStateOf(userProfile.weightKg.toString()) }
        var targetWeight by remember { mutableStateOf(userProfile.targetWeightKg.toString()) }
        var selectedGoal by remember { mutableStateOf(userProfile.fitnessGoal) }

        val goals = listOf("Muscle Gain", "Weight Loss", "Fat Loss", "Endurance", "Strength")

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val newW = weight.toFloatOrNull() ?: userProfile.weightKg
                        val newTW = targetWeight.toFloatOrNull() ?: userProfile.targetWeightKg
                        viewModel.updateProfile(userProfile.copy(fullName = name, weightKg = newW, targetWeightKg = newTW, fitnessGoal = selectedGoal))
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                ) {
                    Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            title = { Text("Edit Fitness Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetWeight,
                            onValueChange = { targetWeight = it },
                            label = { Text("Target (kg)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Fitness Goal:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(goals) { g ->
                            FilterChip(
                                selected = selectedGoal == g,
                                onClick = { selectedGoal = g },
                                label = { Text(g) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonGreen, selectedLabelColor = Color.Black)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
