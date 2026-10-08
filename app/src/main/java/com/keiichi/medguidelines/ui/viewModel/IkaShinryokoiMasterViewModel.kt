package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.IkaShinryokoiRepository
import com.keiichi.medguidelines.data.PairedTextItem
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class IkaShinryokoiMasterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: IkaShinryokoiRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = IkaShinryokoiRepository(db.ikaShinryokoiDao())

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.populateDatabaseFromCsvIfEmpty(
                    context = application,
                    resourceId = R.raw.s_20250602
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleFavorite(item: PairedTextItem) {
        viewModelScope.launch {
            repository.updateFavorite(item.originalIndex, !item.isFavorite)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: StateFlow<List<PairedTextItem>> = _searchQuery
        .debounce(350)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            // 全角・半角スペースで区切り、各ワードを正規化してAND検索（ワードが無ければ全件表示）
            val words = query
                .split(Regex("[ 　]+"))
                .map { normalizeTextForSearch(it) }
                .filter { it.isNotBlank() }
            repository.searchMulti(words)
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
                return IkaShinryokoiMasterViewModel(application) as T
            }
        }
    }
}
