package com.namma.raste.health.domain.model

data class Road(
    val id: Int,
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
    val healthScore: Int,
    val lastUpdated: Long,
    val photoResId: Int? = null
)

data class Contractor(
    val id: Int,
    val companyName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val licenseNumber: String,
    val activeContracts: Int,
    val completedProjects: Int,
    val rating: Float
)

data class DamageReport(
    val id: Int,
    val roadId: Int,
    val reportedBy: String,
    val damageType: DamageType,
    val severity: Severity,
    val description: String,
    val photoPath: String?,
    val lat: Double,
    val lng: Double,
    val timestamp: String,
    val status: ReportStatus,
    val segmentKm: Float,
    val isSynced: Boolean = false
)

enum class DamageType { CRACK, POTHOLE, WATERLOGGING, DRAIN_BLOCKED, EROSION, OTHER }
enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }
enum class ReportStatus { OPEN, IN_REVIEW, RESOLVED }

data class MaintenanceLog(
    val id: Int,
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
