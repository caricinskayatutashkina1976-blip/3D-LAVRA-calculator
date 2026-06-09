package com.lavka.calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lavka.calculator.data.AppSettings
import com.lavka.calculator.data.CalculatorEngine
import com.lavka.calculator.data.CalculatorInput
import com.lavka.calculator.data.CalculatorResult
import com.lavka.calculator.data.PreferencesRepository
import com.lavka.calculator.data.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    private val _input = MutableStateFlow(CalculatorInput())
    val input: StateFlow<CalculatorInput> = _input.asStateFlow()

    private val _result = MutableStateFlow<CalculatorResult?>(null)
    val result: StateFlow<CalculatorResult?> = _result.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showAgreement = MutableStateFlow(false)
    val showAgreement: StateFlow<Boolean> = _showAgreement.asStateFlow()

    private var defaultsApplied = false

    init {
        viewModelScope.launch {
            settings.collect { appSettings ->
                if (!appSettings.agreementAccepted) {
                    _showAgreement.value = true
                }
                if (!defaultsApplied && appSettings.useDefaultsOnStart) {
                    applyDefaults(appSettings)
                    defaultsApplied = true
                }
            }
        }
    }

    fun acceptAgreement() {
        viewModelScope.launch {
            repository.acceptAgreement()
            _showAgreement.value = false
        }
    }

    fun declineAgreement() {
        _showAgreement.value = true
    }

    fun showAgreementScreen() {
        _showAgreement.value = true
    }

    fun dismissAgreementView() {
        if (settings.value.agreementAccepted) {
            _showAgreement.value = false
        }
    }

    fun updateInput(transform: (CalculatorInput) -> CalculatorInput) {
        _input.value = transform(_input.value)
        _errorMessage.value = null
    }

    fun calculate() {
        val current = _input.value
        val weight = current.weight.toDoubleOrNull()
        val plasticPrice = current.plasticPrice.toDoubleOrNull()
        val printTime = current.printTime.toDoubleOrNull()
        val hourCost = current.hourCost.toDoubleOrNull()
        val markup = current.markup.toDoubleOrNull()
        val complexity = current.complexity.toDoubleOrNull()

        when {
            weight == null || weight < 0 ->
                _errorMessage.value = "Введите корректный вес изделия (г)"
            plasticPrice == null || plasticPrice < 0 ->
                _errorMessage.value = "Введите корректную цену пластика за грамм"
            printTime == null || printTime < 0 ->
                _errorMessage.value = "Введите корректное время печати (ч)"
            hourCost == null || hourCost < 0 ->
                _errorMessage.value = "Введите корректную стоимость часа печати"
            markup == null || markup < 0 ->
                _errorMessage.value = "Введите корректную наценку"
            complexity == null || complexity <= 0 ->
                _errorMessage.value = "Коэффициент сложности должен быть больше 0"
            else -> {
                _result.value = CalculatorEngine.calculate(
                    weight, plasticPrice, printTime, hourCost, markup, complexity
                )
                _errorMessage.value = null
            }
        }
    }

    fun clearResult() {
        _result.value = null
        _errorMessage.value = null
    }

    fun resetInputToDefaults() {
        applyDefaults(settings.value)
        clearResult()
    }

    private fun applyDefaults(appSettings: AppSettings) {
        _input.value = CalculatorInput(
            weight = appSettings.defaultWeight,
            plasticPrice = appSettings.defaultPlasticPrice,
            printTime = appSettings.defaultPrintTime,
            hourCost = appSettings.defaultHourCost,
            markup = appSettings.defaultMarkup,
            complexity = appSettings.defaultComplexity
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            if (newSettings.useDefaultsOnStart) {
                applyDefaults(newSettings)
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        updateSettings(settings.value.copy(themeMode = mode))
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            repository.resetToDefaults()
            defaultsApplied = false
            clearResult()
        }
    }
}
