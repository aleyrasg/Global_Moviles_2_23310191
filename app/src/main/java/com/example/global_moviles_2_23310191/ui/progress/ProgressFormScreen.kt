package com.example.global_moviles_2_23310191.ui.progress

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.data.model.ProgressEntry
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressFormScreen(navController: NavController, vm: ProgressViewModel = viewModel()) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    var dateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }
    var waistCm by remember { mutableStateOf("") }
    var bicepCm by remember { mutableStateOf("") }
    var gluteCm by remember { mutableStateOf("") }
    var selectedPhotoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute)
    var showTimePicker by remember { mutableStateOf(false) }

    val pickImages = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedPhotoUris = uris
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Entrada de Progreso") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

        // Date
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date(dateMillis))

        OutlinedTextField(
            value = dateStr,
            onValueChange = {},
            label = { Text("Fecha") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) { Text("Cambiar") }
            }
        )

        Spacer(Modifier.height(10.dp))

        // Time
        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        OutlinedTextField(
            value = timeStr,
            onValueChange = {},
            label = { Text("Hora") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { showTimePicker = true }) { Text("Cambiar") }
            }
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = waistCm,
            onValueChange = { waistCm = it },
            label = { Text("Cintura (cm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = bicepCm,
            onValueChange = { bicepCm = it },
            label = { Text("Bícep (cm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = gluteCm,
            onValueChange = { gluteCm = it },
            label = { Text("Glúteo (cm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = { pickImages.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text(
                if (selectedPhotoUris.isEmpty()) "Elegir fotos de galería"
                else "Cambiar fotos seleccionadas"
            )
        }

        Spacer(Modifier.height(8.dp))

        if (selectedPhotoUris.isNotEmpty()) {
            Text("✅ ${selectedPhotoUris.size} foto(s) seleccionada(s)")
        } else {
            Text("Sin fotos")
        }

        Spacer(Modifier.height(16.dp))

        if (state.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick = {
                val w = waistCm.toDoubleOrNull()
                val b = bicepCm.toDoubleOrNull()
                val g = gluteCm.toDoubleOrNull()

                if (w == null || b == null || g == null) {
                    Toast.makeText(ctx, "Ingresa medidas válidas", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                vm.create(
                    entry = ProgressEntry(
                        userId = userId,
                        dateMillis = dateMillis,
                        hour = hour,
                        minute = minute,
                        waistCm = w,
                        bicepCm = b,
                        gluteCm = g
                    ),
                    photoUris = selectedPhotoUris
                ) {
                    Toast.makeText(ctx, "Entrada creada ✅", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
        }
    }
}

