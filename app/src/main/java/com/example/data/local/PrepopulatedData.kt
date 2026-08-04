package com.example.data.local

import com.example.data.model.Achievement
import com.example.data.model.DietMeal
import com.example.data.model.Exercise
import com.example.data.model.WorkoutPlan

object PrepopulatedData {

    val INITIAL_EXERCISES = listOf(
        // Chest
        Exercise(
            id = 1,
            name = "Barbell Bench Press",
            category = "Chest",
            targetMuscle = "Pectoralis Major, Triceps",
            difficulty = "Intermediate",
            equipment = "Barbell",
            sets = 4,
            reps = "8-10",
            durationSec = 60,
            caloriesBurned = 45,
            instructions = "Lie on flat bench. Grip barbell slightly wider than shoulder width. Lower bar to mid-chest slowly, then press upward explosively until arms extend.",
            safetyTips = "Keep feet flat on floor. Have a spotter when lifting heavy weights.",
            commonMistakes = "Bouncing the bar off chest or lifting glutes off the bench."
        ),
        Exercise(
            id = 2,
            name = "Incline Dumbbell Press",
            category = "Chest",
            targetMuscle = "Upper Chest, Anterior Deltoids",
            difficulty = "Intermediate",
            equipment = "Dumbbell",
            sets = 3,
            reps = "10-12",
            durationSec = 45,
            caloriesBurned = 35,
            instructions = "Set bench to 30-degree incline. Press dumbbells vertically over upper chest, lowering with control until elbows reach 90 degrees.",
            safetyTips = "Do not set the incline angle too high to prevent shoulder strain.",
            commonMistakes = "Flaring elbows out horizontally at 90 degrees."
        ),
        Exercise(
            id = 3,
            name = "Push-ups",
            category = "Chest",
            targetMuscle = "Chest, Shoulders, Triceps, Core",
            difficulty = "Beginner",
            equipment = "Bodyweight",
            sets = 3,
            reps = "15-20",
            durationSec = 40,
            caloriesBurned = 25,
            instructions = "High plank position with hands under shoulders. Lower chest until nearly touching floor, maintaining straight body line, then push back up.",
            safetyTips = "Maintain a rigid plank; don't let lower back sag.",
            commonMistakes = "Flaring elbows out wide or sagging hip alignment."
        ),
        Exercise(
            id = 4,
            name = "Cable Chest Flyes",
            category = "Chest",
            targetMuscle = "Inner & Outer Pectorals",
            difficulty = "Intermediate",
            equipment = "Cable",
            sets = 3,
            reps = "12-15",
            durationSec = 45,
            caloriesBurned = 30,
            instructions = "Set cable pulleys at chest level. Stand center, step forward with slight knee bend. Bring handles together in hugging arc motion.",
            safetyTips = "Slight bend in elbows throughout movement.",
            commonMistakes = "Turning movement into a press rather than a fly."
        ),

        // Back
        Exercise(
            id = 5,
            name = "Lat Pulldown",
            category = "Back",
            targetMuscle = "Latissimus Dorsi, Biceps",
            difficulty = "Beginner",
            equipment = "Machine",
            sets = 4,
            reps = "10-12",
            durationSec = 50,
            caloriesBurned = 35,
            instructions = "Grip bar wide. Sit down, secure thighs. Lean back slightly and pull bar down to upper chest while squeezing shoulder blades together.",
            safetyTips = "Avoid pulling behind the neck.",
            commonMistakes = "Swinging torso backwards to generate momentum."
        ),
        Exercise(
            id = 6,
            name = "Bent-Over Barbell Row",
            category = "Back",
            targetMuscle = "Rhomboids, Lats, Lower Back",
            difficulty = "Intermediate",
            equipment = "Barbell",
            sets = 4,
            reps = "8-10",
            durationSec = 55,
            caloriesBurned = 40,
            instructions = "Hinge hips back at 45 degrees with flat spine. Pull bar to upper abdomen while driving elbows back toward ceiling.",
            safetyTips = "Keep spine neutral to protect lower back.",
            commonMistakes = "Rounding lower back or standing up too upright."
        ),
        Exercise(
            id = 7,
            name = "Pull-ups",
            category = "Back",
            targetMuscle = "Lats, Upper Back, Biceps",
            difficulty = "Advanced",
            equipment = "Bodyweight",
            sets = 3,
            reps = "8-12",
            durationSec = 45,
            caloriesBurned = 38,
            instructions = "Overhand grip slightly wider than shoulders. Pull chin over bar by driving elbows down and back, then lower with control.",
            safetyTips = "Avoid swinging feet or kicking legs.",
            commonMistakes = "Not achieving full elbow extension at the bottom."
        ),

        // Shoulders
        Exercise(
            id = 8,
            name = "Overhead Dumbbell Press",
            category = "Shoulders",
            targetMuscle = "Deltoids, Upper Triceps",
            difficulty = "Intermediate",
            equipment = "Dumbbell",
            sets = 4,
            reps = "10-12",
            durationSec = 50,
            caloriesBurned = 35,
            instructions = "Sit or stand with core brace. Press dumbbells straight up overhead until arms extend without locking elbows.",
            safetyTips = "Avoid arching lower back severely.",
            commonMistakes = "Lowering dumbbells too fast without control."
        ),
        Exercise(
            id = 9,
            name = "Dumbbell Lateral Raises",
            category = "Shoulders",
            targetMuscle = "Lateral Deltoids",
            difficulty = "Beginner",
            equipment = "Dumbbell",
            sets = 3,
            reps = "12-15",
            durationSec = 40,
            caloriesBurned = 25,
            instructions = "Stand upright holding dumbbells at sides. Raise arms out to sides until parallel with floor, pinkies slightly higher than thumbs.",
            safetyTips = "Use moderate light weights to avoid shrugging traps.",
            commonMistakes = "Using momentum or swinging upper body."
        ),

        // Arms (Biceps & Triceps)
        Exercise(
            id = 10,
            name = "Barbell Bicep Curl",
            category = "Biceps",
            targetMuscle = "Biceps Brachii",
            difficulty = "Beginner",
            equipment = "Barbell",
            sets = 3,
            reps = "10-12",
            durationSec = 40,
            caloriesBurned = 25,
            instructions = "Underhand grip on bar. Keep elbows tucked to ribs. Curl bar toward shoulders, pause for squeeze, then lower smoothly.",
            safetyTips = "Keep wrists straight and knees slightly soft.",
            commonMistakes = "Swinging hips back and forth to lift weight."
        ),
        Exercise(
            id = 11,
            name = "Tricep Rope Pushdown",
            category = "Triceps",
            targetMuscle = "Triceps Brachii",
            difficulty = "Beginner",
            equipment = "Cable",
            sets = 3,
            reps = "12-15",
            durationSec = 40,
            caloriesBurned = 25,
            instructions = "Attach rope to high pulley. Keep upper arms glued to sides. Push rope down, spreading rope ends apart at bottom.",
            safetyTips = "Control the eccentric return phase.",
            commonMistakes = "Letting elbows flare forward or shrugging shoulders."
        ),

        // Legs & Glutes
        Exercise(
            id = 12,
            name = "Barbell Back Squat",
            category = "Legs",
            targetMuscle = "Quadriceps, Glutes, Hamstrings",
            difficulty = "Advanced",
            equipment = "Barbell",
            sets = 4,
            reps = "6-8",
            durationSec = 60,
            caloriesBurned = 60,
            instructions = "Position barbell across upper traps. Feet shoulder-width apart. Sit hips back and down until thighs parallel to floor, then drive up.",
            safetyTips = "Keep knees aligned over toes. Use squat rack safety bars.",
            commonMistakes = "Knees caving inward or heels lifting off floor."
        ),
        Exercise(
            id = 13,
            name = "Dumbbell Walking Lunges",
            category = "Legs",
            targetMuscle = "Quads, Glutes, Calves",
            difficulty = "Intermediate",
            equipment = "Dumbbell",
            sets = 3,
            reps = "12 steps/leg",
            durationSec = 50,
            caloriesBurned = 40,
            instructions = "Hold dumbbells at sides. Step forward into lunge until back knee nearly touches ground, then push off back leg to step into next lunge.",
            safetyTips = "Maintain upright posture; don't lean torso forward.",
            commonMistakes = "Front knee driving excessively beyond toes."
        ),

        // Abs & Core
        Exercise(
            id = 14,
            name = "Plank Hold",
            category = "Abs",
            targetMuscle = "Transverse Abdominis, Core",
            difficulty = "Beginner",
            equipment = "Bodyweight",
            sets = 3,
            reps = "60 sec",
            durationSec = 60,
            caloriesBurned = 20,
            instructions = "Forearms on ground under shoulders. Body straight from head to heels. Contract abs tightly and hold steady breath.",
            safetyTips = "If lower back hurts, drop knees down.",
            commonMistakes = "Sagging hips or raising glutes too high."
        ),
        Exercise(
            id = 15,
            name = "Hanging Leg Raises",
            category = "Abs",
            targetMuscle = "Lower Abs, Hip Flexors",
            difficulty = "Intermediate",
            equipment = "Bodyweight",
            sets = 3,
            reps = "12-15",
            durationSec = 40,
            caloriesBurned = 30,
            instructions = "Hang from pull-up bar with overhand grip. Raise extended legs up until perpendicular to torso, lowering with control.",
            safetyTips = "Engage lats to stabilize shoulders.",
            commonMistakes = "Swinging body or using momentum."
        ),

        // Cardio & HIIT
        Exercise(
            id = 16,
            name = "Burpees",
            category = "Cardio",
            targetMuscle = "Full Body Cardio",
            difficulty = "Intermediate",
            equipment = "Bodyweight",
            sets = 4,
            reps = "15 reps",
            durationSec = 45,
            caloriesBurned = 50,
            instructions = "Squat down, kick feet back to push-up plank, perform push-up, jump feet back under hips, and jump explosively upward with hands raised.",
            safetyTips = "Land softly on balls of feet.",
            commonMistakes = "Arching spine in plank phase."
        ),
        Exercise(
            id = 17,
            name = "Jumping Rope",
            category = "Cardio",
            targetMuscle = "Calves, Heart Rate, Endurance",
            difficulty = "Beginner",
            equipment = "None",
            sets = 3,
            reps = "2 mins",
            durationSec = 120,
            caloriesBurned = 70,
            instructions = "Jump lightly on balls of feet while swinging rope smoothly with wrists.",
            safetyTips = "Stay soft on knees to cushion impact.",
            commonMistakes = "Jumping too high or using whole arms to turn rope."
        )
    )

    val INITIAL_WORKOUT_PLANS = listOf(
        WorkoutPlan(
            id = 1,
            title = "Full Body Beginner Blast",
            category = "Beginner",
            type = "Full Body",
            description = "Ideal kickstart routine focusing on foundational strength, posture, and core stability.",
            durationMin = 35,
            totalExercises = 6,
            caloriesBurned = 280,
            difficulty = "Beginner",
            exerciseIdsCsv = "3,5,9,10,11,14"
        ),
        WorkoutPlan(
            id = 2,
            title = "Push Pull Legs (PPL) Split",
            category = "Intermediate",
            type = "Push Pull Legs",
            description = "Hypertrophy-focused classic split routine for maximum muscle building and recovery.",
            durationMin = 50,
            totalExercises = 8,
            caloriesBurned = 420,
            difficulty = "Intermediate",
            exerciseIdsCsv = "1,2,6,7,8,12,13,15"
        ),
        WorkoutPlan(
            id = 3,
            title = "Advanced High Intensity HIIT Shred",
            category = "Advanced",
            type = "HIIT",
            description = "Fast-paced metabolic conditioning to accelerate fat loss, stamina, and explosive power.",
            durationMin = 40,
            totalExercises = 7,
            caloriesBurned = 510,
            difficulty = "Advanced",
            exerciseIdsCsv = "3,12,13,14,15,16,17"
        )
    )

    val INITIAL_DIET_PLANS = listOf(
        DietMeal(
            planType = "Muscle Gain",
            mealType = "Breakfast",
            title = "High-Protein Oatmeal & Egg Bowl",
            calories = 520,
            proteinG = 38,
            carbsG = 58,
            fatG = 14,
            ingredients = "1 cup Oats, 1 scoop Whey Protein, 3 Whole Eggs, 1 tbsp Almond Butter, 1/2 Banana."
        ),
        DietMeal(
            planType = "Muscle Gain",
            mealType = "Lunch",
            title = "Grilled Chicken Breast with Brown Rice & Avocado",
            calories = 680,
            proteinG = 52,
            carbsG = 65,
            fatG = 18,
            ingredients = "200g Chicken Breast, 1.5 cups Jasmine Rice, 1/2 Avocado, Steamed Broccoli."
        ),
        DietMeal(
            planType = "Weight Loss",
            mealType = "Breakfast",
            title = "Greek Yogurt Parfait & Berries",
            calories = 310,
            proteinG = 26,
            carbsG = 32,
            fatG = 6,
            ingredients = "200g Non-fat Greek Yogurt, 1/2 cup Mixed Berries, 10g Chia Seeds, 1 tsp Honey."
        ),
        DietMeal(
            planType = "Weight Loss",
            mealType = "Lunch",
            title = "Mediterranean Salmon Salad Bowl",
            calories = 420,
            proteinG = 38,
            carbsG = 18,
            fatG = 20,
            ingredients = "150g Grilled Salmon, Spinach leaves, Cucumbers, Olive Oil dressing, Feta cheese."
        ),
        DietMeal(
            planType = "Keto",
            mealType = "Dinner",
            title = "Keto Butter Steak with Asparagus",
            calories = 650,
            proteinG = 48,
            carbsG = 6,
            fatG = 46,
            ingredients = "220g Ribeye Steak, Garlic Butter, Sautéed Asparagus, Olive Oil."
        )
    )

    val INITIAL_ACHIEVEMENTS = listOf(
        Achievement("A1", "First Step", "Complete your first workout session", "ic_badge_star", true, 1.0f),
        Achievement("A2", "7-Day Warrior", "Maintain a 7-day workout streak", "ic_badge_fire", false, 0.43f),
        Achievement("A3", "Hydration Master", "Reach daily water goal 5 times", "ic_badge_water", false, 0.6f),
        Achievement("A4", "Iron Lifter", "Log 25 custom exercise sessions", "ic_badge_dumbbell", false, 0.2f),
        Achievement("A5", "Calorie Burner", "Burn over 5,000 total workout calories", "ic_badge_flame", false, 0.55f),
        Achievement("A6", "Goal Crusher", "Reach target weight or body recomposition target", "ic_badge_trophy", false, 0.0f)
    )
}
