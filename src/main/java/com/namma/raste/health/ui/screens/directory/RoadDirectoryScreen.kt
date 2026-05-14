package com.namma.raste.health.ui.screens.directory

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.raste.health.ui.components.RoadHealthCard
import com.namma.raste.health.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadDirectoryScreen(
    viewModel: RoadDirectoryViewModel = hiltViewModel(),
    onRoadClick: (Int) -> Unit,
    onMapClick: () -> Unit = {}
) {
    val query by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val roads by viewModel.roads.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(TechBlack)) {
                SearchBar(
                    query = query,
                    onQueryChange = viewModel::updateSearchQuery,
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search road or taluka", color = TechGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = SearchBarDefaults.colors(containerColor = TechSurface, inputFieldColors = TextFieldDefaults.colors(focusedTextColor = PremiumWhite, unfocusedTextColor = PremiumWhite))
                ) {}

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(RoadFilter.entries) { filter ->
                        FilterChip(
                            selected = currentFilter == filter,
                            onClick = { viewModel.setFilter(filter) },
                            label = { Text(filter.name, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = TechBlack,
                                containerColor = TechSurface,
                                labelColor = TechGray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (currentFilter == filter) ElectricBlue else TechBorder,
                                enabled = true,
                                selected = currentFilter == filter
                            )
                        )
                    }
                }
            }
        },
        containerColor = TechBlack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(roads.size) { index ->
                val road = roads[index]
                var startAnim by remember { mutableStateOf(false) }

                LaunchedEffect(road.id) {
                    delay(index * 40L)
                    startAnim = true
                }

                AnimatedVisibility(
                    visible = startAnim,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 5 }
                ) {
                    RoadHealthCard(
                        road = road, 
                        onClick = { onRoadClick(road.id) },
                        onMapClick = onMapClick
                    )
                }
            }
        }
    }
}
