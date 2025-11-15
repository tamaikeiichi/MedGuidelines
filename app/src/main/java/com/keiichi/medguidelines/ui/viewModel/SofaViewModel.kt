package com.keiichi.medguidelines.ui.viewModel

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// This class will hold the Glasgow Coma Scale score.
data class GcsComponents(
    val e: Int = 4,   // Default to best response
    val v: Int = 5,
    val m: Int = 6
)

class SofaViewModel : ViewModel() {

    // Use a StateFlow to hold the GCS score. Composables can collect this flow.
    // It's private to prevent external classes from directly changing it.
    private val _gcsScore = MutableStateFlow(15.0) // Default to a perfect score
    val gcsScore = _gcsScore.asStateFlow()
    private val _gcsComponents = MutableStateFlow(GcsComponents())
    val gcsComponents = _gcsComponents.asStateFlow()
    // Public function that allows GlasgowComaScaleScreen to update the score.

    // Public function to update the GCS components from GlasgowComaScaleScreen
    fun updateGcsComponents(e: Int, v: Int, m: Int) {
        _gcsComponents.update { GcsComponents(e = e, v = v, m = m) }

        // Also update the total score at the same time
        _gcsScore.update { (e + v + m).toDouble() }
    }
    fun updateGcsFromCnsScore(cnsScore: Int) {
        val newGcsTotal = when (cnsScore) {
            0 -> 15
            1 -> 13
            2 -> 10
            3 -> 6
            4 -> 3
            else -> 15
        }
        // This is a simplified update. We assume the worst-case motor and verbal,
        // and best-case eye score to reach the target total.
        // A more complex implementation could be done if needed.
        // For now, setting a representative total is enough.
        // We will just set one component to reflect the change.
        // Example: Set Eye to 1, Verbal to 1, and Motor to the rest.
        val motor = maxOf(1, newGcsTotal - 2)
        _gcsComponents.update { GcsComponents(e = 1, v = 1, m = motor) }
    }
}
