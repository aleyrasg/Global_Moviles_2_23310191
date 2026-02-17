package com.example.global_moviles_2_23310191.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.global_moviles_2_23310191.data.model.Routine
import com.example.global_moviles_2_23310191.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RoutineUiState(
    val loading: Boolean = false,
    val routines: List<Routine> = emptyList(),
    val error: String? = null
)

class RoutineViewModel(
    private val repo: RoutineRepository = RoutineRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RoutineUiState())
    val state: StateFlow<RoutineUiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.getAll() }
                .onSuccess { list ->
                    _state.value = RoutineUiState(loading = false, routines = list)
                }
                .onFailure { e ->
                    _state.value = RoutineUiState(loading = false, routines = emptyList(), error = e.message)
                }
        }
    }

    fun create(r: Routine, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.create(r) }
                .onSuccess {
                    load()
                    onDone()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(loading = false, error = e.message)
                }
        }
    }

    fun update(r: Routine, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.update(r) }
                .onSuccess {
                    load()
                    onDone()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(loading = false, error = e.message)
                }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            runCatching { repo.delete(id) }
                .onSuccess { load() }
                .onFailure { e ->
                    _state.value = _state.value.copy(loading = false, error = e.message)
                }
        }
    }
}
