package com.example.data

import com.example.data.model.GymAppliance

object GymEquipmentData {

    val ALL_20_EQUIPMENT: List<GymAppliance> = listOf(
        GymAppliance(
            id = "dumbbell",
            name = "Dumbbells",
            category = "Free Weights",
            description = "Versatile pair of dumbbells for chest presses, bicep curls, shoulder raises, and rows.",
            targetMuscles = "Chest, Biceps, Shoulders, Triceps, Back",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "barbell",
            name = "Barbell / Rod",
            category = "Free Weights",
            description = "Heavy olympic rod/barbell essential for squats, deadlifts, bench press, and heavy rows.",
            targetMuscles = "Full Body, Legs, Back, Chest, Shoulders",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "weight_plates",
            name = "Weight Plates",
            category = "Free Weights",
            description = "Bumper and iron weight plates (2.5kg to 25kg) for custom barbell & landmine loading.",
            targetMuscles = "Full Body Strength",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "kettlebell",
            name = "Kettlebells",
            category = "Free Weights",
            description = "Cast iron kettlebells for explosive swings, Turkish get-ups, and core stabilization.",
            targetMuscles = "Glutes, Core, Shoulders, Hamstrings",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "cable_machine",
            name = "Cable Crossover Machine",
            category = "Machines",
            description = "Dual-pulley adjustable cable station for constant tension chest flyes, pushdowns, and face pulls.",
            targetMuscles = "Chest, Triceps, Shoulders, Rear Delt",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "smith_machine",
            name = "Smith Machine",
            category = "Machines",
            description = "Guided barbell track machine for secure heavy benching, squats, and incline presses.",
            targetMuscles = "Chest, Quads, Glutes, Shoulders",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "leg_press",
            name = "Leg Press Machine",
            category = "Machines",
            description = "45-degree angled plate-loaded leg press for massive quad, hamstrings, and glutes overload.",
            targetMuscles = "Quadriceps, Glutes, Hamstrings, Calves",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "pullup_bar",
            name = "Pull-Up Bar & Dip Station",
            category = "Calisthenics",
            description = "Multi-grip bodyweight frame for wide lat pull-ups, chest dips, and hanging leg raises.",
            targetMuscles = "Lats, Lower Chest, Triceps, Abs",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "treadmill",
            name = "Treadmill",
            category = "Cardio",
            description = "Motorized running belt with incline controls for warm-up, HIIT sprints, and endurance.",
            targetMuscles = "Cardio, Legs, Stamina",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "exercise_bike",
            name = "Stationary Exercise Bike",
            category = "Cardio",
            description = "Spin or upright stationary bike for intense calorie burning and low-impact leg conditioning.",
            targetMuscles = "Quads, Calves, Cardiovascular",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "resistance_bands",
            name = "Resistance Bands",
            category = "Accessories",
            description = "Heavy elastic loop bands for mobility, warming up, assistance, and muscle burnout.",
            targetMuscles = "Glutes, Shoulders, Rotator Cuff, Mobility",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "adjustable_bench",
            name = "Adjustable Workout Bench",
            category = "Benches",
            description = "Flat, incline, and decline bench for pressing angles and dumbbell support.",
            targetMuscles = "Upper Chest, Lower Chest, Back Support",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "ez_bar",
            name = "EZ Curl Bar",
            category = "Free Weights",
            description = "Angled ergonomic barbell designed to isolate bicep curls and skullcrushers without wrist strain.",
            targetMuscles = "Biceps, Forearms, Triceps",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "ab_roller",
            name = "Ab Roller Wheel",
            category = "Core",
            description = "Dual-wheel roll-out tool for extreme core activation, lats stabilization, and 6-pack abs.",
            targetMuscles = "Abs, Core, Lats, Lower Back",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "pec_deck",
            name = "Pec Deck / Butterfly Machine",
            category = "Machines",
            description = "Isolated machine fly for inner chest hypertrophy and rear deltoid reverse flyes.",
            targetMuscles = "Chest, Inner Pectorals, Rear Delts",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "lat_pulldown",
            name = "Lat Pulldown Machine",
            category = "Machines",
            description = "Cable station with wide bar attachment to build a wide V-taper back and thick lats.",
            targetMuscles = "Latissimus Dorsi, Upper Back, Biceps",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "rowing_machine",
            name = "Concept Rowing Machine",
            category = "Cardio & Back",
            description = "Air resistance ergometer rower for full-body explosive cardio and back endurance.",
            targetMuscles = "Back, Legs, Core, Heart Rate",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "battle_ropes",
            name = "Battle Ropes",
            category = "Functional",
            description = "Heavy anchor ropes for slamming, waves, and explosive upper body metabolic conditioning.",
            targetMuscles = "Shoulders, Arms, Core, Cardio",
            isSelectedByDefault = false
        ),
        GymAppliance(
            id = "foam_roller",
            name = "Foam Roller",
            category = "Recovery",
            description = "High-density myofascial release roller for muscle recovery, flexibility, and post-workout relief.",
            targetMuscles = "Quads, IT Band, Lats, Calves, Recovery",
            isSelectedByDefault = true
        ),
        GymAppliance(
            id = "preacher_bench",
            name = "Preacher Curl Bench",
            category = "Benches",
            description = "Padded isolation bench that locks arms in place for strict peak bicep curls.",
            targetMuscles = "Biceps Peak, Brachialis",
            isSelectedByDefault = false
        )
    )
}
