package com.namma.raste.health.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.domain.model.Contractor
import com.namma.raste.health.domain.model.DamageReport
import com.namma.raste.health.domain.model.MaintenanceLog
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.domain.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoadDetailUiState(
    val road: Road? = null,
    val contractor: Contractor? = null,
    val reports: List<DamageReport> = emptyList(),
    val logs: List<MaintenanceLog> = emptyList()
)

@HiltViewModel
class RoadDetailViewModel @Inject constructor(
    private val repository: RoadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val roadId: Int = checkNotNull(savedStateHandle["roadId"])

    private val _uiState = MutableStateFlow(RoadDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val road = repository.getRoadById(roadId)
            if (road != null) {
                val contractor = repository.getContractorById(road.contractorId)
                
                combine(
                    repository.getReportsForRoad(roadId),
                    repository.getLogsForRoad(roadId)
                ) { reports, logs ->
                    RoadDetailUiState(
                        road = road,
                        contractor = contractor,
                        reports = reports,
                        logs = logs
                    )
                }.collect {
                    _uiState.value = it
                }
            }
        }
    }
}
