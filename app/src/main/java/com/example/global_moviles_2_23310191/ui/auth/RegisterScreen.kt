package com.example.global_moviles_2_23310191.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.ui.navigation.Routes
import com.example.global_moviles_2_23310191.utils.Prefs

@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = viewModel()) {
    val ctx = LocalContext.current
    val prefs = remember { Prefs(ctx) }
    val formState by vm.formState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(formState.error) {
        formState.error?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_LONG).show()
            vm.resetFormState() // Reset after showing error
        }
    }

    LaunchedEffect(formState.success) {
        if (formState.success) {
            if (rememberMe) prefs.saveEmail(email.trim()) else prefs.clearEmail()
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.REGISTER) { inclusive = true }
                launchSingleTop = true
            }
            vm.resetFormState() // Reset after navigation
        }
    }

    val cs = MaterialTheme.colorScheme
    val gradient = Brush.verticalGradient(
        colors = listOf(
            cs.primary.copy(alpha = 0.18f),
            cs.background,
            cs.surface
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        RegisterContent(
            email = email,
            password = password,
            rememberMe = rememberMe,
            showPassword = showPassword,
            loading = formState.loading,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onRememberMeChange = { rememberMe = it },
            onShowPasswordChange = { showPassword = !showPassword },
            onRegisterClick = {
                val e = email.trim()
                val p = password.trim()
                if (e.isEmpty() || p.length < 6) {
                    Toast.makeText(ctx, "Email válido y password mínimo 6", Toast.LENGTH_SHORT).show()
                } else {
                    vm.register(e, p)
                }
            },
            onLoginClick = { navController.popBackStack() }
        )
    }
}
