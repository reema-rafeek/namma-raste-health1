package com.namma.raste.health.domain.usecase

import com.namma.raste.health.domain.repository.RoadRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncPendingReportsUseCase @Inject constructor(
    private val repository: RoadRepository
) {
    suspend operator fun invoke(): Int {
        val pending = repository.getAllReports().first().filter { !it.isSynced }
        if (pending.isEmpty()) return 0
        
        // Simulated network transmission
        pending.forEach { report ->
            // In a real app, this would be an API call
            repository.insertReport(report.copy(isSynced = true))
        }
        
        return pending.size
    }
}
