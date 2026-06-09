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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lavka.calculator.data.AppSettings
import com.lavka.calculator.data.ThemeMode
import com.lavka.calculator.ui.components.CalculatorField
import com.lavka.calculator.ui.theme.OrangePrimary

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    onResetAll: () -> Unit
) {
    var localSettings by remember(settings) { mutableStateOf(settings) }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Сбросить настройки?") },
            text = { Text("Все пользовательские настройки будут возвращены к значениям по умолчанию.") },
            confirmButton = {
                TextButton(onClick = {
                    onResetAll()
                    showResetDialog = false
                }) {
                    Text("Сбросить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Полная настройка калькулятора и интерфейса",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSection(title = "Тема оформления") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = localSettings.themeMode == mode,
                        onClick = {
                            localSettings = localSettings.copy(themeMode = mode)
                            onSettingsChange(localSettings)
                        },
                        label = {
                            Text(
                                when (mode) {
                                    ThemeMode.LIGHT -> "Светлая"
                                    ThemeMode.DARK -> "Тёмная"
                                    ThemeMode.SYSTEM -> "Системная"
                                }
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(title = "Отображение") {
            OutlinedTextField(
                value = localSettings.currencySymbol,
                onValueChange = {
                    if (it.length <= 3) {
                        localSettings = localSettings.copy(currencySymbol = it)
                        onSettingsChange(localSettings)
                    }
                },
                label = { Text("Символ валюты") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Знаков после запятой: ${localSettings.decimalPlaces}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (0..4).forEach { places ->
                    FilterChip(
                        selected = localSettings.decimalPlaces == places,
                        onClick = {
                            localSettings = localSettings.copy(decimalPlaces = places)
                            onSettingsChange(localSettings)
                        },
                        label = { Text("$places") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(title = "Значения по умолчанию") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Подставлять при запуске",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Автоматически заполнять поля калькулятора",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = localSettings.useDefaultsOnStart,
                    onCheckedChange = {
                        localSettings = localSettings.copy(useDefaultsOnStart = it)
                        onSettingsChange(localSettings)
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            CalculatorField(
                label = "Вес по умолчанию",
                value = localSettings.defaultWeight,
                onValueChange = {
                    localSettings = localSettings.copy(defaultWeight = it)
                    onSettingsChange(localSettings)
                },
                suffix = "г"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalculatorField(
                label = "Цена пластика по умолчанию",
                value = localSettings.defaultPlasticPrice,
                onValueChange = {
                    localSettings = localSettings.copy(defaultPlasticPrice = it)
                    onSettingsChange(localSettings)
                },
                suffix = localSettings.currencySymbol
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalculatorField(
                label = "Время печати по умолчанию",
                value = localSettings.defaultPrintTime,
                onValueChange = {
                    localSettings = localSettings.copy(defaultPrintTime = it)
                    onSettingsChange(localSettings)
                },
                suffix = "ч"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalculatorField(
                label = "Стоимость часа по умолчанию",
                value = localSettings.defaultHourCost,
                onValueChange = {
                    localSettings = localSettings.copy(defaultHourCost = it)
                    onSettingsChange(localSettings)
                },
                suffix = "${localSettings.currencySymbol}/ч"
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalculatorField(
                label = "Наценка по умолчанию",
                value = localSettings.defaultMarkup,
                onValueChange = {
                    localSettings = localSettings.copy(defaultMarkup = it)
                    onSettingsChange(localSettings)
                },
                suffix = localSettings.currencySymbol
            )
            Spacer(modifier = Modifier.height(8.dp))
            CalculatorField(
                label = "Коэффициент сложности по умолчанию",
                value = localSettings.defaultComplexity,
                onValueChange = {
                    localSettings = localSettings.copy(defaultComplexity = it)
                    onSettingsChange(localSettings)
                },
                suffix = "×"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Сбросить все настройки")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OrangePrimary
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            content()
        }
    }
}
