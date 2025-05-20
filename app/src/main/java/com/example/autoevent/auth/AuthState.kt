package com.example.autoevent.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val error: String) : AuthState()
}
