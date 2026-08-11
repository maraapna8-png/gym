package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.StepLog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LiveStepTrackerCard(
    stepLog: StepLog,
    onAddSteps: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Permissions State
    fun hasLocationPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    var isPermissionGranted by remember { mutableStateOf(hasLocationPermissions()) }
    var showPermissionRequestCard by remember { mutableStateOf(false) }

    // Live Tracking Session State
    var isTrackingActive by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Telemetry Data
    var liveSessionSteps by remember { mutableIntStateOf(0) }
    var distanceMeters by remember { mutableFloatStateOf(0f) }
    var speedKmH by remember { mutableFloatStateOf(0f) }
    var currentLatitude by remember { mutableDoubleStateOf(37.7749) }
    var currentLongitude by remember { mutableDoubleStateOf(-122.4194) }
    var locationStatus by remember { mutableStateOf("Initializing GPS...") }
    var isSimulatingWalk by remember { mutableStateOf(false) }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val activityGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: true

        if (fineGranted || coarseGranted) {
            isPermissionGranted = true
            showPermissionRequestCard = false
            isTrackingActive = true
            isPaused = false
        } else {
            isPermissionGranted = false
            showPermissionRequestCard = true
        }
    }

    // Hardware Location Listener & Sensor Tracking
    DisposableEffect(isTrackingActive, isPaused) {
        if (!isTrackingActive || isPaused || !isPermissionGranted) {
            onDispose { }
        } else {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

            var lastLocation: Location? = null

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    speedKmH = (location.speed * 3.6f).coerceAtLeast(0f)
                    locationStatus = "GPS Signal Strong 🛰️"

                    lastLocation?.let { prev ->
                        val distDelta = prev.distanceTo(location)
                        if (distDelta > 1f && distDelta < 500f) { // filter noise
                            distanceMeters += distDelta
                            // Standard human stride ~0.75m per step
                            val stepsAdded = (distDelta / 0.75f).toInt()
                            if (stepsAdded > 0) {
                                liveSessionSteps += stepsAdded
                            }
                        }
                    }
                    lastLocation = location
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) { locationStatus = "GPS Provider Enabled" }
                override fun onProviderDisabled(provider: String) { locationStatus = "GPS Disabled - Turn on Location" }
            }

            // Step Sensor Listener
            val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            val sensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event != null && event.values.isNotEmpty() && event.values[0] > 0) {
                        liveSessionSteps += 1
                        distanceMeters += 0.75f
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            try {
                @SuppressLint("MissingPermission")
                if (hasLocationPermissions()) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L, // every 2 seconds
                        2f,     // 2 meters
                        locationListener
                    )
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        3000L,
                        5f,
                        locationListener
                    )
                }
                stepSensor?.let {
                    sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
                }
            } catch (e: Exception) {
                locationStatus = "Using Motion Sensor Tracking"
            }

            onDispose {
                try {
                    locationManager?.removeUpdates(locationListener)
                    sensorManager?.unregisterListener(sensorListener)
                } catch (_: Exception) {}
            }
        }
    }

    // Walking Simulator for indoors or emulator testing
    LaunchedEffect(isSimulatingWalk, isTrackingActive, isPaused) {
        if (isSimulatingWalk && isTrackingActive && !isPaused) {
            while (isSimulatingWalk && isTrackingActive && !isPaused) {
                delay(1000L)
                val randomSteps = kotlin.random.Random.nextInt(2, 6)
                liveSessionSteps += randomSteps
                distanceMeters += randomSteps * 0.75f
                speedKmH = 3.5f + kotlin.random.Random.nextFloat() * 1.7f
                currentLatitude += 0.00001
                currentLongitude += 0.00001
                locationStatus = "Live GPS Walk Active 🚶"
            }
        }
    }

    // Pulsing animation for Live indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val caloriesBurned = (distanceMeters * 0.05f).roundToInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_step_tracker_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isTrackingActive) 2.dp else 1.dp,
            color = if (isTrackingActive) NeonGreen else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isTrackingActive && !isPaused) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.4f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isTrackingActive) NeonGreen else Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (isTrackingActive) "LIVE GPS STEP TRACKER" else "DAILY STEP TRACKER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isTrackingActive) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AccentCyan.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GPS",
                            tint = AccentCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE LOCATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Step Counter & Goal Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${stepLog.steps + liveSessionSteps}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Goal: ${stepLog.goalSteps} steps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val totalKm = String.format("%.2f", (stepLog.steps + liveSessionSteps) * 0.00075)
                    Text(
                        text = "$totalKm km",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                    Text(
                        text = "Distance Walked",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicator Bar
            val totalSteps = stepLog.steps + liveSessionSteps
            val progressFraction = (totalSteps.toFloat() / stepLog.goalSteps.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = NeonGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Permission Warning Sheet (If requested and denied)
            AnimatedVisibility(visible = showPermissionRequestCard) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentOrange.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOff, contentDescription = null, tint = AccentOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOCATION PERMISSION REQUIRED",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To track live steps, distance, and speed as you walk, FitPro AI needs Location & Motion Sensor permissions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACTIVITY_RECOGNITION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_grant_location_permission")
                        ) {
                            Text("GRANT LOCATION PERMISSION 📍", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // Live Telemetry Details (When active)
            AnimatedVisibility(visible = isTrackingActive) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "LIVE TELEMETRY & GPS SENSORS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryBox(
                            label = "SESSION STEPS",
                            value = "+$liveSessionSteps",
                            color = NeonGreen,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryBox(
                            label = "DISTANCE",
                            value = "${(distanceMeters).roundToInt()} m",
                            color = AccentCyan,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryBox(
                            label = "SPEED",
                            value = String.format("%.1f km/h", speedKmH),
                            color = AccentOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // GPS Coordinates & Status Bar
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = String.format("Lat: %.4f, Lng: %.4f", currentLatitude, currentLongitude),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = locationStatus,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = NeonGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Indoor Walk Simulator Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulator (Indoor / Desktop Walk):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = isSimulatingWalk,
                            onCheckedChange = { isSimulatingWalk = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonGreen)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isTrackingActive) {
                    Button(
                        onClick = {
                            if (hasLocationPermissions()) {
                                isPermissionGranted = true
                                isTrackingActive = true
                                isPaused = false
                                showPermissionRequestCard = false
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACTIVITY_RECOGNITION
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_start_live_step_tracking")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("START LIVE TRACKING 📍", fontWeight = FontWeight.ExtraBold)
                    }

                    OutlinedButton(
                        onClick = { onAddSteps(1000) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("+1k", fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = { isPaused = !isPaused },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentOrange),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPaused) "RESUME" else "PAUSE", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (liveSessionSteps > 0) {
                                onAddSteps(liveSessionSteps)
                            }
                            isTrackingActive = false
                            isPaused = false
                            isSimulatingWalk = false
                            liveSessionSteps = 0
                            distanceMeters = 0f
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("btn_save_step_session")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE SESSION 💾", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
