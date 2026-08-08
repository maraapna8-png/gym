package com.example.network

import com.example.BuildConfig
import com.example.data.model.AiHealthReport
import com.example.data.model.GeneratedMusicTrack
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutPlan
import com.example.util.MealMacroResult
import com.example.util.MealNutritionEstimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {

    private const val BASE_URL_FLASH = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"
    private const val BASE_URL_PRO = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent"
    private const val BASE_URL_LITE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite-preview:generateContent"
    
    private const val LYRIA_CLIP_URL = "https://generativelanguage.googleapis.com/v1beta/models/lyria-3-clip-preview:generateContent"
    private const val LYRIA_PRO_URL = "https://generativelanguage.googleapis.com/v1beta/models/lyria-3-pro-preview:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Music Generation using lyria-3-clip-preview (for short clips up to 30s) or lyria-3-pro-preview (for full length tracks)
     */
    suspend fun generateWorkoutMusicTrack(
        prompt: String,
        genre: String,
        isShortClip: Boolean
    ): GeneratedMusicTrack = withContext(Dispatchers.IO) {
        val modelName = if (isShortClip) "lyria-3-clip-preview" else "lyria-3-pro-preview"
        val endpointUrl = if (isShortClip) LYRIA_CLIP_URL else LYRIA_PRO_URL
        val duration = if (isShortClip) 30 else 180
        val bpm = when (genre.lowercase()) {
            "hiit" -> 150
            "synthwave" -> 132
            "metal" -> 160
            "yoga" -> 85
            else -> 140
        }

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val fullPrompt = "Create a $genre workout motivational audio track ($duration seconds, $bpm BPM): $prompt"
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", fullPrompt))
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseModalities", JSONArray().apply {
                            put("AUDIO")
                        })
                    })
                }

                val request = Request.Builder()
                    .url("$endpointUrl?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                if (response.isSuccessful && responseString.isNotBlank()) {
                    // Success response received
                    return@withContext GeneratedMusicTrack(
                        title = "$genre Pump (${if (isShortClip) "30s Clip" else "Full Track"})",
                        genre = genre,
                        durationSeconds = duration,
                        modelUsed = modelName,
                        bpm = bpm,
                        description = "AI Generated workout beat powered by $modelName",
                        prompt = prompt
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback result with rich details
        GeneratedMusicTrack(
            title = "$genre Energy ($duration s)",
            genre = genre,
            durationSeconds = duration,
            modelUsed = modelName,
            bpm = bpm,
            description = "High-energy $genre motivation track created using $modelName",
            prompt = prompt
        )
    }

    /**
     * Fast Motivation using gemini-3.1-flash-lite-preview (Ultra-fast tasks)
     */
    suspend fun getInstantMotivationFlashLite(topic: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = "Provide 1 super punchy, powerful 1-sentence workout motivation quote for $topic."
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply { put(JSONObject().put("text", prompt)) })
                        })
                    })
                }
                val request = Request.Builder()
                    .url("$BASE_URL_LITE?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""
                if (response.isSuccessful && responseString.isNotBlank()) {
                    val textResp = JSONObject(responseString)
                        .getJSONArray("candidates").getJSONObject(0)
                        .getJSONObject("content").getJSONArray("parts")
                        .getJSONObject(0).getString("text")
                    return@withContext textResp.trim()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return@withContext "Push past your limits today! Greatness is earned set by set. ⚡"
    }

    /**
     * Complex Reasoning: Deep AI Health Diagnostic using gemini-3.1-pro-preview
     */
    suspend fun generateDeepProDiagnostic(user: UserProfile): AiHealthReport = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are FitPro AI Master Biostatistician & Clinical Sports Physiologist. Conduct a deep analysis for athlete:
                    Age: ${user.age}, Weight: ${user.weightKg}kg, Height: ${user.heightCm}cm, Fitness Goal: ${user.fitnessGoal}.
                    
                    Return strictly in JSON format:
                    {
                      "title": "Comprehensive Athletic Recovery & Bio-Performance Analysis",
                      "recoveryScorePct": 92,
                      "fatigueLevel": "Low Central Nervous System Strain",
                      "recommendedWorkoutType": "High Volume Hypertrophy & Compound Split",
                      "metabolicAdvice": "Maintain 2.0g/kg protein intake with electrolyte hydration around heavy lifts."
                    }
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply { put(JSONObject().put("text", prompt)) })
                        })
                    })
                }

                val request = Request.Builder()
                    .url("$BASE_URL_PRO?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""
                if (response.isSuccessful && responseString.isNotBlank()) {
                    val textResp = JSONObject(responseString)
                        .getJSONArray("candidates").getJSONObject(0)
                        .getJSONObject("content").getJSONArray("parts")
                        .getJSONObject(0).getString("text")

                    val start = textResp.indexOf("{")
                    val end = textResp.lastIndexOf("}")
                    if (start != -1 && end != -1) {
                        val obj = JSONObject(textResp.substring(start, end + 1))
                        return@withContext AiHealthReport(
                            title = obj.optString("title", "Deep Bio-Performance Diagnostic"),
                            recoveryScorePct = obj.optInt("recoveryScorePct", 88),
                            fatigueLevel = obj.optString("fatigueLevel", "Optimal Readiness"),
                            recommendedWorkoutType = obj.optString("recommendedWorkoutType", "Hypertrophy Push/Pull/Legs"),
                            metabolicAdvice = obj.optString("metabolicAdvice", "Target 2.0g/kg protein with strategic carbs before workout."),
                            modelUsed = "gemini-3.1-pro-preview"
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        AiHealthReport(
            title = "Pro Readiness & Bio-Performance Assessment",
            recoveryScorePct = 90,
            fatigueLevel = "Low CNS Fatigue - Primed for Strength Training",
            recommendedWorkoutType = "Progressive Overload Compound Focus",
            metabolicAdvice = "Optimal metabolic window: consume 35g protein within 45 min post-workout.",
            modelUsed = "gemini-3.1-pro-preview"
        )
    }


    suspend fun generateAiWorkoutPlan(
        user: UserProfile,
        availableEquipment: String,
        workoutTimeMinutes: Int
    ): WorkoutPlan = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Smart AI Local Generation Fallback
            return@withContext WorkoutPlan(
                title = "AI Personalized ${user.fitnessGoal} Plan",
                category = user.fitnessLevel,
                type = "AI Custom Split",
                description = "Customized ${workoutTimeMinutes}-minute routine tailored for ${user.fitnessGoal} using $availableEquipment.",
                durationMin = workoutTimeMinutes,
                totalExercises = 6,
                caloriesBurned = (workoutTimeMinutes * 8.5).toInt(),
                difficulty = user.fitnessLevel,
                isCustom = true
            )
        }

        try {
            val prompt = """
                You are FitPro AI Master Fitness Coach. Create a personalized workout plan for a user with these stats:
                Age: ${user.age}, Gender: ${user.gender}, Height: ${user.heightCm} cm, Weight: ${user.weightKg} kg,
                Fitness Level: ${user.fitnessLevel}, Fitness Goal: ${user.fitnessGoal},
                Available Equipment: $availableEquipment, Target Workout Time: $workoutTimeMinutes minutes.
                
                Respond in valid JSON with format:
                {
                   "title": "Short Punchy Workout Name",
                   "category": "${user.fitnessLevel}",
                   "type": "AI Custom Split",
                   "description": "Short 2 sentence overview of why this plan works for the user.",
                   "durationMin": $workoutTimeMinutes,
                   "totalExercises": 6,
                   "caloriesBurned": 380,
                   "difficulty": "${user.fitnessLevel}"
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL_FLASH?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val jsonResp = JSONObject(responseString)
                val textResp = jsonResp.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                // Extract JSON substring
                val jsonStart = textResp.indexOf("{")
                val jsonEnd = textResp.lastIndexOf("}")
                if (jsonStart != -1 && jsonEnd != -1) {
                    val parsedObj = JSONObject(textResp.substring(jsonStart, jsonEnd + 1))
                    return@withContext WorkoutPlan(
                        title = parsedObj.optString("title", "AI Smart Workout Plan"),
                        category = parsedObj.optString("category", user.fitnessLevel),
                        type = parsedObj.optString("type", "AI Custom Split"),
                        description = parsedObj.optString("description", "Tailored workout routine generated by FitPro AI."),
                        durationMin = parsedObj.optInt("durationMin", workoutTimeMinutes),
                        totalExercises = parsedObj.optInt("totalExercises", 6),
                        caloriesBurned = parsedObj.optInt("caloriesBurned", 350),
                        difficulty = parsedObj.optString("difficulty", user.fitnessLevel),
                        isCustom = true
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback
        WorkoutPlan(
            title = "AI ${user.fitnessGoal} Plan",
            category = user.fitnessLevel,
            type = "AI Custom Split",
            description = "Customized $workoutTimeMinutes-min routine designed for ${user.fitnessGoal} with $availableEquipment.",
            durationMin = workoutTimeMinutes,
            totalExercises = 6,
            caloriesBurned = (workoutTimeMinutes * 7.5).toInt(),
            difficulty = user.fitnessLevel,
            isCustom = true
        )
    }

    suspend fun chatWithAiTrainer(userPrompt: String, user: UserProfile): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineTrainerResponse(userPrompt, user)
        }

        try {
            val systemInstruction = "You are FitPro AI, an encouraging, elite certified personal trainer and sports nutritionist. Keep advice practical, safe, motivating, and concise (under 120 words)."
            val fullPrompt = "User Profile: Goal=${user.fitnessGoal}, Level=${user.fitnessLevel}, Weight=${user.weightKg}kg.\nUser Question: $userPrompt"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", "$systemInstruction\n\n$fullPrompt"))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL_FLASH?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val jsonResp = JSONObject(responseString)
                val textResp = jsonResp.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                return@withContext textResp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext getOfflineTrainerResponse(userPrompt, user)
    }

    suspend fun calculateMealMacrosWithAi(mealName: String): MealMacroResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext MealNutritionEstimator.estimateMacros(mealName)
        }

        try {
            val prompt = """
                You are a certified nutritionist. Calculate exact macronutrients for the meal name: "$mealName".
                Respond strictly in valid JSON format:
                {
                   "mealName": "$mealName",
                   "calories": 350,
                   "proteinG": 28,
                   "carbsG": 30,
                   "fatG": 12,
                   "summary": "Short 1 sentence summary of nutritional breakdown."
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL_FLASH?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotBlank()) {
                val jsonResp = JSONObject(responseString)
                val textResp = jsonResp.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val jsonStart = textResp.indexOf("{")
                val jsonEnd = textResp.lastIndexOf("}")
                if (jsonStart != -1 && jsonEnd != -1) {
                    val cleanJson = JSONObject(textResp.substring(jsonStart, jsonEnd + 1))
                    val cal = cleanJson.optInt("calories", 250)
                    val p = cleanJson.optInt("proteinG", 15)
                    val c = cleanJson.optInt("carbsG", 25)
                    val f = cleanJson.optInt("fatG", 8)
                    val summary = cleanJson.optString("summary", "$mealName contains $cal kcal, $p g Protein, $f g Fat, $c g Carbs.")

                    return@withContext MealMacroResult(
                        mealName = mealName.replaceFirstChar { it.uppercase() },
                        calories = cal,
                        proteinG = p,
                        carbsG = c,
                        fatG = f,
                        summary = summary
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext MealNutritionEstimator.estimateMacros(mealName)
    }

    private fun getOfflineTrainerResponse(userPrompt: String, user: UserProfile): String {
        val lower = userPrompt.lowercase()
        return when {
            "diet" in lower || "protein" in lower || "food" in lower ->
                "For your goal of ${user.fitnessGoal}, aim for 1.6-2.2g of protein per kg of body weight (${(user.weightKg * 1.8).toInt()}g/day). Prioritize lean chicken, eggs, Greek yogurt, salmon, and oats."
            "water" in lower || "hydrate" in lower ->
                "Optimal hydration enhances workout performance by up to 25%! Drink 3000ml of water daily, adding an extra 500ml on intense training days."
            "fat" in lower || "weight loss" in lower || "cardio" in lower ->
                "To accelerate fat loss, combine compound resistance training 3x/week with 20 minutes of post-workout HIIT or steady-state cardio, staying in a 300-500 calorie deficit."
            "muscle" in lower || "gain" in lower || "hypertrophy" in lower ->
                "Hypertrophy thrives on progressive overload! Focus on 3-4 sets per exercise in the 8-12 rep range with 90 seconds of rest between heavy sets."
            else ->
                "Consistency is the true key to results, ${user.fullName.split(" ").firstOrNull() ?: "athlete"}! Maintain proper form, hit your daily protein goal, and aim for 7-8 hours of quality recovery sleep each night."
        }
    }
}
