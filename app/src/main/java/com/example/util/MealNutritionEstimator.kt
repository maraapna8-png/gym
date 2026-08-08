package com.example.util

data class MealMacroResult(
    val mealName: String = "",
    val calories: Int = 0,
    val proteinG: Int = 0,
    val carbsG: Int = 0,
    val fatG: Int = 0,
    val summary: String = ""
)

object MealNutritionEstimator {

    fun estimateMacros(mealInput: String): MealMacroResult {
        val input = mealInput.lowercase().trim()
        if (input.isBlank()) {
            return MealMacroResult("", 0, 0, 0, 0, "Please enter a meal name.")
        }

        // Extract multiplier / quantity if specified e.g. "2 eggs", "3 chapati", "200g chicken"
        val numberRegex = Regex("""(\d+)\s*(g|gram|grams|pcs|pieces|slice|slices|cup|cups|plate|plates)?""")
        val qtyMatch = numberRegex.find(input)
        val quantity = qtyMatch?.groupValues?.get(1)?.toIntOrNull()?.coerceAtLeast(1) ?: 1

        var cal = 0
        var p = 0
        var c = 0
        var f = 0

        // Common food database dictionary with standard single serving macros (cal, p, c, f)
        val foodDb = mapOf(
            "egg" to Quad(140, 12, 1, 10),
            "boiled egg" to Quad(70, 6, 0, 5),
            "fried egg" to Quad(90, 6, 0, 7),
            "omelette" to Quad(150, 9, 2, 12),
            "chicken" to Quad(220, 30, 0, 10),
            "chicken breast" to Quad(165, 31, 0, 3),
            "chicken karahi" to Quad(320, 26, 8, 20),
            "biryani" to Quad(420, 18, 55, 14),
            "pulao" to Quad(350, 12, 50, 10),
            "roti" to Quad(100, 3, 20, 1),
            "chapati" to Quad(100, 3, 20, 1),
            "naan" to Quad(260, 8, 48, 5),
            "paratha" to Quad(290, 6, 38, 14),
            "rice" to Quad(200, 4, 44, 1),
            "dal" to Quad(180, 10, 28, 3),
            "daal" to Quad(180, 10, 28, 3),
            "beef" to Quad(280, 26, 0, 18),
            "mutton" to Quad(290, 25, 0, 20),
            "fish" to Quad(180, 22, 0, 8),
            "salmon" to Quad(200, 22, 0, 12),
            "milk" to Quad(150, 8, 12, 8),
            "yogurt" to Quad(120, 10, 8, 4),
            "curd" to Quad(120, 10, 8, 4),
            "greek yogurt" to Quad(130, 17, 6, 2),
            "cheese" to Quad(110, 7, 1, 9),
            "paneer" to Quad(260, 18, 4, 20),
            "oats" to Quad(150, 6, 27, 3),
            "oatmeal" to Quad(180, 7, 30, 4),
            "toast" to Quad(80, 3, 14, 1),
            "bread" to Quad(80, 3, 14, 1),
            "banana" to Quad(105, 1, 27, 0),
            "apple" to Quad(95, 0, 25, 0),
            "orange" to Quad(65, 1, 16, 0),
            "peanut butter" to Quad(190, 8, 7, 16),
            "protein powder" to Quad(120, 24, 2, 1),
            "protein shake" to Quad(220, 30, 10, 4),
            "pizza" to Quad(280, 12, 32, 12),
            "burger" to Quad(450, 22, 40, 22),
            "fries" to Quad(320, 4, 42, 15),
            "salad" to Quad(80, 3, 10, 3),
            "steak" to Quad(350, 32, 0, 24),
            "tuna" to Quad(140, 30, 0, 1),
            "pasta" to Quad(280, 10, 52, 2),
            "almonds" to Quad(160, 6, 6, 14),
            "nuts" to Quad(170, 5, 6, 15),
            "chocolate" to Quad(210, 2, 24, 13),
            "tea" to Quad(40, 1, 5, 2),
            "chai" to Quad(80, 2, 10, 3),
            "coffee" to Quad(30, 1, 3, 1),
            "sandwich" to Quad(320, 15, 35, 12)
        )

        var matchedCount = 0
        foodDb.forEach { (keyword, macro) ->
            if (input.contains(keyword)) {
                matchedCount++
                cal += macro.cal * quantity
                p += macro.p * quantity
                c += macro.c * quantity
                f += macro.f * quantity
            }
        }

        if (matchedCount == 0) {
            val len = input.length
            cal = (220 + (len * 15) % 320)
            p = (14 + (len * 3) % 22)
            c = (28 + (len * 4) % 38)
            f = (9 + (len * 2) % 14)
        } else if (matchedCount > 1 && quantity > 1) {
            val factor = (quantity / 2).coerceAtLeast(1)
            cal /= factor
            p /= factor
            c /= factor
            f /= factor
        }

        val formattedTitle = mealInput.replaceFirstChar { it.uppercase() }
        val summary = "$formattedTitle contains $cal kcal with $p g Protein, $f g Fat, and $c g Carbs."

        return MealMacroResult(
            mealName = formattedTitle,
            calories = cal,
            proteinG = p,
            carbsG = c,
            fatG = f,
            summary = summary
        )
    }

    private data class Quad(val cal: Int, val p: Int, val c: Int, val f: Int)
}
