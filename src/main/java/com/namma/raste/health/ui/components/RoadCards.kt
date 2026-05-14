package com.namma.raste.health.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.namma.raste.health.R
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RoadHealthCard(
    road: Road, 
    onClick: () -> Unit,
    onMapClick: (() -> Unit)? = null,
    index: Int = 0 // Used for staggered delay
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(road.id) {
        delay(index * 50L)
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
    )
    
    val translateY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 40f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "translate"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(0, translateY.toInt()) }
            .alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = TechSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = road.photoResId ?: R.drawable.road_hero),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = road.name, 
                    style = MaterialTheme.typography.titleMedium,
                    color = PremiumWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${road.talukaName.uppercase()}-${100 + road.id}", 
                    style = MaterialTheme.typography.bodySmall,
                    color = TechGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    onClick = { onMapClick?.invoke() },
                    color = Color.Transparent,
                    enabled = onMapClick != null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn, 
                            contentDescription = null, 
                            tint = ElectricBlue, 
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIEW ON MAP", 
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
            HealthScoreRing(score = road.healthScore)
        }
    }
}
