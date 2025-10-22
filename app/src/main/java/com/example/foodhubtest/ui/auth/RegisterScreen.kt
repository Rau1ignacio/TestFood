package com.example.foodhubtest.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodhubtest.ui.viewmodels.AuthFormState
import com.example.foodhubtest.ui.viewmodels.AuthScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: AuthScreenState,
    onFormChange: (AuthFormState) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val form = state.form
    val roles = listOf("CLIENT", "ADMIN")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(24.dp))

        // --- SELECTOR DE ROL AÑADIDO ---
        Text("Registrarse como:", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            roles.forEach { role ->
                FilterChip(
                    selected = form.role == role,
                    onClick = { onFormChange(form.copy(role = role)) },
                    label = { Text(if (role == "CLIENT") "Cliente" else "Admin") }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            label = { Text("Nombre") },
            isError = form.nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        form.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.email,
            onValueChange = { onFormChange(form.copy(email = it)) },
            label = { Text("Email") },
            isError = form.emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        form.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.pass,
            onValueChange = { onFormChange(form.copy(pass = it)) },
            label = { Text("Contraseña (mín. 6 caracteres)") },
            isError = form.passError != null,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        form.passError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
        } else {
            state.generalError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Button(
            onClick = onRegisterClick,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Registrarse")
        }
        TextButton(onClick = onNavigateToLogin, enabled = !state.isLoading) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}