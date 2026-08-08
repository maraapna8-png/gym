package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.FitProTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.viewmodel.FitProViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: FitProViewModel = viewModel()
            val darkTheme by viewModel.darkTheme.collectAsState()

            FitProTheme(darkTheme = darkTheme) {
                FitProAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FitProAppContainer(viewModel: FitProViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val activeSubScreen by viewModel.activeSubScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var isAuthenticated by remember { mutableStateOf(true) } // Authenticated by default for instant preview

    if (!isAuthenticated) {
        AuthScreen(
            viewModel = viewModel,
            onLoginSuccess = { isAuthenticated = true }
        )
        return
    }

    if (!userProfile.isOnboardingCompleted || activeSubScreen == "ONBOARDING") {
        OnboardingScreen(
            viewModel = viewModel,
            onFinishOnboarding = {
                viewModel.navigateToSubScreen(null)
            }
        )
        return
    }

    if (activeSubScreen != null) {
        when (activeSubScreen) {
            "SESSION" -> ActiveSessionScreen(viewModel = viewModel)
            "BMI_CALCULATOR" -> BmiCalculatorScreen(viewModel = viewModel, onBack = { viewModel.navigateToSubScreen(null) })
            "AI_CHAT" -> AiCoachChatScreen(viewModel = viewModel, onBack = { viewModel.navigateToSubScreen(null) })
            "ADMIN_PANEL" -> AdminPanelScreen(viewModel = viewModel, onBack = { viewModel.navigateToSubScreen(null) })
            "SETTINGS" -> SettingsScreen(viewModel = viewModel, onBack = { viewModel.navigateToSubScreen(null) })
            "PROGRESS_CHARTS" -> ProgressChartsScreen(viewModel = viewModel, onBack = { viewModel.navigateToSubScreen(null) })
            "ACHIEVEMENTS" -> AchievementsScreen(viewModel = viewModel, onBack = { viewModel.navigateToSubScreen(null) })
            else -> DashboardScreen(viewModel = viewModel, onStartWorkoutPlan = { viewModel.startWorkoutSession(it) })
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = NeonGreen
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(imageVector = if (selectedTab == 0) Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonGreen,
                        selectedTextColor = NeonGreen,
                        indicatorColor = NeonGreen.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(imageVector = if (selectedTab == 1) Icons.Default.FitnessCenter else Icons.Outlined.FitnessCenter, contentDescription = "Workouts") },
                    label = { Text("Workouts", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonGreen,
                        selectedTextColor = NeonGreen,
                        indicatorColor = NeonGreen.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_workouts")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(imageVector = if (selectedTab == 2) Icons.Default.Search else Icons.Outlined.Search, contentDescription = "Library") },
                    label = { Text("Library", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonGreen,
                        selectedTextColor = NeonGreen,
                        indicatorColor = NeonGreen.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_library")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(imageVector = if (selectedTab == 3) Icons.Default.Restaurant else Icons.Outlined.Restaurant, contentDescription = "Diet") },
                    label = { Text("Diet", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonGreen,
                        selectedTextColor = NeonGreen,
                        indicatorColor = NeonGreen.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_diet")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(imageVector = if (selectedTab == 4) Icons.Default.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonGreen,
                        selectedTextColor = NeonGreen,
                        indicatorColor = NeonGreen.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = viewModel, onStartWorkoutPlan = { viewModel.startWorkoutSession(it) })
                1 -> WorkoutsScreen(viewModel = viewModel, onStartWorkout = { viewModel.startWorkoutSession(it) })
                2 -> ExerciseLibraryScreen(viewModel = viewModel)
                3 -> DietScreen(viewModel = viewModel)
                4 -> ProfileAndMoreScreen(viewModel = viewModel)
            }
        }
    }
}
