package com.namma.raste.health.ui.screens.contractor

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.raste.health.ui.components.RoadHealthCard
import com.namma.raste.health.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorProfileScreen(
    viewModel: ContractorProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onRoadClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONTRACTOR DOSSIER", style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TechBlack, titleContentColor = PremiumWhite)
            )
        },
        containerColor = TechBlack
    ) { padding ->
        uiState.contractor?.let { contractor ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TechSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = contractor.companyName, style = MaterialTheme.typography.headlineSmall, color = PremiumWhite, fontWeight = FontWeight.ExtraBold)
                            Text(text = "LICENSE: ${contractor.licenseNumber}", style = MaterialTheme.typography.labelSmall, color = TechGray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "RATING: ", style = MaterialTheme.typography.labelSmall, color = TechGray)
                                Text(text = "${contractor.rating}/5.0", style = MaterialTheme.typography.bodyMedium, color = NeonGreen, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Point of Contact: ${contractor.contactPerson}", color = PremiumWhite, style = MaterialTheme.typography.bodySmall)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contractor.phone}"))
                                        context.startActivity(intent)
                                    },
                                    label = { Text("CALL", fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp)) },
                                    colors = AssistChipDefaults.assistChipColors(labelColor = ElectricBlue, containerColor = TechSurface),
                                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = TechBorder)
                                )
                                AssistChip(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contractor.email}"))
                                        context.startActivity(intent)
                                    },
                                    label = { Text("EMAIL", fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(18.dp)) },
                                    colors = AssistChipDefaults.assistChipColors(labelColor = ElectricBlue, containerColor = TechSurface),
                                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = TechBorder)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "MANAGED INFRASTRUCTURE", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = TechGray,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(uiState.roads) { road ->
                    RoadHealthCard(road, onClick = { onRoadClick(road.id) })
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
    }
}
