package com.lavka.calculator.data

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

data class AppSettings(
    val agreementAccepted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultWeight: String = "50",
    val defaultPlasticPrice: String = "2.5",
    val defaultPrintTime: String = "3",
    val defaultHourCost: String = "50",
    val defaultMarkup: String = "100",
    val defaultComplexity: String = "1.0",
    val currencySymbol: String = "₽",
    val decimalPlaces: Int = 2,
    val useDefaultsOnStart: Boolean = true
)

data class CalculatorInput(
    val weight: String = "",
    val plasticPrice: String = "",
    val printTime: String = "",
    val hourCost: String = "",
    val markup: String = "",
    val complexity: String = ""
)

data class CalculatorResult(
    val materialCost: Double,
    val timeCost: Double,
    val markup: Double,
    val subtotal: Double,
    val finalPrice: Double
)

object CalculatorEngine {
    fun calculate(
        weight: Double,
        plasticPrice: Double,
        printTime: Double,
        hourCost: Double,
        markup: Double,
        complexity: Double
    ): CalculatorResult {
        val materialCost = weight * plasticPrice
        val timeCost = printTime * hourCost
        val subtotal = materialCost + timeCost + markup
        val finalPrice = subtotal * complexity
        return CalculatorResult(
            materialCost = materialCost,
            timeCost = timeCost,
            markup = markup,
            subtotal = subtotal,
            finalPrice = finalPrice
        )
    }
}
