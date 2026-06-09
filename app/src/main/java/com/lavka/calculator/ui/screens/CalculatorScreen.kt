package com.lavka.calculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lavka.calculator.data.AppSettings
import com.lavka.calculator.data.CalculatorInput
import com.lavka.calculator.data.CalculatorResult
import com.lavka.calculator.ui.components.CalculatorField
import com.lavka.calculator.ui.theme.OrangePrimary
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun CalculatorScreen(
    input: CalculatorInput,
    result: CalculatorResult?,
    errorMessage: String?,
    settings: AppSettings,
    onInputChange: (CalculatorInput) -> Unit,
    onCalculate: () -> Unit,
    onReset: () -> Unit
) {
    val currency = settings.currencySymbol
    val formatter = rememberPriceFormatter(settings.decimalPlaces)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "3D-LAVKA",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Калькулятор стоимости 3D-печати",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        CalculatorField(
            label = "Вес изделия",
            value = input.weight,
            onValueChange = { onInputChange(input.copy(weight = it)) },
            suffix = "г"
        )
        Spacer(modifier = Modifier.height(12.dp))
        CalculatorField(
            label = "Цена пластика за 1 грамм",
            value = input.plasticPrice,
            onValueChange = { onInputChange(input.copy(plasticPrice = it)) },
            suffix = currency
        )
        Spacer(modifier = Modifier.height(12.dp))
        CalculatorField(
            label = "Время печати",
            value = input.printTime,
            onValueChange = { onInputChange(input.copy(printTime = it)) },
            suffix = "ч"
        )
        Spacer(modifier = Modifier.height(12.dp))
        CalculatorField(
            label = "Стоимость часа печати",
            value = input.hourCost,
            onValueChange = { onInputChange(input.copy(hourCost = it)) },
            suffix = "$currency/ч"
        )
        Spacer(modifier = Modifier.height(12.dp))
        CalculatorField(
            label = "Наценка",
            value = input.markup,
            onValueChange = { onInputChange(input.copy(markup = it)) },
            suffix = currency
        )
        Spacer(modifier = Modifier.height(12.dp))
        CalculatorField(
            label = "Коэффициент сложности",
            value = input.complexity,
            onValueChange = { onInputChange(input.copy(complexity = it)) },
            suffix = "×"
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text(" Сброс", modifier = Modifier.padding(start = 4.dp))
            }
            Button(
                onClick = onCalculate,
                modifier = Modifier.weight(1.2f),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Text(" Рассчитать", modifier = Modifier.padding(start = 4.dp))
            }
        }

        if (result != null) {
            Spacer(modifier = Modifier.height(24.dp))
            ResultCard(result, currency, formatter, input.complexity)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResultCard(
    result: CalculatorResult,
    currency: String,
    formatter: DecimalFormat,
    complexity: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Результат расчёта",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            ResultRow("Себестоимость материалов", "${formatter.format(result.materialCost)} $currency")
            ResultRow("Стоимость времени печати", "${formatter.format(result.timeCost)} $currency")
            ResultRow("Наценка", "${formatter.format(result.markup)} $currency")
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
            ResultRow(
                label = "Промежуточная сумма",
                value = "${formatter.format(result.subtotal)} $currency",
                emphasized = false
            )
            ResultRow(
                label = "Коэффициент сложности",
                value = "× ${complexity.ifEmpty { "1" }}",
                emphasized = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Итоговая цена",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${formatter.format(result.finalPrice)} $currency",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, emphasized: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (emphasized) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = if (emphasized) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasized) FontWeight.Medium else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun rememberPriceFormatter(decimalPlaces: Int): DecimalFormat {
    val pattern = buildString {
        append("#,##0")
        if (decimalPlaces > 0) {
            append('.')
            repeat(decimalPlaces) { append('0') }
        }
    }
    return DecimalFormat(pattern, DecimalFormatSymbols(Locale("ru", "RU")))
}
