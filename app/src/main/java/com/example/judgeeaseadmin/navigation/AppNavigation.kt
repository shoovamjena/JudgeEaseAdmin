package com.example.judgeeaseadmin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.judgeeaseadmin.screens.CompetitionDetailScreen
import com.example.judgeeaseadmin.screens.HomeScreen
import com.example.judgeeaseadmin.screens.LoginScreen
import com.example.judgeeaseadmin.screens.SignUpScreen
import com.example.judgeeaseadmin.screens.SplashScreen
import com.example.judgeeaseadmin.viewmodel.AppViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AppViewModel){
    val navController = rememberNavController()

    NavHost(navController,startDestination = "splash", builder = {
        composable("splash") {
            SplashScreen(navController, authViewModel)
        }
        composable("login") {
            LoginScreen(modifier,navController,authViewModel)
        }
        composable("signup") {
            SignUpScreen(modifier,navController,authViewModel)
        }
        composable("home") {
            HomeScreen(modifier,navController,authViewModel)
        }
        composable("competition/{compId}") { backStackEntry ->
            val compId = backStackEntry.arguments?.getString("compId") ?: ""
            CompetitionDetailScreen(compId, authViewModel,navController)
        }
    })
}