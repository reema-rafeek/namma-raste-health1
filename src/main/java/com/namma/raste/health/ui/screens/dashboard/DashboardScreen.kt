package com.namma.raste.health.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.ui.components.*
import com.namma.raste.health.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onRoadClick: (Int) -> Unit,
    onReportDamage: () -> Unit,
    onAdminClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var logoTapCount by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.clickable { 
                        logoTapCount++
                        if (logoTapCount >= 7) {
                            onAdminClick()
                            logoTapCount = 0
                        }
                    }) {
                        Text(
                            "NAMMA-RASTE", 
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp),
                            color = PremiumWhite
                        )
                    }
                },
                actions = {
                    Surface(
                        color = TechSurface,
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            "DHARWAD CORE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TechBlack)
            )
        },
        floatingActionButton = {
            val fabScale by animateFloatAsState(
                targetValue = if (uiState.criticalCount > 0) 1.15f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
                label = "fab_bounce"
            )

            ExtendedFloatingActionButton(
                onClick = onReportDamage,
                modifier = Modifier.scale(fabScale),
                containerColor = ElectricBlue,
                contentColor = TechBlack,
                shape = MaterialTheme.shapes.large,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("AUDIT SITE", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = TechBlack
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isEmergencySeeding) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val infiniteTransition = rememberInfiniteTransition(label = "loading")
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                            label = "alpha"
                        )
                        
                        CircularProgressIndicator(color = ElectricBlue)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "SYNCHRONIZING INFRASTRUCTURE DATA...", 
                            color = ElectricBlue.copy(alpha = alpha), 
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TechMetricCard("Nodes", uiState.totalRoads.toString(), { Icon(Icons.Default.Hub, null, Modifier.size(16.dp), ElectricBlue) }, Modifier.weight(1f))
                        TechMetricCard("Anomalies", uiState.openReports.toString(), { Icon(Icons.Default.CrisisAlert, null, Modifier.size(16.dp), CriticalRed) }, Modifier.weight(1f))
                    }

                    CriticalAlertStrip(uiState.roads.filter { it.healthScore < 50 })

                    Text(
                        text = "INFRASTRUCTURE FEED",
                        style = MaterialTheme.typography.labelSmall,
                        color = TechGray,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    if (uiState.roads.isEmpty()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            repeat(5) { ShimmerRoadCard() }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                count = uiState.roads.size,
                                key = { index -> uiState.roads[index].id } // Keying is essential for stability
                            ) { index ->
                                RoadHealthCard(
                                    road = uiState.roads[index], 
                                    onClick = { onRoadClick(uiState.roads[index].id) },
                                    onMapClick = onMapClick,
                                    index = index
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            ) {
                when {
                    uiState.isSyncing -> {
                        DynamicIslandStatus(
                            message = "Syncing Data...",
                            icon = Icons.Default.CloudSync,
                            color = ElectricBlue
                        )
                    }
                    uiState.pendingSyncCount > 0 -> {
                        Box(modifier = Modifier.clickable { viewModel.triggerSync() }) {
                            DynamicIslandStatus(
                                message = "${uiState.pendingSyncCount} Reports Pending Sync",
                                icon = Icons.Default.CloudUpload,
                                color = CyberAmber
                            )
                        }
                    }
                    uiState.criticalCount > 0 -> {
                        DynamicIslandStatus(
                            message = "${uiState.criticalCount} Critical Nodes Detected",
                            icon = Icons.Default.Warning,
                            color = CriticalRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CriticalAlertStrip(criticalRoads: List<Road>) {
    if (criticalRoads.isEmpty()) return

    val infiniteTransition = rememberInfiniteTransition(label = "alert_strip")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "blink"
    )

    Surface(
        color = CriticalRed.copy(alpha = 0.05f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, CriticalRed.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "heartbeat"
            )

            Icon(
                Icons.Default.NotificationsActive, 
                null, 
                tint = CriticalRed.copy(alpha = alpha), 
                modifier = Modifier.size(20.dp).scale(scale)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    count = criticalRoads.size,
                    key = { index -> criticalRoads[index].id }
                ) { index ->
                    Text(
                        text = "CRITICAL FAILURE: ${criticalRoads[index].name.uppercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = CriticalRed,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
