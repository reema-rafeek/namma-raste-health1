package com.namma.raste.health.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.raste.health.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onGetStarted: () -> Unit
) {
    val isSeeding by viewModel.isSeeding.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    if (isSeeding) {
        Box(
            modifier = Modifier.fillMaxSize().background(TechBlack), 
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        color = ElectricBlue,
                        strokeWidth = 2.dp,
                        trackColor = TechBorder
                    )
                    Text(
                        "AI", 
                        color = ElectricBlue, 
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "CONSTRUCTING DIGITAL TWIN...", 
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricBlue,
                    letterSpacing = 2.sp
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(TechBlack, TechSurface)))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    OnboardingPage(page)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, 8.dp, 24.dp, 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page Indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) { index ->
                            val active = pagerState.currentPage == index
                            val width by animateDpAsState(if (active) 32.dp else 8.dp, label = "indicator")
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(width = width, height = 8.dp)
                                    .background(
                                        if (active) ElectricBlue else TechBorder,
                                        MaterialTheme.shapes.small
                                    )
                            )
                        }
                    }

                    Button(
                        onClick = { 
                            if (pagerState.currentPage < 2) {
                                scope.launch { 
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1) 
                                }
                            } else {
                                viewModel.completeOnboarding(onGetStarted) 
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(48.dp)
                    ) {
                        AnimatedContent(
                            targetState = pagerState.currentPage == 2,
                            label = "btn_text"
                        ) { isLast ->
                            Text(
                                if (isLast) "ACTIVATE SYSTEM" else "CONTINUE",
                                color = TechBlack,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    val title = when (page) {
        0 -> "ASSET DEGRADATION"
        1 -> "COMMUNITY AUDITING"
        else -> "DATA-DRIVEN POLICY"
    }
    val description = when (page) {
        0 -> "Rural segments are failing at a critical rate. Minor anomalies lead to total network collapse."
        1 -> "Deploy AI-powered telemetry tools to identify risks before they escalate into structural failure."
        else -> "Smart data ensures public funds are spent where they matter most, accelerating rural growth."
    }

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // High-Tech Animation Placeholder
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(Brush.radialGradient(listOf(ElectricBlue.copy(alpha = 0.15f), Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(tween(1500, easing = LinearOutSlowInEasing), RepeatMode.Reverse),
                label = "scale"
            )
            
            Surface(
                modifier = Modifier.size(180.dp).scale(scale),
                color = TechSurface,
                shape = MaterialTheme.shapes.extraLarge,
                border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when(page) {
                        0 -> Icons.AutoMirrored.Filled.TrendingDown
                        1 -> Icons.Default.Analytics
                        else -> Icons.Default.MilitaryTech
                    }
                    Icon(icon, null, modifier = Modifier.size(64.dp), tint = ElectricBlue)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(56.dp))
        
        AnimatedVisibility(
            visible = animateIn,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp),
                    color = PremiumWhite,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TechGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
        }
    }
}
