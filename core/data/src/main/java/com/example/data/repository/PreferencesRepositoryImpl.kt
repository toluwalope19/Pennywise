package com.example.data.repository

import com.example.data.datastore.PennywiseDataStore
import com.example.domain.repository.PreferencesRepository


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: PennywiseDataStore
) : PreferencesRepository {

    override fun getHasSeenOnboarding(): Flow<Boolean> =
        dataStore.preferences.map { it.hasSeenOnboarding }

    override suspend fun setHasSeenOnboarding(seen: Boolean) =
        dataStore.setHasSeenOnboarding(seen)

    override fun getCurrencySymbol(): Flow<String> =
        dataStore.preferences.map { it.currencySymbol }

    override suspend fun setCurrency(symbol: String, code: String) =
        dataStore.setCurrency(symbol, code)
}