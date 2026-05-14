package com.namma.raste.health.ui.screens.report

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.namma.raste.health.R
import com.namma.raste.health.domain.model.DamageType
import com.namma.raste.health.domain.model.Severity
import com.namma.raste.health.ui.components.CameraPreview
import com.namma.raste.health.ui.components.RoadImage
import com.namma.raste.health.ui.components.captureImage
import com.namma.raste.health.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageReportScreen(
    viewModel: DamageReportViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    if (uiState.isSubmitted) {
        LaunchedEffect(Unit) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ANOMALY AUDIT", style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            StepProgressBar(currentStep = uiState.currentStep)
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                        }.using(SizeTransform(clip = false))
                    },
                    label = "step_nav"
                ) { step ->
                    when (step) {
                        1 -> LocationStep(onLocationSelected = viewModel::setLocation)
                        2 -> DamageTypeStep(selectedType = uiState.damageType, onTypeSelected = viewModel::setDamageType)
                        3 -> SeverityStep(selectedSeverity = uiState.severity, onSeveritySelected = viewModel::setSeverity)
                        4 -> PhotoStep(photoPath = uiState.photoPath, onPhotoCaptured = viewModel::setPhotoPath)
                        5 -> DescriptionStep(description = uiState.description, onDescriptionChanged = viewModel::setDescription)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.currentStep > 1) {
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.previousStep()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = TechGray)
                    ) {
                        Text("PREVIOUS")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (uiState.currentStep < 5) viewModel.nextStep() else viewModel.submitReport()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.currentStep == 5) CriticalRed else ElectricBlue),
                    shape = MaterialTheme.shapes.medium,
                    enabled = when (uiState.currentStep) {
                        1 -> true
                        2 -> uiState.damageType != null
                        3 -> uiState.severity != null
                        4 -> true
                        5 -> uiState.description.length >= 20
                        else -> false
                    }
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TechBlack, strokeWidth = 2.dp)
                    } else {
                        Text(if (uiState.currentStep < 5) "CONTINUE" else "TRANSMIT DATA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StepProgressBar(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..5) {
            val color = if (i <= currentStep) ElectricBlue else TechBorder
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .background(color, MaterialTheme.shapes.small)
            )
        }
    }
}

@Composable
fun LocationStep(onLocationSelected: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val dharwad = LatLng(15.4589, 75.0078)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(dharwad, 15f)
    }

    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val performSearch = {
        if (searchQuery.isNotBlank()) {
            scope.launch {
                try {
                    val geocoder = android.location.Geocoder(context)
                    // Try coordinate parsing first
                    val parts = searchQuery.split(",")
                    if (parts.size == 2) {
                        val lat = parts[0].trim().toDoubleOrNull()
                        val lng = parts[1].trim().toDoubleOrNull()
                        if (lat != null && lng != null) {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                        }
                    } else {
                        // Geocode place name
                        val addresses = withContext(Dispatchers.IO) {
                            geocoder.getFromLocationName(searchQuery, 1)
                        }
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(addr.latitude, addr.longitude), 15f))
                        } else {
                            Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column {
        Text("SET COORDINATES", style = MaterialTheme.typography.labelSmall, color = TechGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search location or coords...", color = TechGray) },
            leadingIcon = { Icon(Icons.Default.Map, null, tint = ElectricBlue) },
            trailingIcon = {
                Row {
                    IconButton(onClick = performSearch) {
                        Icon(Icons.Default.Search, null, tint = ElectricBlue)
                    }
                    IconButton(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                loc?.let {
                                    scope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 16f))
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Permission required for Live Location", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.MyLocation, null, tint = NeonGreen)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = TechBorder,
                focusedTextColor = PremiumWhite,
                unfocusedTextColor = PremiumWhite
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { performSearch() })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
        ) {
            Box {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    Marker(
                        state = MarkerState(position = cameraPositionState.position.target),
                        title = "Audit Point"
                    )
                }
                
                Icon(
                    Icons.Default.Add, 
                    null, 
                    modifier = Modifier.align(Alignment.Center).size(24.dp), 
                    tint = ElectricBlue
                )
            }
        }
        
        Text(
            text = "GPS: ${cameraPositionState.position.target.latitude.toString().take(8)}, ${cameraPositionState.position.target.longitude.toString().take(8)}",
            style = MaterialTheme.typography.labelSmall,
            color = TechGray,
            modifier = Modifier.padding(top = 8.dp)
        )

        LaunchedEffect(cameraPositionState.position.target) {
            onLocationSelected(cameraPositionState.position.target.latitude, cameraPositionState.position.target.longitude)
        }
    }
}

@Composable
fun DamageTypeStep(selectedType: DamageType?, onTypeSelected: (DamageType) -> Unit) {
    val types = listOf(
        DamageTypeInfo(DamageType.CRACK, "CRACK", Icons.Default.Warning),
        DamageTypeInfo(DamageType.POTHOLE, "POTHOLE", Icons.Default.Report),
        DamageTypeInfo(DamageType.WATERLOGGING, "WATERLOGGING", Icons.Default.WaterDrop),
        DamageTypeInfo(DamageType.DRAIN_BLOCKED, "DRAINAGE", Icons.Default.Build),
        DamageTypeInfo(DamageType.EROSION, "EROSION", Icons.Default.Terrain),
        DamageTypeInfo(DamageType.OTHER, "OTHER", Icons.Default.Info)
    )

    Column {
        Text("CLASSIFY ANOMALY", style = MaterialTheme.typography.labelSmall, color = TechGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(types) { type ->
                val haptic = LocalHapticFeedback.current
                FilterChip(
                    selected = selectedType == type.type,
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onTypeSelected(type.type) 
                    },
                    label = { Text(type.label, style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(type.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = TechBlack,
                        containerColor = TechSurface,
                        labelColor = TechGray,
                        iconColor = TechGray
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (selectedType == type.type) ElectricBlue else TechBorder,
                        enabled = true,
                        selected = selectedType == type.type
                    )
                )
            }
        }
    }
}

data class DamageTypeInfo(val type: DamageType, val label: String, val icon: ImageVector)

@Composable
fun SeverityStep(selectedSeverity: Severity?, onSeveritySelected: (Severity) -> Unit) {
    val severities = Severity.entries.toList()
    Column {
        Text("ANOMALY SEVERITY", style = MaterialTheme.typography.labelSmall, color = TechGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        severities.forEach { severity ->
            val color = when(severity) {
                Severity.LOW -> NeonGreen
                Severity.MEDIUM -> CyberAmber
                Severity.HIGH -> Color.Magenta
                Severity.CRITICAL -> CriticalRed
            }
            val haptic = LocalHapticFeedback.current
            Card(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSeveritySelected(severity) 
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = if (selectedSeverity == severity) color.copy(alpha = 0.1f) else TechSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedSeverity == severity) color else TechBorder)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSeverity == severity,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSeveritySelected(severity) 
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = color, unselectedColor = TechGray)
                    )
                    Text(severity.name, color = if (selectedSeverity == severity) color else PremiumWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PhotoStep(photoPath: String?, onPhotoCaptured: (String?) -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Column {
        Text("SITE CAPTURE", style = MaterialTheme.typography.labelSmall, color = TechGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (photoPath == null) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        imageCapture = imageCapture
                    )
                    
                    // AI HUD Overlay
                    Box(modifier = Modifier.fillMaxSize().border(1.dp, ElectricBlue.copy(alpha = 0.2f)))
                    
                    IconButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            captureImage(
                                imageCapture = imageCapture,
                                context = context,
                                executor = ContextCompat.getMainExecutor(context),
                                onImageCaptured = { uri -> onPhotoCaptured(uri.toString()) },
                                onError = { Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show() }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .size(72.dp)
                            .background(PremiumWhite.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                            .border(2.dp, ElectricBlue, androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = "Capture", tint = ElectricBlue, modifier = Modifier.size(32.dp))
                    }
                } else {
                    RoadImage(
                        model = photoPath.toUri(),
                        modifier = Modifier.fillMaxSize()
                    )
                    TextButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onPhotoCaptured(null) 
                        },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = TechBlack.copy(alpha = 0.6f))
                    ) {
                        Text("RETAKE PHOTO", color = PremiumWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun DescriptionStep(description: String, onDescriptionChanged: (String) -> Unit) {
    Column {
        Text("AUDIT NOTES", style = MaterialTheme.typography.labelSmall, color = TechGray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            placeholder = { Text("Minimum 20 characters required for AI analysis validation...", color = TechGray, style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PremiumWhite,
                unfocusedTextColor = PremiumWhite,
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = TechBorder,
                cursorColor = ElectricBlue
            )
        )
    }
}
