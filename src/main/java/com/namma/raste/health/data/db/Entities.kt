package com.namma.raste.health.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "roads")
data class RoadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val talukaName: String,
    val districtName: String,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val lengthKm: Double,
    val constructionYear: Int,
    val warrantyEndDate: String,
    val contractorId: Int,
    val healthScore: Int, // 0-100
    val lastUpdated: Long = System.currentTimeMillis(),
    val photoResId: Int? = null
)

@Entity(tableName = "contractors")
data class ContractorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val activeContracts: Int,
    val completedProjects: Int,
    val rating: Float
)

@Entity(tableName = "damage_reports")
data class DamageReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roadId: Int,
    val reportedBy: String = UUID.randomUUID().toString(),
    val damageType: String, // CRACK/POTHOLE/WATERLOGGING/DRAIN_BLOCKED/EROSION
    val severity: String, // LOW/MEDIUM/HIGH/CRITICAL
    val description: String,
    val photoPath: String?,
    val lat: Double,
    val lng: Double,
    val timestamp: String, // ISO-8601
    val status: String, // OPEN/IN_REVIEW/RESOLVED
    val segmentKm: Float,
    val isSynced: Boolean = false // For offline-first queuing
)

@Entity(tableName = "maintenance_logs")
data class MaintenanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roadId: Int,
    val workType: String,
    val contractor: String,
    val startDate: String,
    val endDate: String,
    val costLakh: Double,
    val beforePhotoPath: String?,
    val afterPhotoPath: String?,
    val notes: String
)
