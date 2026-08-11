package com.example.repository

import com.example.data.local.FitProDao
import com.example.data.model.*
import com.example.network.GeminiService
import com.example.util.MealMacroResult
import com.example.util.MealNutritionEstimator
import kotlinx.coroutines.flow.Flow

class FitProRepository(private val dao: FitProDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allExercises: Flow<List<Exercise>> = dao.getAllExercises()
    val allWorkoutPlans: Flow<List<WorkoutPlan>> = dao.getAllWorkoutPlans()
    val allWorkoutLogs: Flow<List<WorkoutLog>> = dao.getAllWorkoutLogs()
    val allWeightLogs: Flow<List<WeightLog>> = dao.getAllWeightLogs()
    val allDietMeals: Flow<List<DietMeal>> = dao.getAllDietMeals()
    val allAchievements: Flow<List<Achievement>> = dao.getAllAchievements()

    fun getExercisesByCategory(category: String): Flow<List<Exercise>> {
        return if (category == "All") dao.getAllExercises() else dao.getExercisesByCategory(category)
    }

    fun getDietMealsByPlan(planType: String): Flow<List<DietMeal>> {
        return if (planType == "All") dao.getAllDietMeals() else dao.getDietMealsByPlan(planType)
    }

    fun getWaterLog(dateString: String): Flow<WaterLog?> = dao.getWaterLogByDate(dateString)

    fun getStepLog(dateString: String): Flow<StepLog?> = dao.getStepLogByDate(dateString)

    suspend fun saveProfile(profile: UserProfile) = dao.insertOrUpdateProfile(profile)

    suspend fun addExercise(exercise: Exercise) = dao.insertExercise(exercise)

    suspend fun updateExercise(exercise: Exercise) = dao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) = dao.deleteExercise(exercise)

    suspend fun saveWorkoutPlan(plan: WorkoutPlan) = dao.insertWorkoutPlan(plan)

    suspend fun logCompletedWorkout(log: WorkoutLog) = dao.insertWorkoutLog(log)

    suspend fun logWeight(log: WeightLog) = dao.insertWeightLog(log)

    suspend fun updateWaterLog(waterLog: WaterLog) = dao.insertOrUpdateWaterLog(waterLog)

    suspend fun updateStepLog(stepLog: StepLog) = dao.insertOrUpdateStepLog(stepLog)

    suspend fun saveDietMeal(meal: DietMeal) = dao.insertDietMeal(meal)

    suspend fun updateAchievement(achievement: Achievement) = dao.insertOrUpdateAchievement(achievement)

    suspend fun generateAiWorkout(
        userProfile: UserProfile,
        equipment: String,
        timeMin: Int,
        userGoal: String = userProfile.fitnessGoal,
        weeklyAvailabilityDays: Int = 4,
        focusArea: String = "Full Body"
    ): WorkoutPlan {
        val aiPlan = GeminiService.generateAiWorkoutPlan(
            user = userProfile,
            availableEquipment = equipment,
            workoutTimeMinutes = timeMin,
            userGoal = userGoal,
            weeklyAvailabilityDays = weeklyAvailabilityDays,
            focusArea = focusArea
        )
        dao.insertWorkoutPlan(aiPlan)
        return aiPlan
    }

    suspend fun askAiTrainer(prompt: String, userProfile: UserProfile): String {
        return GeminiService.chatWithAiTrainer(prompt, userProfile)
    }

    suspend fun calculateMealMacros(mealName: String): MealMacroResult {
        return GeminiService.calculateMealMacrosWithAi(mealName)
    }

    suspend fun generateWorkoutMusic(
        prompt: String,
        genre: String,
        isShortClip: Boolean
    ): GeneratedMusicTrack {
        return GeminiService.generateWorkoutMusicTrack(prompt, genre, isShortClip)
    }

    suspend fun getInstantMotivationFlashLite(topic: String): String {
        return GeminiService.getInstantMotivationFlashLite(topic)
    }

    suspend fun generateDeepProDiagnostic(userProfile: UserProfile): AiHealthReport {
        return GeminiService.generateDeepProDiagnostic(userProfile)
    }
}
