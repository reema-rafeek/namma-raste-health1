package com.namma.raste.health.data.repository

import com.namma.raste.health.data.db.*
import com.namma.raste.health.domain.model.*
import com.namma.raste.health.domain.repository.RoadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.sqrt

class RoadRepositoryImpl @Inject constructor(
    private val roadDao: RoadDao,
    private val contractorDao: ContractorDao,
    private val reportDao: DamageReportDao,
    private val logDao: MaintenanceLogDao
) : RoadRepository {

    override fun getAllRoads(): Flow<List<Road>> = roadDao.getAllRoads().map { it.map { e -> e.toDomain() } }
    override suspend fun getRoadById(id: Int): Road? = roadDao.getRoadById(id)?.toDomain()
    override suspend fun updateRoad(road: Road) = roadDao.updateRoad(road.toEntity())
    override suspend fun insertRoads(roads: List<Road>) = roadDao.insertRoads(roads.map { it.toEntity() })
    override suspend fun clearAllRoads() = roadDao.clearAll()

    override fun getAllContractors(): Flow<List<Contractor>> = contractorDao.getAllContractors().map { it.map { e -> e.toDomain() } }
    override suspend fun getContractorById(id: Int): Contractor? = contractorDao.getContractorById(id)?.toDomain()
    override suspend fun insertContractors(contractors: List<Contractor>) = contractorDao.insertContractors(contractors.map { it.toEntity() })

    override fun getAllReports(): Flow<List<DamageReport>> = reportDao.getAllReports().map { it.map { e -> e.toDomain() } }
    override fun getReportsForRoad(roadId: Int): Flow<List<DamageReport>> = reportDao.getReportsForRoad(roadId).map { it.map { e -> e.toDomain() } }
    override fun getReportsByDevice(deviceId: String): Flow<List<DamageReport>> = reportDao.getReportsByDevice(deviceId).map { it.map { e -> e.toDomain() } }
    override suspend fun insertReport(report: DamageReport) = reportDao.insertReport(report.toEntity())

    override fun getLogsForRoad(roadId: Int): Flow<List<MaintenanceLog>> = logDao.getLogsForRoad(roadId).map { it.map { e -> e.toDomain() } }
    override suspend fun insertLog(log: MaintenanceLog) = logDao.insertLog(log.toEntity())

    override suspend fun getNearestRoad(lat: Double, lng: Double): Road? {
        val allRoads = roadDao.getAllRoads().first()
        return allRoads.minByOrNull { road ->
            val startDist = calculateDistance(lat, lng, road.startLat, road.startLng)
            val endDist = calculateDistance(lat, lng, road.endLat, road.endLng)
            minOf(startDist, endDist)
        }?.toDomain()
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = lat1 - lat2
        val dLon = lon1 - lon2
        return sqrt(dLat * dLat + dLon * dLon)
    }

    // Mappers
    private fun RoadEntity.toDomain() = Road(id, name, talukaName, districtName, startLat, startLng, endLat, endLng, lengthKm, constructionYear, warrantyEndDate, contractorId, healthScore, lastUpdated, photoResId)
    private fun Road.toEntity() = RoadEntity(id, name, talukaName, districtName, startLat, startLng, endLat, endLng, lengthKm, constructionYear, warrantyEndDate, contractorId, healthScore, lastUpdated, photoResId)

    private fun ContractorEntity.toDomain() = Contractor(id, companyName, contactPerson, phone, email, licenseNumber, activeContracts, completedProjects, rating)
    private fun Contractor.toEntity() = ContractorEntity(id, companyName, contactPerson, phone, email, licenseNumber, activeContracts, completedProjects, rating)

    private fun DamageReportEntity.toDomain() = DamageReport(id, roadId, reportedBy, DamageType.valueOf(damageType), Severity.valueOf(severity), description, photoPath, lat, lng, timestamp, ReportStatus.valueOf(status), segmentKm, isSynced)
    private fun DamageReport.toEntity() = DamageReportEntity(id, roadId, reportedBy, damageType.name, severity.name, description, photoPath, lat, lng, timestamp, status.name, segmentKm, isSynced)

    private fun MaintenanceLogEntity.toDomain() = MaintenanceLog(id, roadId, workType, contractor, startDate, endDate, costLakh, beforePhotoPath, afterPhotoPath, notes)
    private fun MaintenanceLog.toEntity() = MaintenanceLogEntity(id, roadId, workType, contractor, startDate, endDate, costLakh, beforePhotoPath, afterPhotoPath, notes)
}
