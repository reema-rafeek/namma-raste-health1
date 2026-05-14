package com.namma.raste.health.ui.screens.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.domain.model.DamageReport
import com.namma.raste.health.domain.model.DamageType
import com.namma.raste.health.domain.model.ReportStatus
import com.namma.raste.health.domain.model.Severity
import com.namma.raste.health.domain.repository.RoadRepository
import com.namma.raste.health.domain.usecase.CalculateHealthScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DamageReportUiState(
    val currentStep: Int = 1,
    val roadId: Int = 0,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val damageType: DamageType? = null,
    val severity: Severity? = null,
    val photoPath: String? = null,
    val description: String = "",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false
)

@HiltViewModel
class DamageReportViewModel @Inject constructor(
    private val repository: RoadRepository,
    private val calculateHealthScoreUseCase: CalculateHealthScoreUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DamageReportUiState(roadId = savedStateHandle.get<Int>("roadId") ?: 0))
    val uiState = _uiState.asStateFlow()

    fun nextStep() {
        if (_uiState.value.currentStep < 5) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun setLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(lat = lat, lng = lng) }
    }

    fun setDamageType(type: DamageType) {
        _uiState.update { it.copy(damageType = type) }
    }

    fun setSeverity(severity: Severity) {
        _uiState.update { it.copy(severity = severity) }
    }

    fun setPhotoPath(path: String?) {
        _uiState.update { it.copy(photoPath = path) }
    }

    fun setDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun submitReport() {
        val state = _uiState.value
        if (state.damageType == null || state.severity == null || state.description.length < 20) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val report = DamageReport(
                id = 0,
                roadId = state.roadId,
                reportedBy = "USER_DEVICE", // Should be device UUID
                damageType = state.damageType,
                severity = state.severity,
                description = state.description,
                photoPath = state.photoPath,
                lat = state.lat,
                lng = state.lng,
                timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT),
                status = ReportStatus.OPEN,
                segmentKm = 0.5f // Simplified
            )
            repository.insertReport(report)
            calculateHealthScoreUseCase(state.roadId)
            _uiState.update { it.copy(isSubmitting = false, isSubmitted = true) }
        }
    }
}
