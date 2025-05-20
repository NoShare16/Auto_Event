package com.example.autoevent.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val auth: FirebaseAuth        = FirebaseAuth.getInstance(),
    private val db:   FirebaseFirestore   = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState>   = _authState.asStateFlow()

    /* ---------- Public API ---------- */

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    seedUserDoc()                 // ← Seed nach Erfolg
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Login fehlgeschlagen"
                    )
                }
            }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    seedUserDoc()                 // ← Seed nach Erfolg
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Registrierung fehlgeschlagen"
                    )
                }
            }
    }

    fun resetState() { _authState.value = AuthState.Idle }

    /* ---------- Private helper ---------- */

    private fun seedUserDoc() = viewModelScope.launch {
        val user = auth.currentUser ?: return@launch
        val doc  = db.collection("users").document(user.uid)

        withContext(Dispatchers.IO) {
            val exists = try { Tasks.await(doc.get()).exists() } catch (_: Exception) { true }
            if (!exists) {
                val seed = mapOf(
                    "displayName" to (user.email ?: "User"),
                    "bio"         to "",
                    "photoUrl"    to "",
                    "followers"   to 0,
                    "following"   to 0,
                    "updatedAt"   to Timestamp.now()
                )
                try { Tasks.await(doc.set(seed)) } catch (_: Exception) { /* ignore */ }
            }
        }
    }
}
