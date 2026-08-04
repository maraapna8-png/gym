package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Alex Rivera",
    val email: String = "alex.fit@example.com",
    val profilePicUrl: String = "",
    val age: Int = 26,
    val gender: String = "Male",
    val heightCm: Float = 178f,
    val weightKg: Float = 75f,
    val targetWeightKg: Float = 70f,
    val fitnessLevel: String = "Intermediate", // Beginner, Intermediate, Advanced
    val activityLevel: String = "Moderately Active",
    val fitnessGoal: String = "Muscle Gain", // Weight Loss, Fat Loss, Muscle Gain, Strength, Endurance, Body Recomposition, General Fitness
    val medicalNotes: String = "None",
    val unitMetric: Boolean = true, // true for kg/cm, false for lbs/ft
    val isLogged: Boolean = true,
    val isAdmin: Boolean = false,
    val isPremium: Boolean = true
)

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Chest, Back, Shoulders, Biceps, Triceps, Legs, Glutes, Calves, Abs, Cardio, Stretching, Warm-Up, Cool Down
    val targetMuscle: String,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val equipment: String, // Dumbbell, Barbell, Machine, Bodyweight, Cable, None
    val sets: Int = 3,
    val reps: String = "10-12",
    val durationSec: Int = 45,
    val caloriesBurned: Int = 25,
    val instructions: String,
    val safetyTips: String = "Keep your core engaged and maintain controlled movement.",
    val commonMistakes: String = "Avoid arching your back or rushing the movement.",
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Beginner, Intermediate, Advanced
    val type: String, // Full Body, Upper Body, Push Pull Legs, HIIT, Strength, Fat Burning
    val description: String,
    val durationMin: Int,
    val totalExercises: Int,
    val caloriesBurned: Int,
    val difficulty: String,
    val isCustom: Boolean = false,
    val exerciseIdsCsv: String = "" // CSV of exercise IDs
)

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutTitle: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val exercisesCompleted: Int
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val currentMl: Int = 0,
    val goalMl: Int = 3000
)

@Entity(tableName = "step_logs")
data class StepLog(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val steps: Int = 4250,
    val goalSteps: Int = 10000,
    val distanceKm: Float = 3.2f,
    val caloriesBurned: Int = 210
)

@Entity(tableName = "diet_meals")
data class DietMeal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planType: String, // Weight Loss, Muscle Gain, High Protein, Keto, Vegetarian, Vegan, Diabetic Friendly
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val title: String,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val ingredients: String,
    val instructions: String = "Prepare fresh and serve as directed.",
    val isLoggedToday: Boolean = false
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val code: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f // 0.0 to 1.0
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "User" or "FitPro AI Trainer"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isUser: Boolean = false
)

data class BmiResult(
    val bmiValue: Float,
    val category: String,
    val idealWeightMinKg: Float,
    val idealWeightMaxKg: Float,
    val advice: String
)
