package com.example.global_moviles_2_23310191.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.global_moviles_2_23310191.data.model.Routine
import com.example.global_moviles_2_23310191.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoutineUiState(
    val loading: Boolean = false,
    val routines: List<Routine> = emptyList(),
    val error: String? = null,
    val saved: Boolean = false // To trigger navigation
)

class RoutineViewModel(
    private val repo: RoutineRepository = RoutineRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(RoutineUiState())
    val state: StateFlow<RoutineUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = RoutineUiState(loading = true)
            try {
                val routines = repo.getAll()
                _state.value = RoutineUiState(routines = routines)
            } catch (e: Exception) {
                _state.value = RoutineUiState(error = e.message)
            }
        }
    }

    fun create(routine: Routine, onCreated: (String) -> Unit = {}) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, saved = false)
            try {
                val id = repo.create(routine)
                _state.value = _state.value.copy(loading = false, saved = true)
                onCreated(id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun update(routine: Routine) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, saved = false)
            try {
                repo.update(routine)
                _state.value = _state.value.copy(loading = false, saved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                repo.delete(id)
                // Remove the deleted routine from the current state
                val updatedRoutines = _state.value.routines.filterNot { it.id == id }
                _state.value = _state.value.copy(loading = false, routines = updatedRoutines)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    // Call this from the UI after navigation has occurred
    fun resetSavedState() {
        _state.value = _state.value.copy(saved = false)
    }
}
