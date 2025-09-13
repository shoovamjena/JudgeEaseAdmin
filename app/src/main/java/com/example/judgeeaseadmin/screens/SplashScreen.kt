package com.example.judgeeaseadmin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.judgeeaseadmin.viewmodel.AppViewModel
import com.example.judgeeaseadmin.viewmodel.AuthState

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AppViewModel
) {
    val authState = authViewModel.authState.collectAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> Unit
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("JudgeEase", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator()
    }
}
