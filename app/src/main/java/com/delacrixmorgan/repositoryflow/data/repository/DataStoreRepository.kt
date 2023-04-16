package com.delacrixmorgan.repositoryflow.data.repository

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.delacrixmorgan.repositoryflow.data.DogData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : DogRepository {
    companion object {
        const val DATA_STORE_DOG_REPOSITORY = "DataStoreDogRepository"
        const val KEY_NAME = "Name"
        const val KEY_FAVOURITE_TOY = "FavouriteToy"
        const val KEY_OWNER_EMAIL = "OwnerEmail"
    }

    private val dataStore by lazy {
        PreferenceDataStoreFactory.create(produceFile = { appContext.preferencesDataStoreFile(DATA_STORE_DOG_REPOSITORY) })
    }

    override suspend fun saveName(value: String) {
        dataStore.edit { it[stringPreferencesKey(KEY_NAME)] = value }
    }

    override suspend fun saveFavouriteToy(value: String) {
        dataStore.edit { it[stringPreferencesKey(KEY_FAVOURITE_TOY)] = value }
    }

    override suspend fun saveOwnerEmail(value: String) {
        dataStore.edit { it[stringPreferencesKey(KEY_OWNER_EMAIL)] = value }
    }

    private fun getName(): Flow<String?> = dataStore.data.map { it[stringPreferencesKey(KEY_NAME)] }

    private fun getFavouriteToy(): Flow<String?> = dataStore.data.map { it[stringPreferencesKey(KEY_FAVOURITE_TOY)] }
    
    private fun getOwnerEmail(): Flow<String?> = dataStore.data.map { it[stringPreferencesKey(KEY_OWNER_EMAIL)] }

    fun observeDogData(): Flow<DogData> {
        return combine(getName(), getFavouriteToy(), getOwnerEmail()) { name, favouriteToy, ownerEmail ->
            DogData(name, favouriteToy, ownerEmail)
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}