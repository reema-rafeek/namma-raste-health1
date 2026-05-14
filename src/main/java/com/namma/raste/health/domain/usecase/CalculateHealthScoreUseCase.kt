package com.namma.raste.health.domain.usecase

import com.namma.raste.health.domain.model.ReportStatus
import com.namma.raste.health.domain.model.Road
import com.namma.raste.health.domain.model.Severity
import com.namma.raste.health.domain.repository.RoadRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CalculateHealthScoreUseCase @Inject constructor(
    private val repository: RoadRepository
) {
    suspend operator fun invoke(roadId: Int) {
        val road = repository.getRoadById(roadId) ?: return
        val reports = repository.getReportsForRoad(roadId).first()
        val recentLogs = repository.getLogsForRoad(roadId).first()
        
        var score = 100
        
        // Deduct based on open reports
        reports.filter { it.status == ReportStatus.OPEN }.forEach { report ->
            val penalty = when (report.severity) {
                Severity.CRITICAL -> 20
                Severity.HIGH -> 12
                Severity.MEDIUM -> 6
                Severity.LOW -> 2
            }
            score -= penalty
        }
        
        // Cap penalty at -80 (min score 20)
        if (score < 20) score = 20
        
        // Bonus for recent maintenance (last 6 months - simulated check)
        if (recentLogs.isNotEmpty()) {
            score += 5
        }
        
        score = score.coerceIn(20, 100)
        
        repository.updateRoad(road.copy(healthScore = score, lastUpdated = System.currentTimeMillis()))
    }
}
