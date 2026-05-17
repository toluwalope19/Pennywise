package com.example.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
    val CURRENCY_CODE = stringPreferencesKey("currency_code")
    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    val SPENDING_ALERTS_ENABLED = booleanPreferencesKey("spending_alerts_enabled")
    val WEEKLY_SUMMARY_ENABLED = booleanPreferencesKey("weekly_summary_enabled")
    val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
}