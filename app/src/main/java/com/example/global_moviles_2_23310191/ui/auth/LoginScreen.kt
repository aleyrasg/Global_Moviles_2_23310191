package com.example.global_moviles_2_23310191.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.global_moviles_2_23310191.ui.navigation.Routes
import com.example.global_moviles_2_23310191.utils.Prefs

@Composable
fun LoginScreen(navController: NavController, vm: AuthViewModel = viewModel()) {

    val ctx = LocalContext.current
    val prefs = remember { Prefs(ctx) }

    var email by remember { mutableStateOf(prefs.getEmail() ?: "") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(prefs.getEmail() != null) }
    var loading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    val cs = MaterialTheme.colorScheme

    // ✅ Fondo pro: lila suave arriba -> surface abajo
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
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Bienvenida 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = cs.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // ✅ Card más pro (surface + borde suave)
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = cs.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Filled.Email, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cs.primary,
                            focusedLabelColor = cs.primary,
                            cursorColor = cs.primary,
                            unfocusedBorderColor = cs.outline,
                            unfocusedLabelColor = cs.onSurfaceVariant
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null) },
                        trailingIcon = {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = cs.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { showPassword = !showPassword }
                                    .padding(6.dp)
                            )
                        },
                        visualTransformation =
                            if (showPassword) VisualTransformation.None
                            else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cs.primary,
                            focusedLabelColor = cs.primary,
                            cursorColor = cs.primary,
                            unfocusedBorderColor = cs.outline,
                            unfocusedLabelColor = cs.onSurfaceVariant
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = cs.primary,
                                uncheckedColor = cs.outline,
                                checkmarkColor = cs.onPrimary
                            )
                        )
                        Text("Recordarme", color = cs.onSurface)
                    }

                    // ✅ Botón principal pro
                    Button(
                        onClick = {
                            val e = email.trim()
                            val p = password.trim()

                            if (e.isEmpty() || p.isEmpty()) {
                                Toast.makeText(ctx, "Completa email y password", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            loading = true

                            vm.login(
                                email = e,
                                password = p,
                                onSuccess = {
                                    loading = false
                                    if (rememberMe) prefs.saveEmail(e) else prefs.clearEmail()

                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                },
                                onError = { msg ->
                                    loading = false
                                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cs.primary,
                            contentColor = cs.onPrimary
                        )
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = cs.onPrimary
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Entrando...")
                        } else {
                            Text("Entrar")
                        }
                    }

                    // ✅ Secundario con outline / estilo limpio
                    OutlinedButton(
                        onClick = { navController.navigate(Routes.REGISTER) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = cs.primary
                        )
                    ) {
                        Text("Crear cuenta")
                    }
                }
            }
        }
    }
}
