package com.lavka.calculator.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lavka_settings")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val AGREEMENT_ACCEPTED = booleanPreferencesKey("agreement_accepted")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_WEIGHT = stringPreferencesKey("default_weight")
        val DEFAULT_PLASTIC_PRICE = stringPreferencesKey("default_plastic_price")
        val DEFAULT_PRINT_TIME = stringPreferencesKey("default_print_time")
        val DEFAULT_HOUR_COST = stringPreferencesKey("default_hour_cost")
        val DEFAULT_MARKUP = stringPreferencesKey("default_markup")
        val DEFAULT_COMPLEXITY = stringPreferencesKey("default_complexity")
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val DECIMAL_PLACES = intPreferencesKey("decimal_places")
        val USE_DEFAULTS_ON_START = booleanPreferencesKey("use_defaults_on_start")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            agreementAccepted = prefs[Keys.AGREEMENT_ACCEPTED] ?: false,
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            defaultWeight = prefs[Keys.DEFAULT_WEIGHT] ?: "50",
            defaultPlasticPrice = prefs[Keys.DEFAULT_PLASTIC_PRICE] ?: "2.5",
            defaultPrintTime = prefs[Keys.DEFAULT_PRINT_TIME] ?: "3",
            defaultHourCost = prefs[Keys.DEFAULT_HOUR_COST] ?: "50",
            defaultMarkup = prefs[Keys.DEFAULT_MARKUP] ?: "100",
            defaultComplexity = prefs[Keys.DEFAULT_COMPLEXITY] ?: "1.0",
            currencySymbol = prefs[Keys.CURRENCY_SYMBOL] ?: "₽",
            decimalPlaces = prefs[Keys.DECIMAL_PLACES] ?: 2,
            useDefaultsOnStart = prefs[Keys.USE_DEFAULTS_ON_START] ?: true
        )
    }

    suspend fun acceptAgreement() {
        context.dataStore.edit { it[Keys.AGREEMENT_ACCEPTED] = true }
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AGREEMENT_ACCEPTED] = settings.agreementAccepted
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.DEFAULT_WEIGHT] = settings.defaultWeight
            prefs[Keys.DEFAULT_PLASTIC_PRICE] = settings.defaultPlasticPrice
            prefs[Keys.DEFAULT_PRINT_TIME] = settings.defaultPrintTime
            prefs[Keys.DEFAULT_HOUR_COST] = settings.defaultHourCost
            prefs[Keys.DEFAULT_MARKUP] = settings.defaultMarkup
            prefs[Keys.DEFAULT_COMPLEXITY] = settings.defaultComplexity
            prefs[Keys.CURRENCY_SYMBOL] = settings.currencySymbol
            prefs[Keys.DECIMAL_PLACES] = settings.decimalPlaces
            prefs[Keys.USE_DEFAULTS_ON_START] = settings.useDefaultsOnStart
        }
    }

    suspend fun resetToDefaults() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
