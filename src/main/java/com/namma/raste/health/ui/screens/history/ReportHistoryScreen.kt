package com.namma.raste.health.ui.screens.history

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.raste.health.domain.model.DamageReport
import com.namma.raste.health.ui.components.RoadImage
import com.namma.raste.health.ui.theme.TechBlack
import com.namma.raste.health.ui.theme.TechBorder
import com.namma.raste.health.ui.theme.TechSurface
import com.namma.raste.health.ui.theme.TechGray
import com.namma.raste.health.ui.theme.PremiumWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistoryScreen(
    viewModel: ReportHistoryViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("REPORT ARCHIVE", style = MaterialTheme.typography.labelLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TechBlack, titleContentColor = PremiumWhite)
            )
        },
        containerColor = TechBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reports) { report ->
                ReportCard(report)
            }
        }
    }
}

@Composable
fun ReportCard(report: DamageReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = report.damageType.name, style = MaterialTheme.typography.titleMedium, color = PremiumWhite)
                Text(text = report.status.name, style = MaterialTheme.typography.labelSmall, color = TechGray)
            }
            Text(
                text = "Audit at ${report.timestamp.take(16).replace("T", " ")}", 
                style = MaterialTheme.typography.labelSmall, 
                color = TechGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Severity: ${report.severity.name}", style = MaterialTheme.typography.bodySmall, color = TechGray)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (report.photoPath != null && report.photoPath.isNotEmpty()) {
                RoadImage(
                    model = if (report.photoPath.startsWith("file") || report.photoPath.startsWith("content")) {
                        report.photoPath.toUri()
                    } else {
                        com.namma.raste.health.R.drawable.damage_sample
                    },
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            
            Text(text = report.description, style = MaterialTheme.typography.bodySmall, color = TechGray)
        }
    }
}
