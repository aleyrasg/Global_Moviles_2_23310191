package com.example.global_moviles_2_23310191

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.global_moviles_2_23310191.notifications.NotificationChannels
import com.example.global_moviles_2_23310191.ui.auth.AuthViewModel
import com.example.global_moviles_2_23310191.ui.navigation.AppNavGraph
import com.example.global_moviles_2_23310191.ui.navigation.Routes
import com.example.global_moviles_2_23310191.ui.theme.Global_Moviles_2_23310191Theme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // ✅ Crea el canal de notificaciones (Android 8+)
        NotificationChannels.create(this)


        setContent {
            Global_Moviles_2_23310191Theme {

                val authViewModel: AuthViewModel = viewModel()

                // ✅ Se calcula una vez y se "recuerda"
                val startDestination = remember {
                    if (authViewModel.isLoggedIn()) Routes.HOME else Routes.LOGIN
                }

                // ✅ AQUI ya se pasa el vm
                AppNavGraph(
                    startDestination = startDestination,
                    vm = authViewModel
                )
            }
        }
    }
}
