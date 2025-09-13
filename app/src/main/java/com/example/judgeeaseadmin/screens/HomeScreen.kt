package com.example.judgeeaseadmin.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.judgeeaseadmin.viewmodel.AppViewModel
import com.example.judgeeaseadmin.viewmodel.AuthState

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavController,
    authViewModel: AppViewModel
){
    val authState = authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login"){ popUpTo("home") { inclusive = true }}
            else -> Unit
        }
    }

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HomePage" , fontSize = 32.sp)

        Text(
            text = "Hi admin ${authViewModel.getAdminName()}",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {authViewModel.signOut()}
        ) {
            Text("SIGN OUT")
        }
    }
}