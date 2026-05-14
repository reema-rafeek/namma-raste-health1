package com.namma.raste.health.ui.screens.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.domain.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class RoadFilter { ALL, CRITICAL, AMBER, GOOD }

@HiltViewModel
class RoadDirectoryViewModel @Inject constructor(
    private val repository: RoadRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _currentFilter = MutableStateFlow(RoadFilter.ALL)
    val currentFilter = _currentFilter.asStateFlow()

    val roads: StateFlow<List<Road>> = combine(
        _searchQuery,
        _currentFilter,
        repository.getAllRoads()
    ) { query, filter, roads ->
        roads.filter { road ->
            val matchesQuery = road.name.contains(query, ignoreCase = true) || 
                               road.talukaName.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                RoadFilter.ALL -> true
                RoadFilter.CRITICAL -> road.healthScore < 50
                RoadFilter.AMBER -> road.healthScore in 50..79
                RoadFilter.GOOD -> road.healthScore >= 80
            }
            matchesQuery && matchesFilter
        }.sortedBy { it.healthScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: RoadFilter) {
        _currentFilter.value = filter
    }
}
