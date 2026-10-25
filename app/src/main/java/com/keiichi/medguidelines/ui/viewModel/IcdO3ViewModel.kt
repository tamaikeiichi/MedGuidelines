package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.ICD_O_EDITION_32
import com.keiichi.medguidelines.data.ICD_O_EDITION_4
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
    // 版ごとのリポジトリ（ICD-O-3.2 / ICD-O-4）
    private val repositories: Map<String, IcdO3Repository>
    private val topographyRepositories: Map<String, IcdO3TopographyRepository>

    private val _edition = MutableStateFlow(ICD_O_EDITION_4)
    val edition: StateFlow<String> = _edition.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedGradeDigit = MutableStateFlow("")
    val selectedGradeDigit: StateFlow<String> = _selectedGradeDigit.asStateFlow()

    private val _topographySearchQuery = MutableStateFlow("")
    val topographySearchQuery: StateFlow<String> = _topographySearchQuery.asStateFlow()

    // カードタップで選択されたコード（検索バーがこのコードと一致する間は完全一致検索にする）
    private val _selectedCode = MutableStateFlow<String?>(null)
    private val _selectedTopographyCode = MutableStateFlow<String?>(null)

    private val _isTopographyLoading = MutableStateFlow(false)
    val isTopographyLoading: StateFlow<Boolean> = _isTopographyLoading.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repositories = listOf(ICD_O_EDITION_32, ICD_O_EDITION_4).associateWith { IcdO3Repository(db.icdO3Dao(), it) }
        topographyRepositories = listOf(ICD_O_EDITION_32, ICD_O_EDITION_4)
            .associateWith { IcdO3TopographyRepository(db.icdO3TopographyDao(), it) }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repositories.getValue(ICD_O_EDITION_32).populateDatabaseFromCsvIfNeeded(
                    context = application,
                    resourceId = R.raw.icd_o3_morphology
                )
                repositories.getValue(ICD_O_EDITION_4).populateDatabaseFromCsvIfNeeded(
                    context = application,
                    resourceId = R.raw.icd_o4_morphology
                )
            } finally {
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            _isTopographyLoading.value = true
            try {
                topographyRepositories.getValue(ICD_O_EDITION_32).populateDatabaseFromCsvIfNeeded(
                    context = application,
                    resourceId = R.raw.icd_o3_topography
                )
                topographyRepositories.getValue(ICD_O_EDITION_4).populateDatabaseFromCsvIfNeeded(
                    context = application,
                    resourceId = R.raw.icd_o4_topography
                )
            } finally {
                _isTopographyLoading.value = false
            }
        }
    }

    // 版を切り替えたら、検索語はそのままで、カード選択（コード完全一致）だけ解除する
    fun onEditionChanged(newEdition: String) {
        _selectedCode.value = null
        _selectedTopographyCode.value = null
        _edition.value = newEdition
    }

    fun onQueryChanged(newQuery: String) {
        _selectedCode.value = null
        _searchQuery.value = newQuery
    }

    // カードがタップされたら、検索バーをそのコードに置き換えてカード1枚だけを表示する
    fun onCardSelected(code: String) {
        _selectedCode.value = code
        _searchQuery.value = code
    }

    fun onGradeDigitChanged(digit: String) {
        _selectedGradeDigit.value = digit
    }

    fun toggleFavorite(item: IcdO3SearchResult) {
        viewModelScope.launch {
            repositories.getValue(_edition.value).updateFavorite(item.id, !item.isFavorite)
        }
    }

    fun onTopographyQueryChanged(newQuery: String) {
        _selectedTopographyCode.value = null
        _topographySearchQuery.value = newQuery
    }

    fun onTopographyCardSelected(code: String) {
        _selectedTopographyCode.value = code
        _topographySearchQuery.value = code
    }

    fun toggleTopographyFavorite(item: IcdO3TopographySearchResult) {
        viewModelScope.launch {
            topographyRepositories.getValue(_edition.value).updateFavorite(item.id, !item.isFavorite)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<IcdO3SearchResult>> = combine(
        _searchQuery, _selectedCode, _edition
    ) { query, selected, edition -> Triple(query, query == selected, edition) }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, isExactCode, edition) ->
            val repository = repositories.getValue(edition)
            if (query.isBlank()) {
                // クエリが空の場合はお気に入りリストを表示
                repository.getFavorites()
            } else if (isExactCode) {
                repository.searchByCode(query)
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
    val topographySearchResults: StateFlow<List<IcdO3TopographySearchResult>> = combine(
        _topographySearchQuery, _selectedTopographyCode, _edition
    ) { query, selected, edition -> Triple(query, query == selected, edition) }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { (query, isExactCode, edition) ->
            val topographyRepository = topographyRepositories.getValue(edition)
            if (query.isBlank()) {
                // クエリが空の場合はお気に入りリストを表示
                topographyRepository.getFavorites()
            } else if (isExactCode) {
                topographyRepository.searchByCode(query)
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
