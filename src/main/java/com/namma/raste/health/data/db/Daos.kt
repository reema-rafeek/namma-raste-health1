package com.namma.raste.health.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadDao {
    @Query("SELECT * FROM roads")
    fun getAllRoads(): Flow<List<RoadEntity>>

    @Query("SELECT * FROM roads WHERE id = :id")
    suspend fun getRoadById(id: Int): RoadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoads(roads: List<RoadEntity>)

    @Update
    suspend fun updateRoad(road: RoadEntity)

    @Query("DELETE FROM roads")
    suspend fun clearAll()
}

@Dao
interface ContractorDao {
    @Query("SELECT * FROM contractors")
    fun getAllContractors(): Flow<List<ContractorEntity>>

    @Query("SELECT * FROM contractors WHERE id = :id")
    suspend fun getContractorById(id: Int): ContractorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContractors(contractors: List<ContractorEntity>)
}

@Dao
interface DamageReportDao {
    @Query("SELECT * FROM damage_reports")
    fun getAllReports(): Flow<List<DamageReportEntity>>

    @Query("SELECT * FROM damage_reports WHERE roadId = :roadId")
    fun getReportsForRoad(roadId: Int): Flow<List<DamageReportEntity>>

    @Query("SELECT * FROM damage_reports WHERE reportedBy = :deviceId")
    fun getReportsByDevice(deviceId: String): Flow<List<DamageReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DamageReportEntity)
}

@Dao
interface MaintenanceLogDao {
    @Query("SELECT * FROM maintenance_logs WHERE roadId = :roadId")
    fun getLogsForRoad(roadId: Int): Flow<List<MaintenanceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MaintenanceLogEntity)
}
