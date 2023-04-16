package com.delacrixmorgan.repositoryflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delacrixmorgan.repositoryflow.data.DogData
import com.delacrixmorgan.repositoryflow.data.repository.DataStoreRepository
import com.delacrixmorgan.repositoryflow.data.repository.FlowRepository
import com.delacrixmorgan.repositoryflow.data.repository.HashMapRepository
import com.delacrixmorgan.repositoryflow.data.repository.SharedPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferenceRepository: SharedPreferenceRepository,
    private val hashMapRepository: HashMapRepository,
    private val flowRepository: FlowRepository,
    private val dataSourceRepository: DataStoreRepository,
) : ViewModel() {
    val favouriteToys = listOf("Rubber Duck", "Plastic Bone", "Snorlax Plushie")
    var expanded by mutableStateOf(false)

    var name by mutableStateOf(TextFieldValue(""))
        private set
    var favouriteToy by mutableStateOf("")
        private set
    var ownerEmail by mutableStateOf(TextFieldValue(""))
        private set

    var previewName by mutableStateOf(TextFieldValue())
        private set
    var previewFavouriteToy by mutableStateOf("")
        private set
    var previewOwnerEmail by mutableStateOf(TextFieldValue())
        private set

    private val repositoryType = RepositoryType.SharedPreference

    private enum class RepositoryType {
        SharedPreference,
        HashMap,
        Flow,
        DataStore
    }

    init {
        viewModelScope.launch {
            initAndObserve()
        }
    }

    private suspend fun initAndObserve() {
        when (repositoryType) {
            RepositoryType.SharedPreference -> {
                name = TextFieldValue(sharedPreferenceRepository.getName() ?: "")
                favouriteToy = sharedPreferenceRepository.getFavouriteToy() ?: favouriteToys[0]
                ownerEmail = TextFieldValue(sharedPreferenceRepository.getOwnerEmail() ?: "")

                sharedPreferenceRepository.observeDogData().collectLatest { updatePreview(it) }
            }

            RepositoryType.HashMap -> {
                name = TextFieldValue(hashMapRepository.getName() ?: "")
                favouriteToy = hashMapRepository.getFavouriteToy() ?: favouriteToys[0]
                ownerEmail = TextFieldValue(hashMapRepository.getOwnerEmail() ?: "")

                hashMapRepository.observeDogData().collectLatest { updatePreview(it) }
            }

            RepositoryType.Flow -> {
                flowRepository.dogFlow.value.let {
                    name = TextFieldValue(it.name ?: "")
                    favouriteToy = it.favouriteToy ?: favouriteToys[0]
                    ownerEmail = TextFieldValue(it.ownerEmail ?: "")
                }
                flowRepository.dogFlow.collectLatest {
                    updatePreview(it)
                }
            }

            RepositoryType.DataStore -> {
                dataSourceRepository.observeDogData().first().let {
                    name = TextFieldValue(it.name ?: "")
                    favouriteToy = it.favouriteToy ?: favouriteToys[0]
                    ownerEmail = TextFieldValue(it.ownerEmail ?: "")
                }
                dataSourceRepository.observeDogData().collectLatest {
                    updatePreview(it)
                }
            }
        }
    }

    private fun updatePreview(dogData: DogData) {
        previewName = TextFieldValue(dogData.name ?: "")
        previewFavouriteToy = dogData.favouriteToy ?: favouriteToys[0]
        previewOwnerEmail = TextFieldValue(dogData.ownerEmail ?: "")
    }

    fun saveDogName(value: TextFieldValue? = null) = viewModelScope.launch {
        value ?: return@launch
        this@MainViewModel.name = value

        when (repositoryType) {
            RepositoryType.SharedPreference -> sharedPreferenceRepository.saveName(value.text)
            RepositoryType.HashMap -> hashMapRepository.saveName(value.text)
            RepositoryType.Flow -> flowRepository.saveName(value.text)
            RepositoryType.DataStore -> dataSourceRepository.saveName(value.text)
        }
    }

    fun saveDogFavouriteToy(value: String? = null) = viewModelScope.launch {
        value ?: return@launch
        this@MainViewModel.favouriteToy = value

        when (repositoryType) {
            RepositoryType.SharedPreference -> sharedPreferenceRepository.saveFavouriteToy(value)
            RepositoryType.HashMap -> hashMapRepository.saveFavouriteToy(value)
            RepositoryType.Flow -> flowRepository.saveFavouriteToy(value)
            RepositoryType.DataStore -> dataSourceRepository.saveFavouriteToy(value)
        }
    }

    fun saveDogOwnerEmail(value: TextFieldValue? = null) = viewModelScope.launch {
        value ?: return@launch
        this@MainViewModel.ownerEmail = value

        when (repositoryType) {
            RepositoryType.SharedPreference -> sharedPreferenceRepository.saveOwnerEmail(value.text)
            RepositoryType.HashMap -> hashMapRepository.saveOwnerEmail(value.text)
            RepositoryType.Flow -> flowRepository.saveOwnerEmail(value.text)
            RepositoryType.DataStore -> dataSourceRepository.saveOwnerEmail(value.text)
        }
    }

    fun clearDogDetails() {
        viewModelScope.launch {
            when (repositoryType) {
                RepositoryType.SharedPreference -> sharedPreferenceRepository.clear()
                RepositoryType.HashMap -> hashMapRepository.clear()
                RepositoryType.Flow -> flowRepository.clear()
                RepositoryType.DataStore -> dataSourceRepository.clear()
            }
        }

        name = TextFieldValue()
        favouriteToy = favouriteToys[0]
        ownerEmail = TextFieldValue()

        previewName = TextFieldValue()
        previewFavouriteToy = favouriteToys[0]
        previewOwnerEmail = TextFieldValue()
    }
}
