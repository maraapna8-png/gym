package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FitProDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // Exercises
    @Query("SELECT * FROM exercises ORDER BY id ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY id ASC")
    fun getExercisesByCategory(category: String): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    // Workout Plans
    @Query("SELECT * FROM workout_plans ORDER BY id ASC")
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: WorkoutPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlans(plans: List<WorkoutPlan>)

    // Workout Logs
    @Query("SELECT * FROM workout_logs ORDER BY dateMillis DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog)

    // Water Logs
    @Query("SELECT * FROM water_logs WHERE dateString = :dateString LIMIT 1")
    fun getWaterLogByDate(dateString: String): Flow<WaterLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWaterLog(waterLog: WaterLog)

    // Step Logs
    @Query("SELECT * FROM step_logs WHERE dateString = :dateString LIMIT 1")
    fun getStepLogByDate(dateString: String): Flow<StepLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStepLog(stepLog: StepLog)

    // Diet Meals
    @Query("SELECT * FROM diet_meals ORDER BY id ASC")
    fun getAllDietMeals(): Flow<List<DietMeal>>

    @Query("SELECT * FROM diet_meals WHERE planType = :planType ORDER BY id ASC")
    fun getDietMealsByPlan(planType: String): Flow<List<DietMeal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietMeal(meal: DietMeal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietMeals(meals: List<DietMeal>)

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, code ASC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAchievement(achievement: Achievement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
}
