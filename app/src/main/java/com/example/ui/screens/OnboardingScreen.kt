package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.example.data.GymEquipmentData
import com.example.data.model.GymAppliance
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: FitProViewModel,
    onFinishOnboarding: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) } // 1: Bio Metrics, 2: Training Goal

    var nameText by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var targetWeightText by remember { mutableStateOf("") }

    var selectedGoal by remember(userProfile) { mutableStateOf(userProfile.fitnessGoal) }
    var selectedMuscleFocus by remember(userProfile) { mutableStateOf(userProfile.selectedMuscleFocus) }

    var workoutLocation by remember(userProfile) { mutableStateOf(userProfile.workoutLocation) } // "Gym" or "Home"

    val selectedEquipmentIds = remember {
        mutableStateListOf<String>().apply {
            val initial = if (userProfile.selectedEquipmentCsv.isNotEmpty()) {
                userProfile.selectedEquipmentCsv.split(",")
            } else {
                GymEquipmentData.ALL_20_EQUIPMENT.filter { it.isSelectedByDefault }.map { it.name }
            }
            addAll(initial)
        }
    }

    val goalsList = listOf(
        "General Fitness" to "Improve endurance, daily energy & tone",
        "Full Muscle Gain" to "Maximum hypertrophy and muscle size",
        "Train Specific Muscle" to "Focus on weak points (Chest, Arms, Abs, etc.)",
        "Full Body Training" to "Balanced strength across all muscle groups",
        "Six Pack Abs" to "Core isolation and lower bodyfat definition",
        "Weight Loss" to "Calorie burning & fat loss routines"
    )

    val muscleList = listOf("Chest", "Biceps", "Back", "Legs", "Abs", "Shoulders", "Triceps", "Full Body")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FITPRO PERSONALIZATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonGreen,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (currentStep == 1) "Step 1: Your Body Metrics" else "Step 2: Choose Your Fitness Goal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (currentStep > 1) {
                        IconButton(onClick = { currentStep-- }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 10.dp,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(start = 24.dp, end = 24.dp, top = 22.dp, bottom = 42.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step $currentStep of 2",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            if (currentStep < 2) {
                                currentStep++
                            } else {
                                // Save onboarding setup
                                val age = ageText.toIntOrNull() ?: 18
                                val height = heightText.toFloatOrNull() ?: 178f
                                val weight = weightText.toFloatOrNull() ?: 75f
                                val targetWeight = targetWeightText.toFloatOrNull() ?: 70f
                                val equipCsv = selectedEquipmentIds.joinToString(",")

                                viewModel.updateOnboardingProfile(
                                    fullName = if (nameText.isNotBlank()) nameText else "Abdullah",
                                    age = age,
                                    heightCm = height,
                                    weightKg = weight,
                                    targetWeightKg = targetWeight,
                                    fitnessGoal = selectedGoal,
                                    muscleFocus = selectedMuscleFocus,
                                    location = "Gym",
                                    equipmentCsv = equipCsv
                                )
                                onFinishOnboarding()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("btn_onboarding_next")
                    ) {
                        Text(
                            text = if (currentStep == 2) "START WORKOUT PLAN 🚀" else "NEXT STEP ➔",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Step Progress Bar
            LinearProgressIndicator(
                progress = currentStep / 2f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = NeonGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (currentStep) {
                1 -> StepBioMetrics(
                    nameText = nameText, onNameChange = { nameText = it },
                    ageText = ageText, onAgeChange = { ageText = it },
                    heightText = heightText, onHeightChange = { heightText = it },
                    weightText = weightText, onWeightChange = { weightText = it },
                    targetWeightText = targetWeightText, onTargetWeightChange = { targetWeightText = it }
                )
                else -> StepGoalSelection(
                    goals = goalsList,
                    selectedGoal = selectedGoal,
                    onGoalSelect = { selectedGoal = it },
                    muscles = muscleList,
                    selectedMuscle = selectedMuscleFocus,
                    onMuscleSelect = { selectedMuscleFocus = it },
                    weightText = weightText,
                    targetWeightText = targetWeightText,
                    onTargetWeightChange = { targetWeightText = it }
                )
            }
        }
    }
}

@Composable
fun StepBioMetrics(
    nameText: String, onNameChange: (String) -> Unit,
    ageText: String, onAgeChange: (String) -> Unit,
    heightText: String, onHeightChange: (String) -> Unit,
    weightText: String, onWeightChange: (String) -> Unit,
    targetWeightText: String, onTargetWeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Welcome! Tell Us About Yourself",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Enter your name and physical stats to personalize your FitPro experience, profile page, and AI training plan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Name Input
                OutlinedTextField(
                    value = nameText,
                    onValueChange = onNameChange,
                    label = { Text("Your Full Name") },
                    placeholder = { Text("Abdullah") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_onboarding_name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Age Input
                OutlinedTextField(
                    value = ageText,
                    onValueChange = onAgeChange,
                    label = { Text("Your Age (Years)") },
                    placeholder = { Text("18") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Cake, contentDescription = "Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_onboarding_age")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Height Input
                OutlinedTextField(
                    value = heightText,
                    onValueChange = onHeightChange,
                    label = { Text("Height (cm)") },
                    placeholder = { Text("178") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Height, contentDescription = "Height") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_onboarding_height")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Weight Inputs Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = onWeightChange,
                        label = { Text("Current Weight (kg)") },
                        placeholder = { Text("75") },
                        leadingIcon = { Icon(imageVector = Icons.Default.MonitorWeight, contentDescription = "Current Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_onboarding_weight")
                    )

                    OutlinedTextField(
                        value = targetWeightText,
                        onValueChange = onTargetWeightChange,
                        label = { Text("Target Weight (kg)") },
                        placeholder = { Text("70") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Flag, contentDescription = "Target Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_onboarding_target_weight")
                    )
                }

                val currentW = weightText.toFloatOrNull() ?: 70f
                val targetW = targetWeightText.toFloatOrNull() ?: 65f
                val diff = currentW - targetW

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = "Target", tint = NeonGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("YOUR TARGET WEIGHT GOAL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = NeonGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when {
                                diff > 0 -> "🔥 Target Weight Loss: ${String.format("%.1f", diff)} kg to lose ($currentW kg ➔ $targetW kg)"
                                diff < 0 -> "💪 Target Weight Gain: ${String.format("%.1f", -diff)} kg to gain ($currentW kg ➔ $targetW kg)"
                                else -> "⚡ Maintenance Target: Stay at $currentW kg"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (diff > 0) "Expected timeline: ~${(diff / 0.75f).toInt().coerceAtLeast(1)} weeks with a 60-min daily routine!" else "Your plan will optimize hypertrophy and caloric intake for your goal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepGoalSelection(
    goals: List<Pair<String, String>>,
    selectedGoal: String,
    onGoalSelect: (String) -> Unit,
    muscles: List<String>,
    selectedMuscle: String,
    onMuscleSelect: (String) -> Unit,
    weightText: String,
    targetWeightText: String,
    onTargetWeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "How do you want to train your body?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Choose your primary transformation target to generate your daily 1-hour workout routines.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Target Weight Loss Adjustment Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "🎯 TARGET WEIGHT & LOSS GOAL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val currentW = weightText.toFloatOrNull() ?: 70f
                val targetW = targetWeightText.toFloatOrNull() ?: 65f
                val lossKg = (currentW - targetW).coerceAtLeast(0f)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = targetWeightText,
                        onValueChange = onTargetWeightChange,
                        label = { Text("Desired Target Weight (kg)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Flag, contentDescription = "Target Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_goal_target_weight")
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeonGreen.copy(alpha = 0.2f),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("TARGET LOSS", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
                            Text("${String.format("%.1f", lossKg)} kg", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        goals.forEach { (goalTitle, goalDesc) ->
            val isSelected = selectedGoal == goalTitle
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) NeonGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) NeonGreen else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGoalSelect(goalTitle) }
                    .testTag("goal_item_$goalTitle")
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onGoalSelect(goalTitle) },
                        colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goalTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = goalDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // If Specific Muscle requested
        if (selectedGoal == "Train Specific Muscle") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SELECT TARGET MUSCLE GROUP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        muscles.forEach { muscle ->
                            val active = selectedMuscle == muscle
                            FilterChip(
                                selected = active,
                                onClick = { onMuscleSelect(muscle) },
                                label = { Text(muscle, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentCyan,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepEquipmentSelection(
    selectedEquipment: MutableList<String>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SELECT GYM APPLIANCES (${selectedEquipment.size}/20 Selected)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = NeonGreen
            )

            TextButton(
                onClick = {
                    if (selectedEquipment.size == GymEquipmentData.ALL_20_EQUIPMENT.size) {
                        selectedEquipment.clear()
                    } else {
                        selectedEquipment.clear()
                        selectedEquipment.addAll(GymEquipmentData.ALL_20_EQUIPMENT.map { it.name })
                    }
                }
            ) {
                Text("Select All / Reset", fontSize = 12.sp, color = AccentCyan)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(GymEquipmentData.ALL_20_EQUIPMENT) { appliance ->
                val isChecked = selectedEquipment.contains(appliance.name)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isChecked) 2.dp else 1.dp,
                        color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isChecked) {
                                selectedEquipment.remove(appliance.name)
                            } else {
                                selectedEquipment.add(appliance.name)
                            }
                        }
                        .testTag("equip_item_${appliance.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getEquipmentIcon(appliance.id),
                                contentDescription = appliance.name,
                                tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )

                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) selectedEquipment.add(appliance.name)
                                    else selectedEquipment.remove(appliance.name)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, checkmarkColor = MaterialTheme.colorScheme.onPrimary)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = appliance.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        Text(
                            text = appliance.targetMuscles,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepSummaryAndSchedule(
    name: String = "",
    age: Int,
    height: Float,
    weight: Float,
    targetWeight: Float,
    goal: String,
    muscle: String,
    location: String,
    equipmentCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "YOUR PERSONALIZED FITPRO PLAN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonGreen,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "1-Hour Daily Weekly Training Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (name.isNotBlank()) {
                    SummaryRow("User Name", name)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryMetric("Age / Bio", "$age Yrs")
                    SummaryMetric("Stats", "${height.toInt()}cm / ${weight.toInt()}kg")
                    SummaryMetric("Location", location)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Spacer(modifier = Modifier.height(12.dp))

                val diff = weight - targetWeight
                SummaryRow("Target Goal", goal)
                SummaryRow("Target Weight", "${targetWeight.toInt()} kg")
                if (diff > 0) {
                    SummaryRow("Weight Loss Goal", "🔥 ${String.format("%.1f", diff)} kg to lose")
                } else if (diff < 0) {
                    SummaryRow("Weight Gain Goal", "💪 ${String.format("%.1f", -diff)} kg to gain")
                }
                if (goal == "Train Specific Muscle") {
                    SummaryRow("Focus Area", muscle)
                }
                SummaryRow(
                    "Equipment Access",
                    if (location == "Gym") "$equipmentCount Gym Appliances Selected" else "Zero Equipment Bodyweight"
                )
                SummaryRow("Daily Schedule", "60 Minutes / Day (7 Days / Week)")
            }
        }

        Text(
            text = "PREVIEW 7-DAY WORKOUT SCHEDULE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val sampleSchedule = listOf(
            "Day 1" to "60 Min Upper Body Hypertrophy & Pressing",
            "Day 2" to "60 Min Leg Strength & Quad Power",
            "Day 3" to "60 Min Back V-Taper & Core Stabilization",
            "Day 4" to "60 Min Arm Conditioning & Bicep Peak",
            "Day 5" to "60 Min Full Body Metabolic Calorie Burn",
            "Day 6" to "60 Min Shoulder & Abs Sculpting",
            "Day 7" to "60 Min Active Recovery, Foam Rolling & Stretch"
        )

        sampleSchedule.forEach { (day, title) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = day, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun SummaryMetric(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentCyan)
    }
}

fun getEquipmentIcon(id: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (id) {
        "dumbbell", "ez_bar", "barbell", "weight_plates", "kettlebell" -> Icons.Default.FitnessCenter
        "cable_machine", "smith_machine", "leg_press", "pec_deck", "lat_pulldown" -> Icons.Default.Build
        "pullup_bar", "ab_roller", "preacher_bench", "adjustable_bench" -> Icons.Default.Accessibility
        "treadmill", "exercise_bike", "rowing_machine" -> Icons.Default.DirectionsRun
        "resistance_bands", "battle_ropes", "foam_roller" -> Icons.Default.SelfImprovement
        else -> Icons.Default.FitnessCenter
    }
}
