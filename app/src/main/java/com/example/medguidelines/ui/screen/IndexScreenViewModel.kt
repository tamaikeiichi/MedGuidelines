package com.example.medguidelines.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IndexScreenViewModel(
    handle: SavedStateHandle
) : ViewModel() {
    private val _savedItems = MutableStateFlow<List<ListItemData>>(emptyList())
    val savedItems: StateFlow<List<ListItemData>> = _savedItems

    // Function to update the list
    fun updateSavedItems(newItems: List<ListItemData>) {
        _savedItems.value = newItems
    }
}