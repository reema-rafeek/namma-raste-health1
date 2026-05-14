package com.namma.raste.health.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.raste.health.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    private val _isFirstLaunch = MutableStateFlow<Boolean?>(null)
    val isFirstLaunch = _isFirstLaunch.asStateFlow()

    init {
        checkFirstLaunch()
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            val firstLaunch = preferenceRepository.isFirstLaunch.firstOrNull() ?: true
            _isFirstLaunch.value = firstLaunch
        }
    }
}
