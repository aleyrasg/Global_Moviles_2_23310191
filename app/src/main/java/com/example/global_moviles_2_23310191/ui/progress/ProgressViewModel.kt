package com.example.global_moviles_2_23310191.ui.progress

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.global_moviles_2_23310191.data.model.ProgressEntry
import com.example.global_moviles_2_23310191.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProgressUiState(
    val loading: Boolean = false,
    val entries: List<ProgressEntry> = emptyList(),
    val error: String? = null
)

class ProgressViewModel : ViewModel() {

    private val repo = ProgressRepository()

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.getAll() }
                .onSuccess { _state.value = ProgressUiState(loading = false, entries = it) }
                .onFailure { _state.value = ProgressUiState(loading = false, entries = emptyList(), error = it.message) }
        }
    }

    fun create(entry: ProgressEntry, photoUris: List<Uri>, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.create(entry, photoUris) }
                .onSuccess {
                    load()
                    onDone()
                }
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
