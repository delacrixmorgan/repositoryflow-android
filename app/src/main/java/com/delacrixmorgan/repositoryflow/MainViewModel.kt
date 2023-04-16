package com.delacrixmorgan.repositoryflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacrixmorgan.repositoryflow.data.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dogRepository: DogRepository
) : ViewModel() {
    val favouriteToys = listOf("Rubber Duck", "Plastic Bone", "Snorlax Plushie")
    var expanded by mutableStateOf(false)

    var name by mutableStateOf(TextFieldValue(dogRepository.getName() ?: ""))
        private set
    var favouriteToy by mutableStateOf(dogRepository.getFavouriteToy() ?: favouriteToys[0])
        private set
    var ownerEmail by mutableStateOf(TextFieldValue(dogRepository.getOwnerEmail() ?: ""))
        private set

    var previewName by mutableStateOf(TextFieldValue())
        private set
    var previewFavouriteToy by mutableStateOf("")
        private set
    var previewOwnerEmail by mutableStateOf(TextFieldValue())
        private set

    init {
        viewModelScope.launch { observeDog() }
    }

    fun saveDogDetails(
        name: TextFieldValue? = null,
        favouriteToy: String? = null,
        ownerEmail: TextFieldValue? = null,
    ) {
        name?.let {
            this.name = it
            dogRepository.saveName(it.text)
        }
        favouriteToy?.let {
            this.favouriteToy = it
            dogRepository.saveFavouriteToy(it)
        }
        ownerEmail?.let {
            this.ownerEmail = it
            dogRepository.saveOwnerEmail(it.text)
        }
    }

    private suspend fun observeDog() {
        dogRepository.observeDog().collectLatest {
            previewName = TextFieldValue(it.name ?: "")
            previewFavouriteToy = it.favouriteToy ?: favouriteToys[0]
            previewOwnerEmail = TextFieldValue(it.ownerEmail ?: "")
        }
    }

    fun clearDogDetails() {
        dogRepository.clear()

        name = TextFieldValue()
        favouriteToy = favouriteToys[0]
        ownerEmail = TextFieldValue()

        previewName = TextFieldValue()
        previewFavouriteToy = favouriteToys[0]
        previewOwnerEmail = TextFieldValue()
    }
}
