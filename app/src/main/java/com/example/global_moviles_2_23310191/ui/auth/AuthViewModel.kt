package com.example.global_moviles_2_23310191.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// Represents the state of the auth UI (login/register forms)
data class AuthFormState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false // To trigger navigation
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // State for login/register forms
    private val _formState = MutableStateFlow(AuthFormState())
    val formState = _formState.asStateFlow()

    // State for the current user (logged in or not)
    private val _user = MutableStateFlow(auth.currentUser)
    val user = _user.asStateFlow()

    init {
        // Listen to Firebase auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _user.value = firebaseAuth.currentUser
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _formState.value = AuthFormState(loading = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                // On success, the AuthStateListener will update the _user flow.
                // We set success to true to signal the UI to navigate.
                _formState.value = AuthFormState(success = true)
            } catch (e: Exception) {
                _formState.value = AuthFormState(error = e.message ?: "Authentication failed")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _formState.value = AuthFormState(loading = true)
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                // On success, the AuthStateListener will update the _user flow.
                // We set success to true to signal the UI to navigate.
                _formState.value = AuthFormState(success = true)
            } catch (e: Exception) {
                _formState.value = AuthFormState(error = e.message ?: "Registration failed")
            }
        }
    }

    fun resetFormState() {
        _formState.value = AuthFormState()
    }

    fun logout() {
        auth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
