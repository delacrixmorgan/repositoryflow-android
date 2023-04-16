package com.delacrixmorgan.repositoryflow.data.repository

interface DogRepository {
    suspend fun saveName(value: String)
    suspend fun saveFavouriteToy(value: String)
    suspend fun saveOwnerEmail(value: String)
    suspend fun clear()
}