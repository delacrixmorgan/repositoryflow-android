package com.delacrixmorgan.repositoryflow.data.repository

import com.delacrixmorgan.repositoryflow.data.DogData
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FlowRepository @Inject constructor() : DogRepository {
    val dogFlow = MutableStateFlow(DogData())

    override suspend fun saveName(value: String) {
        dogFlow.emit(dogFlow.value.copy(name = value))
    }

    override suspend fun saveFavouriteToy(value: String) {
        dogFlow.emit(dogFlow.value.copy(favouriteToy = value))
    }

    override suspend fun saveOwnerEmail(value: String) {
        dogFlow.emit(dogFlow.value.copy(ownerEmail = value))
    }

    override suspend fun clear() {
        dogFlow.emit(DogData())
    }
}