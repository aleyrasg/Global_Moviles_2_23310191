package com.example.global_moviles_2_23310191.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.ui.auth.AuthViewModel
import com.example.global_moviles_2_23310191.ui.navigation.Routes

@Composable
fun HomeScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home ✅", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("Aquí luego van: Rutinas, Lugares, Mapa, Recordatorios…")

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                vm.logout()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
        Button(
            onClick = { navController.navigate(Routes.ROUTINES) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ir a Rutinas") }
        Button(
            onClick = { navController.navigate(Routes.PLACES) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ir a Lugares") }
        Button(
            onClick = { navController.navigate(Routes.MAP) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Mapa") }
        Button(onClick = { navController.navigate(Routes.REMINDERS) }) {
            Text("Recordatorios")
        }


    }
}
