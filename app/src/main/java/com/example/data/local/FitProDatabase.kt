package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        WorkoutPlan::class,
        WorkoutLog::class,
        WaterLog::class,
        StepLog::class,
        DietMeal::class,
        Achievement::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FitProDatabase : RoomDatabase() {

    abstract fun fitProDao(): FitProDao

    companion object {
        @Volatile
        private var INSTANCE: FitProDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FitProDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitProDatabase::class.java,
                    "fitpro_ai_database"
                )
                    .addCallback(FitProDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class FitProDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.fitProDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val dao = database.fitProDao()
                        if (dao.getUserProfileOnce() == null) {
                            populateInitialData(dao)
                        }
                    }
                }
            }

            suspend fun populateInitialData(dao: FitProDao) {
                dao.insertOrUpdateProfile(UserProfile())
                dao.insertExercises(PrepopulatedData.INITIAL_EXERCISES)
                dao.insertWorkoutPlans(PrepopulatedData.INITIAL_WORKOUT_PLANS)
                dao.insertDietMeals(PrepopulatedData.INITIAL_DIET_PLANS)
                dao.insertAchievements(PrepopulatedData.INITIAL_ACHIEVEMENTS)
                dao.insertOrUpdateWaterLog(WaterLog(dateString = getCurrentDateString(), currentMl = 1250, goalMl = 3000))
                dao.insertOrUpdateStepLog(StepLog(dateString = getCurrentDateString(), steps = 4820, goalSteps = 10000))
            }

            private fun getCurrentDateString(): String {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                return sdf.format(java.util.Date())
            }
        }
    }
}
