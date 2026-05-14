package com.namma.raste.health.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.domain.repository.RoadRepository
import com.namma.raste.health.domain.usecase.SeedDataUseCase
import com.namma.raste.health.domain.usecase.SyncPendingReportsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val roads: List<Road> = emptyList(),
    val totalRoads: Int = 0,
    val openReports: Int = 0,
    val criticalCount: Int = 0,
    val avgHealth: Int = 0,
    val isEmergencySeeding: Boolean = false,
    val pendingSyncCount: Int = 0,
    val isSyncing: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: RoadRepository,
    private val seedDataUseCase: SeedDataUseCase,
    private val syncPendingReportsUseCase: SyncPendingReportsUseCase
) : ViewModel() {

    private val _isEmergencySeeding = MutableStateFlow(false)
    private val _isSyncing = MutableStateFlow(false)

    init {
        // Safe one-time seeding check
        viewModelScope.launch {
            val roads = repository.getAllRoads().first()
            if (roads.isEmpty()) {
                _isEmergencySeeding.value = true
                try {
                    seedDataUseCase()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isEmergencySeeding.value = false
                }
            }
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAllRoads(),
        repository.getAllReports(),
        _isEmergencySeeding,
        _isSyncing
    ) { roads, reports, isSeeding, isSyncing ->
        DashboardUiState(
            roads = roads,
            totalRoads = roads.size,
            openReports = reports.size,
            criticalCount = roads.count { it.healthScore < 50 },
            avgHealth = if (roads.isNotEmpty()) roads.map { it.healthScore }.average().toInt() else 0,
            isEmergencySeeding = isSeeding,
            pendingSyncCount = reports.count { !it.isSynced },
            isSyncing = isSyncing
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun triggerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            syncPendingReportsUseCase()
            _isSyncing.value = false
        }
    }
}
