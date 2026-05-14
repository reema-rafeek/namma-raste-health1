package com.namma.raste.health.domain.repository

import com.namma.raste.health.domain.model.Contractor
import com.namma.raste.health.domain.model.DamageReport
import com.namma.raste.health.domain.model.MaintenanceLog
import com.namma.raste.health.domain.model.Road
import kotlinx.coroutines.flow.Flow

interface RoadRepository {
    fun getAllRoads(): Flow<List<Road>>
    suspend fun getRoadById(id: Int): Road?
    suspend fun updateRoad(road: Road)
    suspend fun insertRoads(roads: List<Road>)
    suspend fun clearAllRoads()

    fun getAllContractors(): Flow<List<Contractor>>
    suspend fun getContractorById(id: Int): Contractor?
    suspend fun insertContractors(contractors: List<Contractor>)

    fun getAllReports(): Flow<List<DamageReport>>
    fun getReportsForRoad(roadId: Int): Flow<List<DamageReport>>
    fun getReportsByDevice(deviceId: String): Flow<List<DamageReport>>
    suspend fun insertReport(report: DamageReport)

    fun getLogsForRoad(roadId: Int): Flow<List<MaintenanceLog>>
    suspend fun insertLog(log: MaintenanceLog)

    suspend fun getNearestRoad(lat: Double, lng: Double): Road?
}
