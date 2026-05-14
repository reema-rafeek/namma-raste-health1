package com.namma.raste.health.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.namma.raste.health.ui.navigation.Screen
import com.namma.raste.health.ui.screens.dashboard.DashboardScreen
import com.namma.raste.health.ui.screens.dashboard.DashboardViewModel
import com.namma.raste.health.ui.screens.directory.RoadDirectoryScreen
import com.namma.raste.health.ui.screens.history.ReportHistoryScreen
import com.namma.raste.health.ui.screens.success.SuccessMapScreen

@Composable
fun MainScreen(
    onRoadClick: (Int) -> Unit,
    onReportDamage: (Int) -> Unit,
    onAdminClick: () -> Unit,
    onMapClick: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        NavigationItem("Home", Screen.Dashboard.route, Icons.Default.Home),
        NavigationItem("Search", Screen.RoadDirectory.route, Icons.AutoMirrored.Filled.List),
        NavigationItem("Success", Screen.SuccessMap.route, Icons.Default.Star),
        NavigationItem("History", Screen.ReportHistory.route, Icons.Default.History)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = com.namma.raste.health.ui.theme.TechBlack) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = com.namma.raste.health.ui.theme.ElectricBlue,
                            selectedTextColor = com.namma.raste.health.ui.theme.ElectricBlue,
                            unselectedIconColor = com.namma.raste.health.ui.theme.TechGray,
                            unselectedTextColor = com.namma.raste.health.ui.theme.TechGray,
                            indicatorColor = com.namma.raste.health.ui.theme.TechSurface
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController, 
            startDestination = Screen.Dashboard.route, 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                DashboardScreen(
                    viewModel = viewModel,
                    onRoadClick = onRoadClick,
                    onReportDamage = { 
                        val firstRoadId = uiState.roads.firstOrNull()?.id ?: 1
                        onReportDamage(firstRoadId) 
                    },
                    onAdminClick = onAdminClick,
                    onMapClick = onMapClick
                )
            }
            composable(Screen.RoadDirectory.route) {
                RoadDirectoryScreen(
                    onRoadClick = onRoadClick,
                    onMapClick = onMapClick
                )
            }
            composable(Screen.SuccessMap.route) {
                SuccessMapScreen(onRoadClick = onRoadClick)
            }
            composable(Screen.ReportHistory.route) {
                ReportHistoryScreen()
            }
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)
