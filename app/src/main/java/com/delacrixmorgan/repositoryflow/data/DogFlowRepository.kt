package com.delacrixmorgan.repositoryflow.data

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DogFlowRepository @Inject constructor() {
    val dogFlow = MutableStateFlow(DogData())

    suspend fun saveName(value: String) {
        dogFlow.emit(dogFlow.value.copy(name = value))
    }

    suspend fun saveFavouriteToy(value: String) {
        dogFlow.emit(dogFlow.value.copy(favouriteToy = value))
    }

    suspend fun saveOwnerEmail(value: String) {
        dogFlow.emit(dogFlow.value.copy(ownerEmail = value))
    }

    suspend fun clear() {
        dogFlow.emit(DogData())
    }
}