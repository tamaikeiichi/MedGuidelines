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
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import com.keiichi.medguidelines.ui.screen.LabelStringAndScore
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

    private val _showNenreiSelection = MutableStateFlow(false)
    val showNenreiSelection: StateFlow<Boolean> = _showNenreiSelection.asStateFlow()

    // 病態ドロップダウンの選択肢リスト
    private val _byotaiOptions = kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())
    val byotaiOptions: StateFlow<List<String>> = _byotaiOptions.asStateFlow()

    // 年齢ラジオボタン
    private val _nenreiOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val nenreiOptions: StateFlow<List<LabelStringAndScore>> = _nenreiOptions.asStateFlow()

    /**
     * 【修正案1】ICDリストの項目が選択されたときに呼び出されるメソッド
     * @param item 選択された IcdEntity
     */
    fun onIcdItemSelected(item: IcdEntity) {
        // 選択された項目のmdcCodeとbunruiCodeを使って、共通のチェック処理を呼び出す
        checkAndShowByotaiSelection(item.mdcCode, item.bunruiCode)
    }

    /**
     * 【修正案2】分類リストの項目が選択されたときに呼び出されるメソッド
     * @param item 選択された BunruiEntity
     */
    fun onBunruiItemSelected(item: BunruiEntity) {
        // 選択された項目のmdcCodeとbunruiCodeを使って、共通のチェック処理を呼び出す
        checkAndShowByotaiSelection(item.mdcCode, item.bunruiCode)
    }

    /**
     * 病態名が選択されたときに呼び出される
     * @param mdcCode 選択された項目のMDCコード
     * @param bunruiCode 選択された項目の分類コード
     * @param byotaiCode 選択された病態コード
     */
    fun onNenreiSelected(mdcCode: String?, bunruiCode: String?, byotaiCode: String?) {
        viewModelScope.launch {
            if (mdcCode == null || bunruiCode == null || byotaiCode == null) {
                _showNenreiSelection.value = false // 必要な情報がなければ非表示
                return@launch
            }

            // --- ここでViewModelがロジックを実行 ---
            // リポジトリやDAO経由で年齢条件を取得する (この関数はRepositoryに実装する必要がある)
            val joken1Ijo = repository.getNenreiJoken1Ijo(mdcCode, bunruiCode)
            val joken1Miman = repository.getNenreiJoken1Miman(mdcCode, bunruiCode)
            val joken2Ijo = repository.getNenreiJoken2Ijo(mdcCode, bunruiCode)
            val joken2Miman = repository.getNenreiJoken2Miman(mdcCode, bunruiCode)
            val joken3Ijo = repository.getNenreiJoken3Ijo(mdcCode, bunruiCode)
            val joken3Miman = repository.getNenreiJoken3Miman(mdcCode, bunruiCode)
            val joken4Ijo = repository.getNenreiJoken4Ijo(mdcCode, bunruiCode)
            val joken4Miman = repository.getNenreiJoken4Miman(mdcCode, bunruiCode)
            val joken5Ijo = repository.getNenreiJoken5Ijo(mdcCode, bunruiCode)
            val joken5Miman = repository.getNenreiJoken5Miman(mdcCode, bunruiCode)

            val joken1Value = repository.getNenreiJoken1Value(mdcCode, bunruiCode)
            val joken2Value = repository.getNenreiJoken2Value(mdcCode, bunruiCode)
            val joken3Value = repository.getNenreiJoken3Value(mdcCode, bunruiCode)
            val joken4Value = repository.getNenreiJoken4Value(mdcCode, bunruiCode)
            val joken5Value = repository.getNenreiJoken5Value(mdcCode, bunruiCode)




            // ... joken2, 3, 4, 5も同様に取得 ...
            val joken1String =
                joken1Ijo.toInt().toString() + "以上" + joken1Miman.toInt().toString() + "未満"
            val joken2String: String? = if (joken2Ijo != null) {
                joken2Ijo.toInt().toString() + "以上" + joken2Miman.toInt().toString() + "未満"
            } else {
                null
            }
            val joken3String: String? = if (joken3Ijo != null) {
                joken3Ijo.toInt().toString() + "以上" + joken3Miman.toInt().toString() + "未満"
            } else {
                null
            }
            val joken4String: String? = if (joken4Ijo != null) {
                joken4Ijo.toInt().toString() + "以上" + joken4Miman.toInt().toString() + "未満"
            } else {
                null
            }
            val joken5String: String? = if (joken5Ijo != null) {
                joken5Ijo.toInt().toString() + "以上" + joken5Miman.toInt().toString() + "未満"
            } else {
                null
            }

            // 有効な選択肢だけをリストにする
            val options: List<LabelStringAndScore> = buildList {
                add(LabelStringAndScore(joken1String, joken1Value.toInt()))
                add(LabelStringAndScore(joken2String, joken2Value.toInt()))
                add(LabelStringAndScore(joken3String, joken3Value.toInt()))
                add(LabelStringAndScore(joken4String, joken4Value.toInt()))
                add(LabelStringAndScore(joken5String, joken5Value.toInt()))
            }

            if (options.isNotEmpty()) {
                _nenreiOptions.value = options
                _showNenreiSelection.value = true // ★ 年齢選択UIを表示
            } else {
                _showNenreiSelection.value = false
            }
        }
    }

    /**
     * MDCコードと分類コードを元に、病態選択UIの表示を制御する共通メソッド
     * @param mdcCode チェックするMDCコード
     * @param bunruiCode チェックする分類コード
     */
    private fun checkAndShowByotaiSelection(mdcCode: String?, bunruiCode: String?) {
        viewModelScope.launch {
            // mdcCodeとbunruiCodeがnullでないことを確認
            if (mdcCode != null && bunruiCode != null) {
                // 1. Repositoryに問い合わせて、対応する病態が存在するかチェック
                val byotaiExists = repository.checkMdcAndBunruiExist(mdcCode, bunruiCode)

                // 2. 存在する場合 (true の場合) のみ、UIの表示とデータ取得を行う
                if (byotaiExists) {
                    _byotaiOptions.value = repository.getByotaiNames(mdcCode, bunruiCode)
                    _showByotaiSelection.value = true // ★ 病態選択UIを表示させる
                } else {
                    // 存在しない場合は、UIを非表示にする
                    _showByotaiSelection.value = false
                    _byotaiOptions.value = emptyList()
                }
            } else {
                // mdcCodeまたはbunruiCodeがnullの場合は、UIを非表示にする
                _showByotaiSelection.value = false
                _byotaiOptions.value = emptyList()
            }
        }
    }

// DpcScreenViewModel.kt

    private fun checkAndShowNenreiSelection(mdcCode: String?, bunruiCode: String?) {
        viewModelScope.launch {
            // mdcCodeとbunruiCodeがnullでないことを確認
            if (mdcCode != null && bunruiCode != null) {
                // --- ここからが修正箇所 ---

                // 1. 年齢条件を決定するために必要な値をすべてリポジトリから取得する
                val joken1Ijo = repository.getNenreiJoken1Ijo(mdcCode, bunruiCode)
                val joken1Miman = repository.getNenreiJoken1Miman(mdcCode, bunruiCode)
                val joken1Value = repository.getNenreiJoken1Value(mdcCode, bunruiCode)

                val joken2Ijo = repository.getNenreiJoken2Ijo(mdcCode, bunruiCode)
                val joken2Miman = repository.getNenreiJoken2Miman(mdcCode, bunruiCode)
                val joken2Value = repository.getNenreiJoken2Value(mdcCode, bunruiCode)

                val joken3Ijo = repository.getNenreiJoken3Ijo(mdcCode, bunruiCode)
                val joken3Miman = repository.getNenreiJoken3Miman(mdcCode, bunruiCode)
                val joken3Value = repository.getNenreiJoken3Value(mdcCode, bunruiCode)

                val joken4Ijo = repository.getNenreiJoken4Ijo(mdcCode, bunruiCode)
                val joken4Miman = repository.getNenreiJoken4Miman(mdcCode, bunruiCode)
                val joken4Value = repository.getNenreiJoken4Value(mdcCode, bunruiCode)

                val joken5Ijo = repository.getNenreiJoken5Ijo(mdcCode, bunruiCode)
                val joken5Miman = repository.getNenreiJoken5Miman(mdcCode, bunruiCode)
                val joken5Value = repository.getNenreiJoken5Value(mdcCode, bunruiCode)

                // 2. 取得した値からラベル文字列を安全に生成する
                val joken1String: String? = if (joken1Ijo != null && joken1Miman != null) {
                    "${joken1Ijo.toInt()}以上${joken1Miman.toInt()}未満"
                } else { null }
                val joken2String: String? = if (joken2Ijo != null && joken2Miman != null) {
                    "${joken2Ijo.toInt()}以上${joken2Miman.toInt()}未満"
                } else { null }
                val joken3String: String? = if (joken3Ijo != null && joken3Miman != null) {
                    "${joken3Ijo.toInt()}以上${joken3Miman.toInt()}未満"
                } else { null }
                val joken4String: String? = if (joken4Ijo != null && joken4Miman != null) {
                    "${joken4Ijo.toInt()}以上${joken4Miman.toInt()}未満"
                } else { null }
                val joken5String: String? = if (joken5Ijo != null && joken5Miman != null) {
                    "${joken5Ijo.toInt()}以上${joken5Miman.toInt()}未満"
                } else { null }

                // 3. nullでない有効な選択肢だけをリストに追加する
                val options: List<LabelStringAndScore> = buildList {
                    if (joken1String != null && joken1Value != null) add(LabelStringAndScore(joken1String, joken1Value.toInt()))
                    if (joken2String != null && joken2Value != null) add(LabelStringAndScore(joken2String, joken2Value.toInt()))
                    if (joken3String != null && joken3Value != null) add(LabelStringAndScore(joken3String, joken3Value.toInt()))
                    if (joken4String != null && joken4Value != null) add(LabelStringAndScore(joken4String, joken4Value.toInt()))
                    if (joken5String != null && joken5Value != null) add(LabelStringAndScore(joken5String, joken5Value.toInt()))
                }

                // 4. 生成したリストをStateFlowにセットし、UIの表示を制御する
                _nenreiOptions.value = options
                _showNenreiSelection.value = options.isNotEmpty()

                // --- ここまでが修正箇所 ---

            } else {
                // mdcCodeまたはbunruiCodeがnullの場合は、UIを非表示にする
                _showNenreiSelection.value = false
                _nenreiOptions.value = emptyList()
            }
        }
    }




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
        val normalizedQuery = normalizeTextForSearch(newQuery)
        _searchQuery.value = normalizedQuery // 正規化済みクエリをFlowに渡す

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


    /**
     * 【追加】病態選択UIの状態をリセットする。
     * 新しい検索が始まったときなどにUIから呼び出す。
     */
    fun resetByotaiSelection() {
        _showByotaiSelection.value = false
        _byotaiOptions.value = emptyList()
    }
}
