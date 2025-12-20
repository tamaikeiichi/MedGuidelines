// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/viewmodel/DpcScreenViewModel.kt

package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.DpcRepository
import com.keiichi.medguidelines.data.IcdEntity // IcdEntityをインポート
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// AndroidViewModelを継承して、Applicationコンテキストを使えるようにする
class DpcScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DpcRepository

    // --- StateFlowの定義 ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 検索クエリを保持するStateFlow
    private val _searchQuery = MutableStateFlow("")

    init {
        // データベースとリポジトリを初期化
        val dpcDao = AppDatabase.getDatabase(application).dpcDao()
        repository = DpcRepository(dpcDao)

        // アプリ起動時にデータベースの初期化処理を呼び出す
        initializeDatabase()
    }

    /**
     * データベースが空ならExcelからデータを読み込む
     */
    private fun initializeDatabase() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.populateDatabaseFromExcelIfEmpty(getApplication())
            } catch (e: Exception) {
                _errorMessage.value = "データベースの初期化に失敗しました: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 検索クエリを更新する
     */
    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /**
     * 検索クエリの変更を監視し、リポジトリに検索を依頼する
     * 結果はStateFlowとしてUIに公開される
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val displayedItemsIcd: StateFlow<List<IcdEntity>> = _searchQuery
        .filter { it.isNotBlank() } // クエリが空でない場合のみ処理
        .debounce(300) // 300ミリ秒待ってから検索を実行（入力中の負荷を軽減）
        .flatMapLatest { query ->
            repository.searchIcd(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // UIがアクティブな間だけ監視
            initialValue = emptyList() // 初期値は空のリスト
        )

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Applicationのインスタンスを取得
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // DpcScreenViewModelのインスタンスを生成して返す
                return DpcScreenViewModel(application) as T
            }
        }
    }
}
