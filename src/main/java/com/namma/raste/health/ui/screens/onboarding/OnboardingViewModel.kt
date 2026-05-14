package com.namma.raste.health.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.data.repository.PreferenceRepository
import com.namma.raste.health.domain.usecase.SeedDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val seedDataUseCase: SeedDataUseCase,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    private val _isSeeding = MutableStateFlow(false)
    val isSeeding = _isSeeding.asStateFlow()

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _isSeeding.value = true
                seedDataUseCase()
                preferenceRepository.setFirstLaunchCompleted()
            } catch (e: Exception) {
                e.printStackTrace()
                // Still allow completion to avoid getting stuck
                preferenceRepository.setFirstLaunchCompleted()
            } finally {
                _isSeeding.value = false
                onComplete()
            }
        }
    }
}
