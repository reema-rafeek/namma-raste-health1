package com.namma.raste.health.ui.screens.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.namma.raste.health.R
import com.namma.raste.health.ui.theme.*
import com.namma.raste.health.ui.screens.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessMapScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onRoadClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLeaderboard by remember { mutableStateOf(false) }

    val dharwad = LatLng(15.4589, 75.0078)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(dharwad, 10f)
    }
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 140.dp,
        sheetContainerColor = TechSurface,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (showLeaderboard) "CONTRACTOR LEADERBOARD" else "ROAD SUCCESS INDEX", 
                        style = MaterialTheme.typography.titleLarge,
                        color = PremiumWhite,
                        fontWeight = FontWeight.ExtraBold
                    )
                    TextButton(onClick = { showLeaderboard = !showLeaderboard }) {
                        Text(if (showLeaderboard) "SHOW ROADS" else "SHOW CONTRACTORS", color = ElectricBlue)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                if (showLeaderboard) {
                    // Simulated Contractor Leaderboard
                    listOf(
                        "Basaveshwara Constructions" to 4.9f,
                        "Karnataka Road Builders" to 4.8f,
                        "Nandi Infra Projects" to 4.5f
                    ).forEachIndexed { index, (name, rating) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("#${index + 1}", color = TechGray, modifier = Modifier.width(32.dp))
                                Text(name, color = PremiumWhite)
                            }
                            Text("⭐ $rating", color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    uiState.roads.sortedByDescending { it.healthScore }.take(5).forEachIndexed { index, road ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${index + 1}. ${road.name}", color = PremiumWhite)
                            Text(text = "${road.healthScore}%", color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Success Map Background
            Image(
                painter = painterResource(id = R.drawable.map_static_base),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                uiState.roads.forEach { road ->
                    val healthColor = when {
                        road.healthScore >= 80 -> NeonGreen
                        road.healthScore >= 50 -> CyberAmber
                        else -> CriticalRed
                    }
                    Polyline(
                        points = listOf(
                            LatLng(road.startLat, road.startLng),
                            LatLng(road.endLat, road.endLng)
                        ),
                        color = healthColor,
                        width = 12f,
                        clickable = true,
                        onClick = { onRoadClick(road.id) }
                    )
                }
            }
        }
    }
}
