package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WaterTrackerCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel
import com.example.util.MealNutritionEstimator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(viewModel: FitProViewModel) {
    val meals by viewModel.dietMeals.collectAsState()
    val selectedDietPlan by viewModel.selectedDietPlan.collectAsState()
    val waterLog by viewModel.waterLog.collectAsState()

    val consumedCalories by viewModel.consumedCalories.collectAsState()
    val consumedProtein by viewModel.consumedProtein.collectAsState()
    val consumedCarbs by viewModel.consumedCarbs.collectAsState()
    val consumedFat by viewModel.consumedFat.collectAsState()

    val calculatedMealResult by viewModel.calculatedMealResult.collectAsState()
    val isCalculatingMeal by viewModel.isCalculatingMeal.collectAsState()
    var mealSearchInput by remember { mutableStateOf("") }

    val mealToastMessage by viewModel.mealToastMessage.collectAsState()

    val dietPlans = listOf("All", "Muscle Gain", "Weight Loss", "Keto")

    var showCustomMealDialog by remember { mutableStateOf(false) }

    // Auto clear toast after 3s
    LaunchedEffect(mealToastMessage) {
        if (mealToastMessage != null) {
            delay(3000)
            viewModel.clearMealToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("diet_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            // Macro Breakdown Summary Card
            item {
                Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TODAY'S NUTRITION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$consumedCalories / 2,400 kcal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { showCustomMealDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_log_custom_meal")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Custom", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Custom", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroBarItem("Protein", "180g", "${consumedProtein}g", NeonGreen, consumedProtein.toFloat() / 180f, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        MacroBarItem("Carbs", "250g", "${consumedCarbs}g", AccentCyan, consumedCarbs.toFloat() / 250f, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        MacroBarItem("Fats", "65g", "${consumedFat}g", AccentOrange, consumedFat.toFloat() / 65f, Modifier.weight(1f))
                    }
                }
            }
        }

        // Daily Water Tracker Card
            item {
                WaterTrackerCard(
                    waterLog = waterLog,
                    onAddWater = { viewModel.addWaterIntake(it) },
                    onSetGoal = { viewModel.setWaterGoal(it) },
                    onResetWater = { viewModel.resetWaterIntake() }
                )
            }

            // AI Meal Macro Calculator Card (Protein, Fat, Calories Estimator)
            item {
                Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Macro Calculator",
                                tint = NeonGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MEAL MACRO CALCULATOR",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "P / F / KCAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter any meal name (e.g. '2 boiled eggs & 1 chapati', 'chicken biryani 200g', 'oatmeal with peanut butter') to calculate protein, fat, carbs & calories instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = mealSearchInput,
                        onValueChange = { input ->
                            mealSearchInput = input
                            if (input.isNotBlank()) {
                                viewModel.calculateMealNutrition(input)
                            } else {
                                viewModel.clearCalculatedMeal()
                            }
                        },
                        placeholder = { Text("Enter meal name e.g. 2 eggs, 1 roti, chicken karahi...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Meal", tint = NeonGreen)
                        },
                        trailingIcon = {
                            if (isCalculatingMeal) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = NeonGreen)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (mealSearchInput.isNotBlank()) {
                                viewModel.calculateMealNutrition(mealSearchInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_calculate_meal_macros")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Calculate", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CALCULATE MACROS (PROTEIN, FAT, KCAL)", fontWeight = FontWeight.ExtraBold)
                    }

                    calculatedMealResult?.let { res ->
                        if (res.mealName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = res.mealName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        MacroBadge("🔥 Calories", "${res.calories} kcal", AccentOrange, Modifier.weight(1f))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        MacroBadge("🍗 Protein", "${res.proteinG} g", NeonGreen, Modifier.weight(1f))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        MacroBadge("🥑 Fat", "${res.fatG} g", AccentCyan, Modifier.weight(1f))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        MacroBadge("🍞 Carbs", "${res.carbsG} g", Color(0xFFFFD54F), Modifier.weight(1f))
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = res.summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            viewModel.addCustomDietMeal(
                                                title = res.mealName,
                                                mealType = "Calculated Meal",
                                                calories = res.calories,
                                                protein = res.proteinG,
                                                carbs = res.carbsG,
                                                fat = res.fatG
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Log", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("LOG THIS MEAL (+${res.calories} KCAL)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

            // Plan Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dietPlans) { plan ->
                        FilterChip(
                            selected = selectedDietPlan == plan,
                            onClick = { viewModel.setDietPlanFilter(plan) },
                            label = { Text(plan, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            // Meal Items
            items(meals, key = { it.id }) { meal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (meal.isLoggedToday) NeonGreen.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (meal.isLoggedToday) DarkGreen else NeonGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = meal.mealType.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonGreen,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (meal.isLoggedToday) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = NeonGreen,
                                            contentColor = Color.Black
                                        ) {
                                            Text(
                                                text = "LOGGED ✓",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${meal.calories} kcal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = meal.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = meal.ingredients,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "P: ${meal.proteinG}g  •  C: ${meal.carbsG}g  •  F: ${meal.fatG}g",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan
                                )

                                Button(
                                    onClick = { viewModel.toggleLogMeal(meal) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (meal.isLoggedToday) DarkGreen else NeonGreen,
                                        contentColor = if (meal.isLoggedToday) Color.White else Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_log_meal_${meal.id}")
                                ) {
                                    Icon(
                                        imageVector = if (meal.isLoggedToday) Icons.Default.Check else Icons.Default.Restaurant,
                                        contentDescription = "Log Meal",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (meal.isLoggedToday) "LOGGED ✓" else "LOG MEAL",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

        // Floating Meal Logged Banner / Toast Feedback Notification
        AnimatedVisibility(
            visible = mealToastMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            mealToastMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NeonGreen,
                    contentColor = Color.Black,
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Fastfood, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Custom Meal Entry Dialog
    if (showCustomMealDialog) {
        var customTitle by remember { mutableStateOf("") }
        var customCalories by remember { mutableStateOf("") }
        var customProtein by remember { mutableStateOf("") }
        var customCarbs by remember { mutableStateOf("") }
        var customFat by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("Lunch") }

        AlertDialog(
            onDismissRequest = { showCustomMealDialog = false },
            title = { Text("Log Custom Meal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Meal Name") },
                        placeholder = { Text("e.g. 2 eggs, 1 roti, chicken biryani") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedButton(
                        onClick = {
                            if (customTitle.isNotBlank()) {
                                val estimated = MealNutritionEstimator.estimateMacros(customTitle)
                                customCalories = estimated.calories.toString()
                                customProtein = estimated.proteinG.toString()
                                customCarbs = estimated.carbsG.toString()
                                customFat = estimated.fatG.toString()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Auto", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AUTO-CALCULATE MACROS (P, F, KCAL)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = customCalories,
                        onValueChange = { customCalories = it },
                        label = { Text("Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customProtein,
                            onValueChange = { customProtein = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customCarbs,
                            onValueChange = { customCarbs = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customFat,
                            onValueChange = { customFat = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cal = customCalories.toIntOrNull() ?: 0
                        val p = customProtein.toIntOrNull() ?: 0
                        val c = customCarbs.toIntOrNull() ?: 0
                        val f = customFat.toIntOrNull() ?: 0

                        if (customTitle.isNotBlank()) {
                            viewModel.addCustomDietMeal(
                                title = customTitle,
                                mealType = selectedType,
                                calories = cal,
                                protein = p,
                                carbs = c,
                                fat = f
                            )
                            showCustomMealDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                ) {
                    Text("Log Meal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomMealDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MacroBarItem(
    label: String,
    goal: String,
    current: String,
    color: Color,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(text = current, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun MacroBadge(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
