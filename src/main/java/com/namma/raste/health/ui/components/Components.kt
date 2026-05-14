package com.namma.raste.health.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.namma.raste.health.R
import com.namma.raste.health.ui.theme.*

@Composable
fun HealthScoreRing(score: Int, modifier: Modifier = Modifier) {
    val targetSweepAngle = (score / 100f) * 360f
    val animatedSweepAngle by animateFloatAsState(
        targetValue = targetSweepAngle,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "score_ring"
    )

    val color = when {
        score >= 80 -> NeonGreen
        score >= 50 -> CyberAmber
        else -> CriticalRed
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(64.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // High-Tech background track
            drawArc(
                color = TechBorder,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )
            // Vibrant Progress
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(color.copy(alpha = 0.5f), color)
                ),
                startAngle = -90f,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = color
            )
            Text(
                text = "HQI",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = TechGray
            )
        }
    }
}

@Composable
fun TechMetricCard(label: String, value: String, icon: @Composable () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "blink"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder.copy(alpha = alpha))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TechGray, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = PremiumWhite
            )
        }
    }
}

@Composable
fun RoadImage(model: Any?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.road_hero),
        error = androidx.compose.ui.res.painterResource(id = R.drawable.road_hero)
    )
}

@Composable
fun ShimmerRoadCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp).background(TechBorder.copy(alpha = alpha), MaterialTheme.shapes.medium))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(150.dp, 20.dp).background(TechBorder.copy(alpha = alpha), MaterialTheme.shapes.small))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.size(100.dp, 14.dp).background(TechBorder.copy(alpha = alpha), MaterialTheme.shapes.small))
            }
            Box(modifier = Modifier.size(48.dp).background(TechBorder.copy(alpha = alpha), androidx.compose.foundation.shape.CircleShape))
        }
    }
}

@Composable
fun DynamicIslandStatus(message: String, icon: ImageVector, color: Color = ElectricBlue) {
    Surface(
        modifier = Modifier
            .padding(top = 12.dp)
            .widthIn(min = 200.dp)
            .height(40.dp),
        color = TechBlack,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = PremiumWhite,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

