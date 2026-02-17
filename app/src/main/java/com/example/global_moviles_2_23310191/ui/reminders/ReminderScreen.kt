package com.example.global_moviles_2_23310191.ui.reminders

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.global_moviles_2_23310191.notifications.ReminderScheduler
import java.util.Calendar

@Composable
fun ReminderScreen() {
    val ctx = LocalContext.current

    // Permiso Android 13+
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recordatorios", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = {
                val cal = Calendar.getInstance().apply { add(Calendar.SECOND, 10) }
                ReminderScheduler.schedule(
                    context = ctx,
                    triggerAtMillis = cal.timeInMillis,
                    title = "Recordatorio",
                    message = "Prueba de notificación en 10s"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Probar notificación en 10s")
        }
    }
}
