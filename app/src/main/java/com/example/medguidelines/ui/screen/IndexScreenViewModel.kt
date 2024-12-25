package com.example.medguidelines.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medguidelines.data.PreferenceDataStoreConstants.NAME_KEY
import com.example.medguidelines.data.preferenceDataStoreHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.medguidelines.ui.screen.IndexScreen
import kotlinx.coroutines.launch

class IndexScreenViewModel() : ViewModel() {
    fun showCompletedTasks(show: String){
        viewModelScope.launch {
            preferenceDataStoreHelper.putPreference(NAME_KEY, show)
        }
    }
}


