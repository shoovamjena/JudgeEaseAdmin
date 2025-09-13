package com.example.judgeeaseadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkExistingAdmin()
    }

    private fun checkExistingAdmin() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            viewModelScope.launch {
                fetchAdminData(currentUser.uid)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val uid = auth.currentUser?.uid ?: throw Exception("Admin not found")
                fetchAdminData(uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val uid = auth.currentUser?.uid ?: throw Exception("Admin not found")

                val adminMap = mapOf(
                    "name" to name,
                    "email" to email,
                    "role" to "admin"
                )

                firestore.collection("admins").document(uid).set(adminMap).await()
                _authState.value = AuthState.Authenticated(name, email)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    private suspend fun fetchAdminData(uid: String) {
        try {
            val document = firestore.collection("admins").document(uid).get().await()
            if (document.exists()) {
                val name = document.getString("name") ?: "Unknown"
                val email = document.getString("email") ?: "Unknown"
                _authState.value = AuthState.Authenticated(name, email)
            } else {
                _authState.value = AuthState.Error("Admin data not found")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Failed to fetch admin data")
        }
    }

    fun getAdminName(): String {
        return when (val currentState = _authState.value) {
            is AuthState.Authenticated -> currentState.name
            else -> "Unknown Admin"
        }
    }
}

sealed class AuthState {
    data object Initial : AuthState()
    data class Authenticated(val name: String, val email: String) : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
