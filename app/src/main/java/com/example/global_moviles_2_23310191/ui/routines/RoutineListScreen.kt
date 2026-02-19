package com.example.global_moviles_2_23310191.ui.routines

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.ui.navigation.Routes

@Composable
fun RoutineListScreen(navController: NavController, vm: RoutineViewModel = viewModel()) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.load() }

    val filteredRoutines = remember(state.routines, query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            state.routines
        } else {
            state.routines.filter {
                it.name.lowercase().contains(q) || it.type.lowercase().contains(q)
            }
        }
    }

    RoutineListContent(
        routines = filteredRoutines,
        query = query,
        onQueryChange = { query = it },
        loading = state.loading,
        error = state.error,
        onAddClick = {
            navController.navigate(Routes.ROUTINE_FORM)
        },
        onRoutineClick = { routine ->
            navController.navigate("${Routes.ROUTINE_FORM}/${routine.id}")
        },
        onDeleteRoutine = { routineId ->
            vm.delete(routineId)
            Toast.makeText(ctx, "Rutina eliminada", Toast.LENGTH_SHORT).show()
        }
    )
}
