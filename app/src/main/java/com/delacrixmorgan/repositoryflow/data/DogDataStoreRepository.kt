package com.delacrixmorgan.repositoryflow.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DogDataStoreRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    companion object {
        const val DATA_STORE_DOG_REPOSITORY = "DataStoreDogRepository"
        const val KEY_NAME = "Name"
        const val KEY_FAVOURITE_TOY = "FavouriteToy"
        const val KEY_OWNER_EMAIL = "OwnerEmail"
    }

    private val dataStore by lazy {
        PreferenceDataStoreFactory.create(produceFile = { appContext.preferencesDataStoreFile(DATA_STORE_DOG_REPOSITORY) })
    }

    suspend fun saveName(value: String) = dataStore.edit { it[stringPreferencesKey(KEY_NAME)] = value }

    private fun getName(): Flow<String?> = dataStore.data.map { it[stringPreferencesKey(KEY_NAME)] }

    suspend fun saveFavouriteToy(value: String) = dataStore.edit { it[stringPreferencesKey(KEY_FAVOURITE_TOY)] = value }

    private fun getFavouriteToy(): Flow<String?> = dataStore.data.map { it[stringPreferencesKey(KEY_FAVOURITE_TOY)] }

    suspend fun saveOwnerEmail(value: String) = dataStore.edit { it[stringPreferencesKey(KEY_OWNER_EMAIL)] = value }

    private fun getOwnerEmail(): Flow<String?> = dataStore.data.map { it[stringPreferencesKey(KEY_OWNER_EMAIL)] }

    fun observeDogData(): Flow<DogData> {
        return combine(getName(), getFavouriteToy(), getOwnerEmail()) { name, favouriteToy, ownerEmail ->
            DogData(name, favouriteToy, ownerEmail)
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}