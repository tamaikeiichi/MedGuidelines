// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/viewmodel/DpcScreenViewModel.kt

package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.BunruiEntity
import com.keiichi.medguidelines.data.DpcRepository
import com.keiichi.medguidelines.data.IcdEntity // IcdEntityをインポート
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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

    // --- ここからByotai関連のStateFlowを追加 ---

    // 病態選択UIを表示するかどうか
    private val _showByotaiSelection = kotlinx.coroutines.flow.MutableStateFlow(false)
    val showByotaiSelection: StateFlow<Boolean> = _showByotaiSelection.asStateFlow()

    // 病態ドロップダウンの選択肢リスト
    private val _byotaiOptions = kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())
    val byotaiOptions: StateFlow<List<String>> = _byotaiOptions.asStateFlow()



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
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
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
    /**
     * ICDが選択されたときにUIから呼び出されるメソッド
     * @param mdcCode 選択されたMDCコード
     * @param bunruiCode 選択された分類コード
     */
    fun onMdcAndBunruiSelected(mdcCode: String, bunruiCode: String) {
        viewModelScope.launch {
            // byotaiマスターに該当データが存在するかチェック
            val exists = repository.checkMdcAndBunruiExist(mdcCode, bunruiCode)
            _showByotaiSelection.value = exists
            if (exists) {
                // 存在すれば、ドロップダウンの選択肢を取得する
                _byotaiOptions.value = repository.getByotaiNames(mdcCode, bunruiCode)
            } else {
                // 存在しなければ選択肢をクリア
                _byotaiOptions.value = emptyList()
            }
        }
    }

    /**
     * ドロップダウンで病態名が選択されたときに呼び出されるメソッド
     * @param byotaiName 選択された病態名
     * @return 選択された病態名に対応する病態コード
     */
    suspend fun getByotaiCode(byotaiName: String): String? {
        return repository.getByotaiCodeByName(byotaiName)
    }

    // --- ここからBunruiの検索結果を追加 ---
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val displayedItemsBunrui: StateFlow<List<BunruiEntity>> = _searchQuery
        .filter { it.isNotBlank() } // クエリが空でない場合のみ
        .debounce(300)              // 300ms待ってから検索
        .flatMapLatest { query ->     // 最新のクエリで検索を実行
            repository.searchBunrui(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), // UIがアクティブな間だけ監視
            initialValue = emptyList() // 初期値は空リスト
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
