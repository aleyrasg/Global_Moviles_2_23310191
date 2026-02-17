package com.example.global_moviles_2_23310191.ui.places

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.global_moviles_2_23310191.data.model.Place
import com.example.global_moviles_2_23310191.data.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PlaceUiState(
    val loading: Boolean = false,
    val places: List<Place> = emptyList(),
    val error: String? = null
)

class PlaceViewModel(
    private val repo: PlaceRepository = PlaceRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceUiState())
    val state: StateFlow<PlaceUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.getAll() }
                .onSuccess { _state.value = PlaceUiState(loading = false, places = it) }
                .onFailure { _state.value = PlaceUiState(loading = false, places = emptyList(), error = it.message) }
        }
    }

    fun create(place: Place, photoUri: Uri?, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.create(place, photoUri) }
                .onSuccess { load(); onDone() }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
        }
    }

    fun update(place: Place, newPhotoUri: Uri?, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.update(place, newPhotoUri) }
                .onSuccess { load(); onDone() }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.delete(id) }
                .onSuccess { load() }
                .onFailure { _state.value = _state.value.copy(loading = false, error = it.message) }
        }
    }
}
