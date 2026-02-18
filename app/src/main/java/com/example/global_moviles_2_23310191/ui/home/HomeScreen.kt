package com.example.global_moviles_2_23310191.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.Routine
import com.example.global_moviles_2_23310191.ui.auth.AuthViewModel
import com.example.global_moviles_2_23310191.ui.navigation.Routes
import com.example.global_moviles_2_23310191.ui.routines.RoutineViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    vm: AuthViewModel,
    routineVm: RoutineViewModel = viewModel()
) {
    val state by routineVm.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cargar rutinas al entrar
    LaunchedEffect(Unit) { routineVm.load() }

    // 🔹 Contenido del Drawer (barra lateral)
    val drawerContent: @Composable () -> Unit = {
        ModalDrawerSheet {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Menú",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Divider()

            // ✅ Orden solicitado: Lugares, Mapa, Recordatorios, Cerrar sesión
            NavigationDrawerItem(
                label = { Text("Lugares") },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.PLACES) { launchSingleTop = true }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Mapa") },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.MAP) { launchSingleTop = true }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Recordatorios") },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.REMINDERS) { launchSingleTop = true }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Cerrar sesión") },
                selected = false,
                onClick = {
                    scope.launch { drawerState.close() }
                    vm.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }

    // 🧠 Drawer wrapper
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = drawerContent
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rutinas") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate(Routes.ROUTINE_FORM) // crear rutina
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva rutina")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // Estado loading/error como ya lo manejas en listas
                state.error?.let {
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                if (state.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                }

                if (!state.loading && state.routines.isEmpty()) {
                    Text(
                        "Aún no tienes rutinas. Presiona + para crear una.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    return@Column
                }

                // ✅ Lista visual de rutinas existentes
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.routines) { routine ->
                        RoutineHomeCard(
                            routine = routine,
                            onClick = {
                                // Editar rutina
                                navController.navigate("${Routes.ROUTINE_FORM}/${routine.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineHomeCard(
    routine: Routine,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(routine.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Tipo: ${routine.type} • ${routine.durationMin} min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (routine.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    routine.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
