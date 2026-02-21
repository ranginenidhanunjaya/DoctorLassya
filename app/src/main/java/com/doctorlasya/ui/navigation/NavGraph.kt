package com.doctorlasya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.doctorlasya.ui.screens.ChatScreen
import com.doctorlasya.ui.screens.HomeScreen
import com.doctorlasya.ui.screens.OnboardingScreen
import com.doctorlasya.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home       : Screen("home")
    object Chat       : Screen("chat")
    object Settings   : Screen("settings")
}

@Composable
fun LaasyaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController  = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onStartChat = { navController.navigate(Screen.Chat.route) },
                onSettings  = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
