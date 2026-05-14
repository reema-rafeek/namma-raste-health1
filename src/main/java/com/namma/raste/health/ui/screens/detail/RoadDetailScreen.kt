package com.namma.raste.health.ui.screens.detail

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.namma.raste.health.R
import com.namma.raste.health.domain.model.*
import com.namma.raste.health.ui.components.*
import com.namma.raste.health.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadDetailScreen(
    viewModel: RoadDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onReportDamage: (Int) -> Unit,
    onContractorClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ENGINEERING REPORT", style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TechBlack, titleContentColor = PremiumWhite)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onReportDamage(uiState.road?.id ?: 1) },
                containerColor = CriticalRed,
                contentColor = TechBlack,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("LOG ANOMALY", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = TechBlack
    ) { padding ->
        uiState.road?.let { road ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                Box(modifier = Modifier.height(280.dp)) {
                    Image(
                        painter = painterResource(id = road.photoResId ?: R.drawable.road_hero),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, TechBlack))))
                }

                RoadDetailContent(road, uiState, onContractorClick)
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
    }
}

@Composable
fun RoadDetailContent(road: Road, uiState: RoadDetailUiState, onContractorClick: (Int) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("TELEMETRY", "ANOMALIES", "LOGS")

    Column {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = TechSurface,
            contentColor = ElectricBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ElectricBlue
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> InfoTab(road, uiState, onContractorClick)
            1 -> ReportsTab(uiState.reports)
            2 -> HistoryTab(uiState.logs)
        }
    }
}

@Composable
fun HealthPredictionChart(score: Int) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "PREDICTIVE DEGRADATION TREND", 
            style = MaterialTheme.typography.labelSmall, 
            color = TechGray,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val width = size.width
            val height = size.height
            val points = listOf(score, score - 5, (score - 12).coerceAtLeast(20), (score - 18).coerceAtLeast(20), (score - 25).coerceAtLeast(20))
            val stepX = width / (points.size - 1)
            
            val path = androidx.compose.ui.graphics.Path()
            points.forEachIndexed { i, p ->
                val x = i * stepX
                val y = height - (p / 100f * height)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                
                drawCircle(
                    color = if (i == 0) NeonGreen else TechGray.copy(alpha = 0.5f),
                    radius = 4.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
            
            drawPath(
                path = path,
                color = ElectricBlue,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("NOW", style = MaterialTheme.typography.labelSmall, color = TechGray)
            Text("+6 MONTHS (ESTIMATED)", style = MaterialTheme.typography.labelSmall, color = TechGray)
        }
    }
}

@Composable
fun InfoTab(road: Road, uiState: RoadDetailUiState, onContractorClick: (Int) -> Unit) {
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        // Warranty Alert Badge
        val isWarrantyExpiring = road.warrantyEndDate.contains("2024") // Simulated logic
        if (isWarrantyExpiring) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                color = CriticalRed.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium,
                border = androidx.compose.foundation.BorderStroke(1.dp, CriticalRed.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PriorityHigh, null, tint = CriticalRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "WARRANTY MILESTONE: FINAL AUDIT REQUIRED",
                        style = MaterialTheme.typography.labelSmall,
                        color = CriticalRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            HealthScoreRing(score = road.healthScore)
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(text = road.name, style = MaterialTheme.typography.headlineSmall, color = PremiumWhite, fontWeight = FontWeight.ExtraBold)
                Text(text = "DISTRICT: ${road.districtName.uppercase()}", color = TechGray, style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Predictive Analytics Section
        HealthPredictionChart(road.healthScore)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.2f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = ElectricBlue)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "AI insight: Maintenance cycle predicted within 45 days based on recent crack density.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PremiumWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "CONTRACTOR TRANSPARENCY", style = MaterialTheme.typography.labelSmall, color = TechGray)
        Spacer(modifier = Modifier.height(8.dp))
        uiState.contractor?.let { contractor ->
            AssistChip(
                onClick = { onContractorClick(contractor.id) },
                label = { Text(contractor.companyName, color = PremiumWhite) },
                leadingIcon = { Icon(Icons.Default.Engineering, null, tint = ElectricBlue, modifier = Modifier.size(18.dp)) },
                colors = AssistChipDefaults.assistChipColors(containerColor = TechSurface),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = TechBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "SYSTEM PARAMETERS", style = MaterialTheme.typography.labelSmall, color = TechGray)
        Spacer(modifier = Modifier.height(12.dp))
        ParameterRow("Warranty Status", road.warrantyEndDate, NeonGreen)
        ParameterRow("Segment Length", "${road.lengthKm} KM", PremiumWhite)
        ParameterRow("Cycle Start", "${road.constructionYear}", TechGray)
    }
}

@Composable
fun ParameterRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TechGray, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ReportsTab(reports: List<DamageReport>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reports) { report ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TechSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CrisisAlert, null, tint = CriticalRed)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = report.damageType.name, style = MaterialTheme.typography.titleMedium, color = PremiumWhite)
                            Text(text = "Reported at ${report.timestamp.take(16).replace("T", " ")}", style = MaterialTheme.typography.labelSmall, color = TechGray)
                        }
                    }
                    
                    if (!report.photoPath.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        RoadImage(
                            model = if (report.photoPath.startsWith("file") || report.photoPath.startsWith("content")) {
                                report.photoPath.toUri()
                            } else {
                                R.drawable.damage_sample // Fallback for seeded data
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = report.description, style = MaterialTheme.typography.bodySmall, color = TechGray)
                }
            }
        }
    }
}

@Composable
fun HistoryTab(logs: List<MaintenanceLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(logs) { log ->
            TimelineItem(log)
        }
    }
}

@Composable
fun TimelineItem(log: MaintenanceLog) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(12.dp).background(ElectricBlue, androidx.compose.foundation.shape.CircleShape))
            Box(modifier = Modifier.width(2.dp).height(80.dp).background(TechBorder))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = log.workType, style = MaterialTheme.typography.titleSmall, color = PremiumWhite)
            Text(text = "${log.startDate} - ${log.endDate}", style = MaterialTheme.typography.labelSmall, color = TechGray)
            Text(text = "INVESTMENT: ₹${log.costLakh} L", style = MaterialTheme.typography.bodyMedium, color = NeonGreen, fontWeight = FontWeight.Bold)
        }
    }
}
