package com.keiichi.medguidelines.ui.viewModel

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// This class will hold the Glasgow Coma Scale score.
class SofaViewModel : ViewModel() {

    // Use a StateFlow to hold the GCS score. Composables can collect this flow.
    // It's private to prevent external classes from directly changing it.
    private val _gcsScore = MutableStateFlow(15.0) // Default to a perfect score

    // This is the publicly exposed, read-only version of the score.
    val gcsScore = _gcsScore.asStateFlow()

    // Public function that allows GlasgowComaScaleScreen to update the score.
    fun updateGcsScore(newScore: Double) {
        _gcsScore.update { newScore }
    }
}
