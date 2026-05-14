package com.namma.raste.health.ui.screens.contractor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.domain.model.Contractor
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.domain.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContractorProfileUiState(
    val contractor: Contractor? = null,
    val roads: List<Road> = emptyList()
)

@HiltViewModel
class ContractorProfileViewModel @Inject constructor(
    private val repository: RoadRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val contractorId: Int = checkNotNull(savedStateHandle["contractorId"])

    private val _uiState = MutableStateFlow(ContractorProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val contractor = repository.getContractorById(contractorId)
            repository.getAllRoads().collect { roads ->
                val contractorRoads = roads.filter { it.contractorId == contractorId }
                _uiState.value = ContractorProfileUiState(contractor, contractorRoads)
            }
        }
    }
}
