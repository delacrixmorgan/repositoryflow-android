package com.delacrixmorgan.repositoryflow.data

class DogData(
    val name: String? = null,
    val favouriteToy: String? = null,
    val ownerEmail: String? = null,
) {
    fun copy(
        name: String? = this.name,
        favouriteToy: String? = this.favouriteToy,
        ownerEmail: String? = this.ownerEmail
    ) = DogData(name, favouriteToy, ownerEmail)
}