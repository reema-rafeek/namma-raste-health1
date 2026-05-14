package com.namma.raste.health.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.namma.raste.health.ui.navigation.Screen
import com.namma.raste.health.ui.screens.admin.AdminSeedScreen
import com.namma.raste.health.ui.screens.contractor.ContractorProfileScreen
import com.namma.raste.health.ui.screens.detail.RoadDetailScreen
import com.namma.raste.health.ui.screens.main.MainScreen
import com.namma.raste.health.ui.screens.onboarding.OnboardingScreen
import com.namma.raste.health.ui.screens.report.DamageReportScreen
import com.namma.raste.health.ui.screens.splash.SplashScreen
import com.namma.raste.health.ui.theme.NammaRasteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaRasteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(tween(400)) },
        exitTransition = { fadeOut(tween(400)) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onRoadClick = { id -> 
                    navController.navigate(Screen.RoadDetail.createRoute(id)) 
                },
                onReportDamage = { id ->
                    navController.navigate(Screen.DamageReport.createRoute(id))
                },
                onAdminClick = {
                    navController.navigate(Screen.AdminSeed.route)
                },
                onMapClick = {
                    navController.navigate(Screen.SuccessMap.route)
                }
            )
        }
        composable(Screen.AdminSeed.route) {
            AdminSeedScreen()
        }
        composable(
            route = Screen.RoadDetail.route,
            arguments = listOf(navArgument("roadId") { type = NavType.IntType })
        ) {
            RoadDetailScreen(
                onBack = { navController.popBackStack() },
                onReportDamage = { id ->
                    navController.navigate(Screen.DamageReport.createRoute(id))
                },
                onContractorClick = { id ->
                    navController.navigate(Screen.ContractorProfile.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.DamageReport.route,
            arguments = listOf(navArgument("roadId") { type = NavType.IntType })
        ) {
            DamageReportScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.ContractorProfile.route,
            arguments = listOf(navArgument("contractorId") { type = NavType.IntType })
        ) {
            ContractorProfileScreen(
                onBack = { navController.popBackStack() },
                onRoadClick = { id ->
                    navController.navigate(Screen.RoadDetail.createRoute(id))
                }
            )
        }
    }
}
