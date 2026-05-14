package com.namma.raste.health.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.raste.health.R
import com.namma.raste.health.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()
    
    // States for entrance animations
    var startTextAnim by remember { mutableStateOf(false) }
    var startBgAnim by remember { mutableStateOf(false) }

    val bgScale by animateFloatAsState(
        targetValue = if (startBgAnim) 1.1f else 1.0f,
        animationSpec = tween(5000, easing = LinearEasing),
        label = "bg_zoom"
    )

    LaunchedEffect(Unit) {
        startBgAnim = true
        delay(500)
        startTextAnim = true
    }

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch != null) {
            delay(4000) // Increased for full animation cycle
            if (isFirstLaunch == true) {
                onNavigateToOnboarding()
            } else {
                onNavigateToDashboard()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(TechBlack)
    ) {
        // Cinematic Background with Ken Burns effect
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().scale(bgScale),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, TechBlack.copy(alpha = 0.9f)),
                        startY = 300f
                    )
                )
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoadNetworkAnimation()
            
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = startTextAnim,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NAMMA-RASTE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp
                        ),
                        color = PremiumWhite
                    )
                    Text(
                        text = "INFRASTRUCTURE INTELLIGENCE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ElectricBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            AnimatedVisibility(
                visible = startTextAnim,
                enter = fadeIn(tween(1500, delayMillis = 1000))
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.width(160.dp),
                    color = ElectricBlue,
                    trackColor = TechBorder
                )
            }
        }

        // Bottom Brand Text
        Text(
            text = "DESIGNED BY AI CORE",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(if (startTextAnim) 0.4f else 0f),
            style = MaterialTheme.typography.labelSmall,
            color = TechGray,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun RoadNetworkAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "road_network")
    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "line_draw"
    )

    Canvas(modifier = Modifier.size(140.dp)) {
        // Subtle animated grid
        for (i in 0..5) {
            val gridAlpha = 0.1f
            drawLine(TechBorder.copy(alpha = gridAlpha), Offset(0f, i * 28.dp.toPx()), Offset(size.width, i * 28.dp.toPx()), 1f)
            drawLine(TechBorder.copy(alpha = gridAlpha), Offset(i * 28.dp.toPx(), 0f), Offset(i * 28.dp.toPx(), size.height), 1f)
        }

        // Dynamic Digital Twin lines
        val paths = listOf(
            Offset(20.dp.toPx(), 40.dp.toPx()) to Offset(120.dp.toPx(), 40.dp.toPx()),
            Offset(40.dp.toPx(), 80.dp.toPx()) to Offset(100.dp.toPx(), 80.dp.toPx()),
            Offset(70.dp.toPx(), 20.dp.toPx()) to Offset(70.dp.toPx(), 120.dp.toPx())
        )

        paths.forEachIndexed { index, pair ->
            val p = (lineProgress - (index * 0.1f)).coerceIn(0f, 1f)
            drawLine(
                color = if (index % 2 == 0) ElectricBlue else NeonGreen,
                start = pair.first,
                end = pair.first + (pair.second - pair.first) * p,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
                alpha = 0.8f
            )
        }
    }
}
