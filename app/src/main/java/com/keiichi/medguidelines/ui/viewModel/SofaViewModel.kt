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
    private val _gcsComponents = MutableStateFlow(GcsComponents())
    val gcsComponents = _gcsComponents.asStateFlow()
    fun updateGcsComponents(e: Int, v: Int, m: Int) {
        _gcsComponents.update { GcsComponents(e = e, v = v, m = m) }
    }

}
