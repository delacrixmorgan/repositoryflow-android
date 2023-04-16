package com.delacrixmorgan.repositoryflow.data.repository

import com.delacrixmorgan.repositoryflow.data.DogData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class HashMapRepository @Inject constructor() : DogRepository {
    companion object {
        const val KEY_NAME = "Name"
        const val KEY_FAVOURITE_TOY = "FavouriteToy"
        const val KEY_OWNER_EMAIL = "OwnerEmail"
    }

    private val dogHashMap = ObservableHashMap<String, String>()

    override suspend fun saveName(value: String) {
        dogHashMap[KEY_NAME] = value
    }

    override suspend fun saveFavouriteToy(value: String) {
        dogHashMap[KEY_FAVOURITE_TOY] = value
    }

    override suspend fun saveOwnerEmail(value: String) {
        dogHashMap[KEY_OWNER_EMAIL] = value
    }

    fun getName() = dogHashMap[KEY_NAME]

    fun getFavouriteToy() = dogHashMap[KEY_FAVOURITE_TOY]

    fun getOwnerEmail() = dogHashMap[KEY_OWNER_EMAIL]

    private fun getDogData() = DogData(
        name = getName(),
        favouriteToy = getFavouriteToy(),
        ownerEmail = getOwnerEmail(),
    )

    fun observeDogData(): Flow<DogData> {
        val flow = channelFlow {
            val scope = this
            trySend(getDogData())

            dogHashMap.registerChangeListener {
                scope.launch { trySend(getDogData()) }
            }
            awaitClose { dogHashMap.unregisterChangeListener() }
        }
        return flow.flowOn(Dispatchers.IO)
    }

    override suspend fun clear() {
        dogHashMap.clear()
    }
}

class ObservableHashMap<K, V> : HashMap<K, V>() {
    private var listener: ((Pair<K, V?>) -> Unit)? = null

    fun registerChangeListener(listener: ((Pair<K, V?>) -> Unit)) {
        this.listener = listener
    }

    fun unregisterChangeListener() {
        listener = null
    }

    override fun put(key: K, value: V): V? {
        listener?.invoke(Pair(key, value))
        return super.put(key, value)
    }
}