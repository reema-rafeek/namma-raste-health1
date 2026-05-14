package com.namma.raste.health.domain.usecase

import com.namma.raste.health.domain.model.*
import com.namma.raste.health.domain.repository.RoadRepository
import javax.inject.Inject

class SeedDataUseCase @Inject constructor(
    private val repository: RoadRepository,
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase
) {
    suspend operator fun invoke() {
        repository.clearAllRoads()
        
        val contractors = listOf(
            Contractor(1, "Basaveshwara Constructions", "Rajesh Patil", "+91 98765 43210", "rajesh@basava.com", "LIC-HUBLI-001", 12, 45, 4.8f),
            Contractor(2, "Nandi Infra Projects", "Priya Hiremath", "+91 98450 12345", "priya@nandi.com", "LIC-DHAR-002", 8, 30, 4.5f),
            Contractor(3, "Karnataka Road Builders", "Suresh Reddy", "+91 99001 12233", "suresh@krb.com", "LIC-BENG-003", 25, 120, 4.9f)
        )
        repository.insertContractors(contractors)

        val roads = listOf(
            Road(1, "Kalghatgi–Navalgund Link", "Kalghatgi", "Dharwad", 15.1837, 74.9669, 15.22, 75.13, 14.2, 2019, "2024-12-31", 1, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.road_hero),
            Road(2, "Hubli Rural Bypass", "Hubli", "Dharwad", 15.3647, 75.1240, 15.34, 75.15, 8.6, 2021, "2026-06-30", 2, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.splash_bg),
            Road(3, "Alnavar Feeder Road", "Alnavar", "Dharwad", 15.4328, 74.8214, 15.45, 75.17, 11.4, 2020, "2025-03-15", 3, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.damage_sample),
            Road(4, "Shiggaon Village Road", "Shiggaon", "Haveri", 14.9961, 75.2285, 14.98, 75.19, 6.8, 2022, "2027-09-20", 1, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.map_static_base),
            Road(5, "Annigeri GP Road", "Annigeri", "Dharwad", 15.4287, 75.4336, 15.56, 75.21, 4.3, 2018, "2023-11-10", 2, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.road_hero),
            Road(6, "Kundgol Rural Sector", "Kundgol", "Dharwad", 15.2570, 75.2479, 15.26, 75.30, 9.5, 2021, "2026-01-15", 3, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.splash_bg),
            Road(7, "Mugd Bypass Link", "Dharwad", "Dharwad", 15.4589, 75.0078, 15.47, 75.05, 5.2, 2022, "2027-05-10", 1, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.damage_sample),
            Road(8, "Navalgund Agri Corridor", "Navalgund", "Dharwad", 15.5630, 75.3719, 15.60, 75.40, 12.8, 2020, "2025-08-22", 2, 100, System.currentTimeMillis(), com.namma.raste.health.R.drawable.map_static_base)
        )
        repository.insertRoads(roads)
        
        // Seed maintenance history for transparency
        val logs = listOf(
            MaintenanceLog(0, 1, "Surface Overlay", "Basaveshwara", "2023-01-10", "2023-02-15", 45.5, null, null, "Periodic renewal completed"),
            MaintenanceLog(0, 3, "Drainage Repair", "Karnataka Builders", "2023-06-20", "2023-07-05", 12.2, null, null, "Clogged culverts restored"),
            MaintenanceLog(0, 5, "Patch Work", "Nandi Infra", "2022-11-05", "2022-11-12", 8.4, null, null, "Minor pothole repairs")
        )
        for (log in logs) repository.insertLog(log)

        // Seed damage reports (vary the network health)
        val reports = listOf(
            DamageReport(0, 1, "SYSTEM", DamageType.POTHOLE, Severity.CRITICAL, "Large pothole near market junction", null, 15.185, 74.968, "2024-04-20T10:00:00Z", ReportStatus.OPEN, 1.2f),
            DamageReport(0, 1, "SYSTEM", DamageType.CRACK, Severity.HIGH, "Surface erosion extending 20m", null, 15.186, 74.970, "2024-04-21T11:00:00Z", ReportStatus.OPEN, 1.3f),
            DamageReport(0, 3, "SYSTEM", DamageType.WATERLOGGING, Severity.MEDIUM, "Drainage failure causing accumulation", null, 15.435, 74.825, "2024-04-22T09:15:00Z", ReportStatus.OPEN, 0.5f),
            DamageReport(0, 5, "SYSTEM", DamageType.EROSION, Severity.CRITICAL, "Edge drop-off hazard detected", null, 15.430, 75.435, "2024-04-23T08:30:00Z", ReportStatus.OPEN, 0.8f)
        )
        for (report in reports) {
            repository.insertReport(report)
            calculateHealthScoreUseCase(report.roadId)
        }
    }
}
