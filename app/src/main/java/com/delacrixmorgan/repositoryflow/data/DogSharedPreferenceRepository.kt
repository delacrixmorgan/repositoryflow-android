package com.delacrixmorgan.repositoryflow.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class DogSharedPreferenceRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    companion object {
        const val SHARED_PREFERENCE_DOG_REPOSITORY = "SharedPreferenceDogRepository"
        const val KEY_NAME = "Name"
        const val KEY_FAVOURITE_TOY = "FavouriteToy"
        const val KEY_OWNER_EMAIL = "OwnerEmail"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        appContext.getSharedPreferences(SHARED_PREFERENCE_DOG_REPOSITORY, Context.MODE_PRIVATE)
    }

    fun saveName(value: String) = sharedPreferences.edit { putString(KEY_NAME, value) }

    fun getName(): String? = sharedPreferences.getStringOrNull(KEY_NAME)

    fun saveFavouriteToy(value: String) = sharedPreferences.edit { putString(KEY_FAVOURITE_TOY, value) }

    fun getFavouriteToy(): String? = sharedPreferences.getStringOrNull(KEY_FAVOURITE_TOY)

    fun saveOwnerEmail(value: String) = sharedPreferences.edit { putString(KEY_OWNER_EMAIL, value) }

    fun getOwnerEmail(): String? = sharedPreferences.getStringOrNull(KEY_OWNER_EMAIL)

    private fun getDogData() = DogData(
        name = getName(),
        favouriteToy = getFavouriteToy(),
        ownerEmail = getOwnerEmail()
    )

    fun observeDog(): Flow<DogData> {
        val flow = channelFlow {
            val scope = this
            trySend(getDogData())

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                scope.launch { trySend(getDogData()) }
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }
        return flow.flowOn(Dispatchers.IO)
    }

    fun clear() = sharedPreferences.edit { clear() }

    private fun SharedPreferences.getStringOrNull(key: String): String? {
        return if (contains(key)) getString(key, "") else null
    }
}