package com.namma.raste.health.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.domain.usecase.SeedDataUseCase
import com.namma.raste.health.domain.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminSeedViewModel @Inject constructor(
    private val seedDataUseCase: SeedDataUseCase,
    private val repository: RoadRepository
) : ViewModel() {
    fun seedData() {
        viewModelScope.launch { seedDataUseCase() }
    }
    
    fun clearData() {
        viewModelScope.launch { repository.clearAllRoads() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSeedScreen(
    viewModel: AdminSeedViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Seed Panel") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Button(onClick = { viewModel.seedData() }, modifier = Modifier.fillMaxWidth()) {
                Text("Load Full Karnataka PMGSY Sample Data")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.clearData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Clear All Data")
            }
        }
    }
}
