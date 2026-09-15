package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.IcdO3Repository
import com.keiichi.medguidelines.data.IcdO3SearchResult
import com.keiichi.medguidelines.data.IcdO3TopographyRepository
import com.keiichi.medguidelines.data.IcdO3TopographySearchResult
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class IcdO3ViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: IcdO3Repository
    private val topographyRepository: IcdO3TopographyRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedGradeDigit = MutableStateFlow("")
    val selectedGradeDigit: StateFlow<String> = _selectedGradeDigit.asStateFlow()

    private val _topographySearchQuery = MutableStateFlow("")
    val topographySearchQuery: StateFlow<String> = _topographySearchQuery.asStateFlow()

    private val _isTopographyLoading = MutableStateFlow(false)
    val isTopographyLoading: StateFlow<Boolean> = _isTopographyLoading.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = IcdO3Repository(db.icdO3Dao())
        topographyRepository = IcdO3TopographyRepository(db.icdO3TopographyDao())

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.populateDatabaseFromCsvIfEmpty(
                    context = application,
                    resourceId = R.raw.icd_o3_morphology
                )
            } finally {
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            _isTopographyLoading.value = true
            try {
                topographyRepository.populateDatabaseFromCsvIfEmpty(
                    context = application,
                    resourceId = R.raw.icd_o3_topography
                )
            } finally {
                _isTopographyLoading.value = false
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onGradeDigitChanged(digit: String) {
        _selectedGradeDigit.value = digit
    }

    fun toggleFavorite(item: IcdO3SearchResult) {
        viewModelScope.launch {
            repository.updateFavorite(item.id, !item.isFavorite)
        }
    }

    fun onTopographyQueryChanged(newQuery: String) {
        _topographySearchQuery.value = newQuery
    }

    fun toggleTopographyFavorite(item: IcdO3TopographySearchResult) {
        viewModelScope.launch {
            topographyRepository.updateFavorite(item.id, !item.isFavorite)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<IcdO3SearchResult>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // クエリが空の場合はお気に入りリストを表示
                repository.getFavorites()
            } else {
                // クエリがある場合は通常検索（コード／英語名称／日本語名称）
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

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val topographySearchResults: StateFlow<List<IcdO3TopographySearchResult>> = _topographySearchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // クエリが空の場合はお気に入りリストを表示
                topographyRepository.getFavorites()
            } else {
                // クエリがある場合は通常検索（コード／英語名称／日本語名称）
                val normalizedInput = normalizeTextForSearch(query)
                val words = normalizedInput.trim().split(Regex("\\s+"))
                topographyRepository.searchMulti(words)
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
                return IcdO3ViewModel(application) as T
            }
        }
    }
}
