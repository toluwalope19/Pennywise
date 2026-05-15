package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "pennywise_preferences")

@Singleton
class PennywiseDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val preferences: Flow<AppPreferences> = context.dataStore.data
        .map { prefs ->
            AppPreferences(
                currencySymbol = prefs[PreferencesKeys.CURRENCY_SYMBOL] ?: "₦",
                currencyCode = prefs[PreferencesKeys.CURRENCY_CODE] ?: "NGN",
                isDarkTheme = prefs[PreferencesKeys.IS_DARK_THEME] ?: true,
                spendingAlertsEnabled = prefs[PreferencesKeys.SPENDING_ALERTS_ENABLED] ?: true,
                weeklySummaryEnabled = prefs[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] ?: false
            )
        }

    suspend fun setCurrency(symbol: String, code: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.CURRENCY_SYMBOL] = symbol
            prefs[PreferencesKeys.CURRENCY_CODE] = code
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_DARK_THEME] = enabled
        }
    }

    suspend fun setSpendingAlerts(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SPENDING_ALERTS_ENABLED] = enabled
        }
    }

    suspend fun setWeeklySummary(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.WEEKLY_SUMMARY_ENABLED] = enabled
        }
    }
}