package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FitProDatabase
import com.example.data.model.*
import com.example.repository.FitProRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FitProViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FitProDatabase.getDatabase(application, viewModelScope)
    val repository = FitProRepository(db.fitProDao())

    // Selected Navigation Tab (0=Dashboard, 1=Workouts, 2=Library, 3=Diet, 4=Profile/More)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // SubScreen for Navigation (null = main tab view, or "SESSION", "AI_CHAT", "BMI", "ADMIN", "SETTINGS", "PROFILE_SETUP", "PROGRESS")
    private val _activeSubScreen = MutableStateFlow<String?>(null)
    val activeSubScreen: StateFlow<String?> = _activeSubScreen.asStateFlow()

    // User Profile
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // Exercises & Search Filters
    private val _exerciseSearchQuery = MutableStateFlow("")
    val exerciseSearchQuery: StateFlow<String> = _exerciseSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredExercises: StateFlow<List<Exercise>> = combine(
        repository.allExercises,
        _exerciseSearchQuery,
        _selectedCategory
    ) { exercises, query, category ->
        exercises.filter { ex ->
            val matchCategory = category == "All" || ex.category.equals(category, ignoreCase = true)
            val matchQuery = query.isBlank() || ex.name.contains(query, ignoreCase = true) || ex.targetMuscle.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workout Plans
    val workoutPlans: StateFlow<List<WorkoutPlan>> = repository.allWorkoutPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workout Logs
    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.allWorkoutLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Diet Meals
    private val _selectedDietPlan = MutableStateFlow("All")
    val selectedDietPlan: StateFlow<String> = _selectedDietPlan.asStateFlow()

    val dietMeals: StateFlow<List<DietMeal>> = combine(
        repository.allDietMeals,
        _selectedDietPlan
    ) { meals, plan ->
        if (plan == "All") meals else meals.filter { it.planType.equals(plan, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Water Tracker
    private val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val waterLog: StateFlow<WaterLog> = repository.getWaterLog(todayDateString)
        .map { it ?: WaterLog(dateString = todayDateString, currentMl = 1250, goalMl = 3000) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WaterLog(todayDateString, 1250, 3000))

    // Step Tracker
    val stepLog: StateFlow<StepLog> = repository.getStepLog(todayDateString)
        .map { it ?: StepLog(dateString = todayDateString, steps = 5120, goalSteps = 10000) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StepLog(todayDateString, 5120, 10000))

    // Achievements
    val achievements: StateFlow<List<Achievement>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Workout Session State
    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan.asStateFlow()

    private val _isSessionRunning = MutableStateFlow(false)
    val isSessionRunning: StateFlow<Boolean> = _isSessionRunning.asStateFlow()

    private val _sessionTimerSeconds = MutableStateFlow(0)
    val sessionTimerSeconds: StateFlow<Int> = _sessionTimerSeconds.asStateFlow()

    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private var timerJob: Job? = null

    // AI Chat Coach
    private val _chatMessages = MutableStateFlow(listOf(
        ChatMessage(
            sender = "FitPro AI Trainer",
            message = "Hello Athlete! I'm your AI Personal Trainer. Ask me anything about workout routines, nutrition, form tips, or weight loss!",
            isUser = false
        )
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Settings
    private val _darkTheme = MutableStateFlow(true)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    // --- Actions ---

    fun selectTab(index: Int) {
        _selectedTab.value = index
        _activeSubScreen.value = null
    }

    fun navigateToSubScreen(subScreen: String?) {
        _activeSubScreen.value = subScreen
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _exerciseSearchQuery.value = query
    }

    fun setDietPlanFilter(plan: String) {
        _selectedDietPlan.value = plan
    }

    fun toggleFavoriteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.updateExercise(exercise.copy(isFavorite = !exercise.isFavorite))
        }
    }

    fun updateProfile(updated: UserProfile) {
        viewModelScope.launch {
            repository.saveProfile(updated)
        }
    }

    fun addWaterIntake(amountMl: Int) {
        viewModelScope.launch {
            val current = waterLog.value
            val newAmount = (current.currentMl + amountMl).coerceAtLeast(0)
            repository.updateWaterLog(current.copy(currentMl = newAmount))
        }
    }

    fun addSteps(addedSteps: Int) {
        viewModelScope.launch {
            val current = stepLog.value
            val newSteps = current.steps + addedSteps
            val newDist = newSteps * 0.00075f
            val newCal = (newSteps * 0.04f).toInt()
            repository.updateStepLog(current.copy(steps = newSteps, distanceKm = newDist, caloriesBurned = newCal))
        }
    }

    fun startWorkoutSession(plan: WorkoutPlan) {
        _activePlan.value = plan
        _currentExerciseIndex.value = 0
        _sessionTimerSeconds.value = 0
        _isSessionRunning.value = true
        _activeSubScreen.value = "SESSION"

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isSessionRunning.value) {
                delay(1000)
                _sessionTimerSeconds.value += 1
                if (_restTimerSeconds.value > 0) {
                    _restTimerSeconds.value -= 1
                }
            }
        }
    }

    fun triggerRestTimer(seconds: Int = 45) {
        _restTimerSeconds.value = seconds
    }

    fun nextExercise() {
        val plan = _activePlan.value ?: return
        if (_currentExerciseIndex.value < plan.totalExercises - 1) {
            _currentExerciseIndex.value += 1
            triggerRestTimer(45)
        } else {
            finishWorkoutSession()
        }
    }

    fun finishWorkoutSession() {
        val plan = _activePlan.value
        _isSessionRunning.value = false
        timerJob?.cancel()

        if (plan != null) {
            val durationMin = (_sessionTimerSeconds.value / 60).coerceAtLeast(1)
            val cal = plan.caloriesBurned
            viewModelScope.launch {
                repository.logCompletedWorkout(
                    WorkoutLog(
                        workoutTitle = plan.title,
                        durationMinutes = durationMin,
                        caloriesBurned = cal,
                        exercisesCompleted = plan.totalExercises
                    )
                )
            }
        }
        _activeSubScreen.value = null
    }

    fun generateAiWorkoutPlan(equipment: String, timeMinutes: Int) {
        viewModelScope.launch {
            _isAiLoading.value = true
            repository.generateAiWorkout(userProfile.value, equipment, timeMinutes)
            _isAiLoading.value = false
        }
    }

    fun sendAiChatMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val userMsg = ChatMessage(sender = "User", message = userPrompt, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isAiLoading.value = true
            val reply = repository.askAiTrainer(userPrompt, userProfile.value)
            val aiMsg = ChatMessage(sender = "FitPro AI Trainer", message = reply, isUser = false)
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiLoading.value = false
        }
    }

    fun toggleTheme() {
        _darkTheme.value = !_darkTheme.value
    }

    fun addNewExerciseByAdmin(
        name: String,
        category: String,
        targetMuscle: String,
        difficulty: String,
        equipment: String,
        instructions: String
    ) {
        viewModelScope.launch {
            repository.addExercise(
                Exercise(
                    name = name,
                    category = category,
                    targetMuscle = targetMuscle,
                    difficulty = difficulty,
                    equipment = equipment,
                    instructions = instructions,
                    isCustom = true
                )
            )
        }
    }

    fun calculateBmi(weightKg: Float, heightCm: Float): BmiResult {
        val heightM = heightCm / 100f
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 22f
        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal Weight"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obese"
        }
        val minIdeal = 18.5f * (heightM * heightM)
        val maxIdeal = 24.9f * (heightM * heightM)

        val advice = when (category) {
            "Underweight" -> "Focus on a moderate calorie surplus with high protein intake and progressive resistance strength training."
            "Normal Weight" -> "Great job! Maintain body recomposition with balanced hypertrophy workouts and consistent daily activity."
            "Overweight" -> "Incorporate HIIT cardio 3x/week and maintain a light 300-cal daily deficit alongside strength exercises."
            else -> "Consult your healthcare provider, start with low-impact cardio, walking, and light resistance training."
        }

        return BmiResult(
            bmiValue = String.format(Locale.US, "%.1f", bmi).toFloat(),
            category = category,
            idealWeightMinKg = String.format(Locale.US, "%.1f", minIdeal).toFloat(),
            idealWeightMaxKg = String.format(Locale.US, "%.1f", maxIdeal).toFloat(),
            advice = advice
        )
    }
}
