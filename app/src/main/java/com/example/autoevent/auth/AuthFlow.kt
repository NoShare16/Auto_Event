package com.example.autoevent.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AuthFlow(onAuthSuccess: () -> Unit) {
    var showLogin by remember { mutableStateOf(true) }

    if (showLogin) {
        LoginScreen(
            onLoginSuccess = onAuthSuccess,
            onNavigateToRegister = { showLogin = false }
        )
    } else {
        RegisterScreen(
            onRegisterSuccess = onAuthSuccess,
            onNavigateToLogin = { showLogin = true }
        )
    }
}

/* -------- Login -------- */

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    /* Navigation nach Erfolg */
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { authViewModel.login(email, password) }) {
            Text("Login")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Noch keinen Account? Registrieren")
        }

        when (authState) {
            is AuthState.Loading -> {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Spacer(Modifier.height(8.dp))
                Text(
                    (authState as AuthState.Error).error,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}

/* -------- Register -------- */

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrieren", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { authViewModel.register(email, password) }) {
            Text("Registrieren")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Bereits Account? Login")
        }

        when (authState) {
            is AuthState.Loading -> {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Spacer(Modifier.height(8.dp))
                Text(
                    (authState as AuthState.Error).error,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}
