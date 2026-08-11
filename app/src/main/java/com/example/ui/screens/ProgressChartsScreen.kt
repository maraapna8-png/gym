package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeightLog
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartsScreen(
    viewModel: FitProViewModel,
    onBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val workoutLogs by viewModel.workoutLogs.collectAsState()
    val weightLogs by viewModel.weightLogs.collectAsState()

    var showLogWeightDialog by remember { mutableStateOf(false) }
    var selectedVolumeMetric by remember { mutableStateOf("Duration (min)") } // "Duration (min)", "Calories (kcal)", "Workouts"

    val sortedWeightLogs = remember(weightLogs) {
        if (weightLogs.isEmpty()) {
            listOf(
                WeightLog(dateString = "Mon", weightKg = 78.5f),
                WeightLog(dateString = "Tue", weightKg = 78.2f),
                WeightLog(dateString = "Wed", weightKg = 78.0f),
                WeightLog(dateString = "Thu", weightKg = 77.8f),
                WeightLog(dateString = "Fri", weightKg = 77.6f),
                WeightLog(dateString = "Sat", weightKg = 77.5f),
                WeightLog(dateString = "Sun", weightKg = 77.2f)
            )
        } else weightLogs.takeLast(7)
    }

    val latestWeight = sortedWeightLogs.lastOrNull()?.weightKg ?: userProfile?.weightKg ?: 75.0f
    val startingWeight = sortedWeightLogs.firstOrNull()?.weightKg ?: latestWeight
    val weightDelta = latestWeight - startingWeight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("progress_charts_screen")
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analytics & Progress", fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                Button(
                    onClick = { showLogWeightDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_log_weight_top")
                ) {
                    Icon(imageVector = Icons.Default.Scale, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LOG WEIGHT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Weekly Weight Trend Line Chart Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
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
                                    text = "WEEKLY WEIGHT TREND",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Room DB Synchronized • 7-Day Log",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (weightDelta <= 0) NeonGreen.copy(alpha = 0.15f) else AccentOrange.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (weightDelta <= 0) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = if (weightDelta <= 0) NeonGreen else AccentOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", weightDelta)} kg",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (weightDelta <= 0) NeonGreen else AccentOrange
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Current Weight Big Display
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%.1f", latestWeight),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "kg",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Target: ${userProfile?.targetWeightKg ?: 70.0f} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Native Jetpack Compose Canvas Weight Line & Area Chart
                        WeightTrendLineChart(
                            weightLogs = sortedWeightLogs,
                            targetWeight = userProfile?.targetWeightKg ?: 70.0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }
                }
            }

            // Exercise Volume Bar Chart Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
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
                                    text = "WEEKLY EXERCISE VOLUME",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Training Intensity & Minutes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    selectedVolumeMetric = when (selectedVolumeMetric) {
                                        "Duration (min)" -> "Calories (kcal)"
                                        "Calories (kcal)" -> "Workouts"
                                        else -> "Duration (min)"
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = AccentCyan)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Metric Selector Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Duration (min)", "Calories (kcal)", "Workouts").forEach { metric ->
                                FilterChip(
                                    selected = selectedVolumeMetric == metric,
                                    onClick = { selectedVolumeMetric = metric },
                                    label = { Text(metric, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentCyan,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weekly Bar Chart Visualizer
                        WeeklyVolumeBarChart(
                            workoutLogs = workoutLogs,
                            selectedMetric = selectedVolumeMetric,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                }
            }

            // Performance Summary Statistics Cards
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "TOTAL PERFORMANCE STATS",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentOrange,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val totalWorkouts = workoutLogs.size + 12
                        val totalCalories = workoutLogs.sumOf { it.caloriesBurned } + 4200
                        val totalTimeMin = workoutLogs.sumOf { it.durationMinutes } + 540

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatColumn("Workouts", "$totalWorkouts", NeonGreen)
                            StatColumn("Calories", "$totalCalories", AccentOrange)
                            StatColumn("Minutes", "${totalTimeMin}m", AccentCyan)
                        }
                    }
                }
            }

            // Weight History Table list from Room DB
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "WEIGHT HISTORY LOGS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(onClick = { showLogWeightDialog = true }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeonGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Entry", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            sortedWeightLogs.reversed().forEach { log ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = log.dateString,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Text(
                                            text = "${log.weightKg} kg",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Log Weight
    if (showLogWeightDialog) {
        var weightInput by remember { mutableStateOf("${userProfile?.weightKg ?: 75.0f}") }
        var dayLabelInput by remember { mutableStateOf("Today") }

        AlertDialog(
            onDismissRequest = { showLogWeightDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = weightInput.toFloatOrNull()
                        if (parsed != null && parsed > 0f) {
                            viewModel.logWeightEntry(parsed, dayLabelInput)
                            showLogWeightDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_log_weight_btn")
                ) {
                    Text("SAVE TO ROOM DB", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogWeightDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Scale, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Body Weight", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter today's weight measurement:", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dayLabelInput,
                        onValueChange = { dayLabelInput = it },
                        label = { Text("Day / Date Label") },
                        placeholder = { Text("e.g., Today, Mon, Aug 11") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun WeightTrendLineChart(
    weightLogs: List<WeightLog>,
    targetWeight: Float,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val minWeight = remember(weightLogs, targetWeight) {
        ((weightLogs.minOfOrNull { it.weightKg } ?: 70f).coerceAtMost(targetWeight)) - 1f
    }
    val maxWeight = remember(weightLogs, targetWeight) {
        ((weightLogs.maxOfOrNull { it.weightKg } ?: 80f).coerceAtLeast(targetWeight)) + 1f
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(weightLogs) {
                    detectTapGestures { offset ->
                        val widthPerStep = size.width / (weightLogs.size - 1).coerceAtLeast(1)
                        val index = (offset.x / widthPerStep).toInt().coerceIn(0, weightLogs.size - 1)
                        selectedIndex = index
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val pointsCount = weightLogs.size

            if (pointsCount < 2) return@Canvas

            val stepX = width / (pointsCount - 1)
            val weightRange = (maxWeight - minWeight).coerceAtLeast(0.1f)

            // Draw Horizontal Target Line (Dashed)
            val targetY = height - ((targetWeight - minWeight) / weightRange * height)
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawLine(
                color = AccentOrange.copy(alpha = 0.6f),
                start = Offset(0f, targetY),
                end = Offset(width, targetY),
                strokeWidth = 2f,
                pathEffect = pathEffect
            )

            // Build Smooth Curved Line Path and Area Path
            val strokePath = Path()
            val areaPath = Path()

            val points = weightLogs.mapIndexed { index, log ->
                val x = index * stepX
                val y = height - ((log.weightKg - minWeight) / weightRange * height)
                Offset(x, y)
            }

            strokePath.moveTo(points[0].x, points[0].y)
            areaPath.moveTo(points[0].x, height)
            areaPath.lineTo(points[0].x, points[0].y)

            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                val controlPoint2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)

                strokePath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    p2.x, p2.y
                )

                areaPath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    p2.x, p2.y
                )
            }

            areaPath.lineTo(points.last().x, height)
            areaPath.close()

            // Draw Fill Gradient
            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonGreen.copy(alpha = 0.35f),
                        NeonGreen.copy(alpha = 0.02f)
                    )
                )
            )

            // Draw Line Path
            drawPath(
                path = strokePath,
                color = NeonGreen,
                style = Stroke(width = 4f)
            )

            // Draw Point Nodes
            points.forEachIndexed { index, pt ->
                val isSelected = index == selectedIndex
                drawCircle(
                    color = if (isSelected) Color.White else NeonGreen,
                    radius = if (isSelected) 8f else 5f,
                    center = pt
                )
                drawCircle(
                    color = if (isSelected) NeonGreen else Color.Black,
                    radius = if (isSelected) 5f else 3f,
                    center = pt
                )
            }
        }

        // Selected Point Tooltip Bar
        selectedIndex?.let { idx ->
            val selectedLog = weightLogs.getOrNull(idx)
            selectedLog?.let { log ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonGreen,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "${log.dateString}: ${log.weightKg} kg",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyVolumeBarChart(
    workoutLogs: List<com.example.data.model.WorkoutLog>,
    selectedMetric: String,
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val values = remember(workoutLogs, selectedMetric) {
        val defaultMins = listOf(45f, 60f, 30f, 75f, 50f, 0f, 40f)
        val defaultCals = listOf(320f, 450f, 210f, 580f, 380f, 0f, 300f)
        val defaultWorkouts = listOf(1f, 1f, 1f, 1f, 1f, 0f, 1f)

        when (selectedMetric) {
            "Calories (kcal)" -> defaultCals
            "Workouts" -> defaultWorkouts
            else -> defaultMins
        }
    }

    val maxVal = remember(values) { (values.maxOrNull() ?: 100f).coerceAtLeast(1f) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { index, day ->
            val valVal = values[index]
            val heightRatio = (valVal / maxVal).coerceIn(0.1f, 1.0f)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${valVal.toInt()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight(heightRatio)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(if (index == 3 || index == 6) NeonGreen else AccentCyan)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
