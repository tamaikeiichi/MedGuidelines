package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.ShobyomeiEntity
import com.keiichi.medguidelines.data.ShyobyomeiRepository
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShobyomeiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ShyobyomeiRepository
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ShyobyomeiRepository(db.shobyomeiDao())
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.populateDatabaseFromCsvIfEmpty(
                    context = application,
                    resourceId = R.raw.b_20260101 
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleFavorite(item: ShobyomeiEntity) {
        viewModelScope.launch {
            repository.updateFavorite(item.code, !item.isFavorite)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<ShobyomeiEntity>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // クエリが空の場合はお気に入りリストを表示
                repository.getFavorites()
            } else {
                // クエリがある場合は通常検索
                val normalizedInput = normalizeTextForSearch(query)
                val words = normalizedInput.trim().split(Regex("\\s+"))
                repository.searchMulti(words)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ShobyomeiViewModel(application) as T
            }
        }
    }
}
