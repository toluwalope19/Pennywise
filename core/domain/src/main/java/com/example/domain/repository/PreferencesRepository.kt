package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getHasSeenOnboarding(): Flow<Boolean>
    suspend fun setHasSeenOnboarding(seen: Boolean)
    fun getCurrencySymbol(): Flow<String>
    suspend fun setCurrency(symbol: String, code: String)
}