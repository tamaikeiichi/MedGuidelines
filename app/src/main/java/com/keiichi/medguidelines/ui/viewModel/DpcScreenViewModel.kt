// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/viewmodel/DpcScreenViewModel.kt

package com.keiichi.medguidelines.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.DpcRepository
import com.keiichi.medguidelines.data.FukushobyoJoken
import com.keiichi.medguidelines.data.FukushobyoRepository
import com.keiichi.medguidelines.data.IcdEntity
import com.keiichi.medguidelines.data.JushodoJcsRepository
import com.keiichi.medguidelines.data.JushodoShujutsuRepository
import com.keiichi.medguidelines.data.JushodoStrokeRepository
import com.keiichi.medguidelines.data.NenreiRepository
import com.keiichi.medguidelines.data.ShindangunBunruiTensuhyoJoken
import com.keiichi.medguidelines.data.ShindangunBunruiTensuhyoRepository
import com.keiichi.medguidelines.data.Shochi1Joken
import com.keiichi.medguidelines.data.Shochi1Repository
import com.keiichi.medguidelines.data.Shochi2Joken
import com.keiichi.medguidelines.data.Shochi2Repository
import com.keiichi.medguidelines.data.ShujutsuRepository
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import com.keiichi.medguidelines.ui.screen.LabelStringAndScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.first

// AndroidViewModelを継承して、Applicationコンテキストを使えるようにする
class DpcScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DpcRepository
    private val shujutsuRepository: ShujutsuRepository
    private val shochi1Repository: Shochi1Repository
    private val shochi2Repository: Shochi2Repository
    private val fukushobyoRepository: FukushobyoRepository
    private val jushodoJcsRepository: JushodoJcsRepository
    private val nenreiRepository: NenreiRepository
    private val jushodoShujutsuRepository: JushodoShujutsuRepository
    private val jushodoStrokeRepository: JushodoStrokeRepository
    private val shindangunBunruiTensuhyoRepository: ShindangunBunruiTensuhyoRepository

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

    private val _showShujutsuSelection = MutableStateFlow(false)
    val showShujutsuSelection: StateFlow<Boolean> = _showShujutsuSelection.asStateFlow()

    private val _showShochi1Selection = MutableStateFlow(false)
    val showShochi1Selection: StateFlow<Boolean> = _showShochi1Selection.asStateFlow()

    private val _showShochi2Selection = MutableStateFlow(false)
    val showShochi2Selection: StateFlow<Boolean> = _showShochi2Selection.asStateFlow()
    private val _showFukushobyoSelection = MutableStateFlow(false)
    val showFukushobyoSelection: StateFlow<Boolean> = _showFukushobyoSelection.asStateFlow()

    private val _showJushodoJcsSelection = MutableStateFlow(false)
    val showJushodoJcsSelection: StateFlow<Boolean> = _showJushodoJcsSelection.asStateFlow()

    private val _showJushodoShujutsuSelection = MutableStateFlow(false)
    val showJushodoShujutsuSelection: StateFlow<Boolean> =
        _showJushodoShujutsuSelection.asStateFlow()

    private val _showJushodoStrokeSelection = MutableStateFlow(false)
    val showJushodoStrokeSelection: StateFlow<Boolean> = _showJushodoStrokeSelection.asStateFlow()

    private val _showIcdName = MutableStateFlow(false)
    val showIcdName: StateFlow<Boolean> = _showIcdName.asStateFlow()

    // 病態ドロップダウンの選択肢リスト
    private val _byotaiOptions = kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())
    val byotaiOptions: StateFlow<List<String>> = _byotaiOptions.asStateFlow()

    // 年齢ラジオボタン
    private val _nenreiOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val nenreiOptions: StateFlow<List<LabelStringAndScore>> = _nenreiOptions.asStateFlow()

    private val _shujutsuOptions =
        kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())
    val shujutsuOptions: StateFlow<List<String>> = _shujutsuOptions.asStateFlow()

    private val _shochi1Options = MutableStateFlow<List<Shochi1Joken>>(emptyList())
    val shochi1Options: StateFlow<List<Shochi1Joken>> = _shochi1Options.asStateFlow()

    private val _shochi2Options = MutableStateFlow<List<Shochi2Joken>>(emptyList())
    val shochi2Options: StateFlow<List<Shochi2Joken>> = _shochi2Options.asStateFlow()

    private val _fukushobyoOptions = MutableStateFlow<List<FukushobyoJoken>>(emptyList())
    val fukushobyoOptions: StateFlow<List<FukushobyoJoken>> = _fukushobyoOptions.asStateFlow()

    private val _jushodoJcsOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val jushodoJcsOptions: StateFlow<List<LabelStringAndScore>> = _jushodoJcsOptions.asStateFlow()
    private val _jushodoShujutsuOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val jushodoShujutsuOptions: StateFlow<List<LabelStringAndScore>> =
        _jushodoShujutsuOptions.asStateFlow()

    private val _jushodoStrokeOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val jushodoStrokeOptions: StateFlow<List<LabelStringAndScore>> =
        _jushodoStrokeOptions.asStateFlow()

    private val _shindangunBunruiTensuhyoOptions =
        MutableStateFlow<List<ShindangunBunruiTensuhyoJoken>>(emptyList())
    val shindangunBunruiTensuhyoOptions: StateFlow<List<ShindangunBunruiTensuhyoJoken>> =
        _shindangunBunruiTensuhyoOptions.asStateFlow()

    fun onShindangunCodeChanged(code: String) {
        viewModelScope.launch {
            // 引数自体は置換せず、そのまま渡す
           _isLoading.value = true
            try {
                val result = shindangunBunruiTensuhyoRepository.getNames(code)
                _shindangunBunruiTensuhyoOptions.value = result

                Log.d("tamaiDpc", "Shindangun options updated: ${result.size} items, ")
            } catch (e: Exception) {
                Log.e("tamaiDpc", "Failed to fetch Shindangun options", e)
                _shindangunBunruiTensuhyoOptions.value = emptyList()
            } finally {
               _isLoading.value = false
            }
        }
    }

    // DpcScreenViewModel.kt 内に追加または修正

    fun resetAllSelections() {
        _showByotaiSelection.value = false
        _showNenreiSelection.value = false
        _showShujutsuSelection.value = false
        _showShochi1Selection.value = false
        _showShochi2Selection.value = false
        _showFukushobyoSelection.value = false
        _showJushodoJcsSelection.value = false
        _showJushodoShujutsuSelection.value = false
        _showJushodoStrokeSelection.value = false
        _showIcdName.value = false

        // 必要に応じてオプションリストもクリア
        _byotaiOptions.value = emptyList()
        // 他のオプション（shindangun等）もあればここでリセット
    }

    suspend fun getDebugRows(): List<String> {
        // repository経由、あるいは直接daoから取得
        return shindangunBunruiTensuhyoRepository.getFirstThreeRows()
    }

    /**
     * 【修正案1】ICDリストの項目が選択されたときに呼び出されるメソッド
     * @param item 選択された IcdEntity
     */
    fun onIcdItemSelected(item: IcdEntity) {
        viewModelScope.launch {
            _showIcdName.value = true
            // --- 1. 病態選択UIの表示判断 ---
            // 選択された項目のmdcCodeとbunruiCodeがnullでないことを確認
            if (item.mdcCode != null && item.bunruiCode != null) {
                // 対応する病態が存在するかチェック
                Log.d(
                    "tamaiDpc",
                    "after if (item.mdcCode != null && item.bunruiCode != null) mdcCode ${item.mdcCode} bunruiCode ${item.bunruiCode}"
                )
                val byotaiExists = repository.checkMdcAndBunruiExist(item.mdcCode, item.bunruiCode)
                if (byotaiExists) {
                    // 存在すれば、病態の選択肢を準備してUIを表示させる
                    _byotaiOptions.value = repository.getByotaiNames(item.mdcCode, item.bunruiCode)
                    _showByotaiSelection.value = true
                } else {
                    // 存在しなければ、UIを非表示にする
                    _showByotaiSelection.value = false
                    _byotaiOptions.value = emptyList()
                }
            } else {
                _showByotaiSelection.value = false
                _byotaiOptions.value = emptyList()
            }

            // --- 2. 年齢選択UIの表示判断 ---
            // 選択された項目のbunruiCodeがnullでないことを確認
            if (item.mdcCode != null && item.bunruiCode != null) {
                // nenrei_masterテーブルにbunruiCodeが存在するかチェック
                val nenreiDataExists = repository.checkMdcAndBunruiExistsInNenrei(item.mdcCode, item.bunruiCode)
                val jushodoJcsDateExists =
                    jushodoJcsRepository.checkMdcAndBunruiExistsInMaster(item.mdcCode, item.bunruiCode)
                val jushodoShujutsuDataExists =
                    jushodoShujutsuRepository.checkBunruiExistsInMaster(
                        item.mdcCode,
                        item.bunruiCode
                    )
                val jushodoStrokeDataExists =
                    jushodoStrokeRepository.checkMdcAndBunruiExistInMaster(
                        item.mdcCode,
                        item.bunruiCode
                    )
                Log.d("dpcJushodoShujutsu", "jushodoShujutsuDataExists $jushodoShujutsuDataExists")
                if (nenreiDataExists && item.mdcCode != null) {
                    // ★ 存在すれば、年齢条件の選択肢リストを生成する
                    _nenreiOptions.value = createNenreiOptionsList(item.mdcCode, item.bunruiCode)
                    _showNenreiSelection.value = _nenreiOptions.value.isNotEmpty()
                } else {
                    // 存在しなければ、UIを非表示にする
                    _showNenreiSelection.value = false
                    _nenreiOptions.value = emptyList()
                }
                if (jushodoJcsDateExists && item.mdcCode != null) {
                    // ★ 存在すれば、年齢条件の選択肢リストを生成する
                    _jushodoJcsOptions.value =
                        createJushoJcsJokenOptionsList(item.mdcCode, item.bunruiCode)
                    _showJushodoJcsSelection.value = _nenreiOptions.value.isNotEmpty()
                } else {
                    // 存在しなければ、UIを非表示にする
                    _showJushodoJcsSelection.value = false
                    _jushodoJcsOptions.value = emptyList()
                }
                if (jushodoShujutsuDataExists && item.mdcCode != null) {
                    // ★ 存在すれば、年齢条件の選択肢リストを生成する
                    _jushodoShujutsuOptions.value =
                        createJushoShujutsuJokenOptionsList(item.mdcCode, item.bunruiCode)
                    _showJushodoShujutsuSelection.value = true
                } else {
                    // 存在しなければ、UIを非表示にする
                    _showJushodoShujutsuSelection.value = false
                    _jushodoShujutsuOptions.value = emptyList()
                }
                if (jushodoStrokeDataExists && item.mdcCode != null) {
                    // ★ 存在すれば、年齢条件の選択肢リストを生成する
                    _jushodoStrokeOptions.value =
                        createJushoStrokeJokenOptionsList(item.mdcCode, item.bunruiCode)
                    _showJushodoStrokeSelection.value = true
                } else {
                    // 存在しなければ、UIを非表示にする
                    _showJushodoStrokeSelection.value = false
                    _jushodoStrokeOptions.value = emptyList()
                }
            } else {
                _showNenreiSelection.value = false
                _nenreiOptions.value = emptyList()
            }

            if (item.mdcCode != null && item.bunruiCode != null) {
                Log.d(
                    "tamaiDpc",
                    "for shujutsu, after if (item.mdcCode != null && item.bunruiCode != null) mdcCode ${item.mdcCode} bunruiCode ${item.bunruiCode}"
                )

                // 対応する病態が存在するかチェック
                val shujutsuExists = shujutsuRepository.checkMdcAndBunruiExistsInShujutsu(item.mdcCode, item.bunruiCode)
                val shochi1Exists = shochi1Repository.checkMdcAndBunruiExistsInShochi1(item.mdcCode, item.bunruiCode)
                val shochi2Exists = shochi2Repository.checkMdcAndBunruiExistsInMaster(item.mdcCode, item.bunruiCode)
                val fukushobyoExists =
                    fukushobyoRepository.checkMdcAndBunruiExistsInMaster(item.mdcCode, item.bunruiCode)
                val jushodoJcsExists =
                    jushodoJcsRepository.checkMdcAndBunruiExistsInMaster(item.mdcCode, item.bunruiCode)
                val jushodoStrokeExists = jushodoStrokeRepository.checkMdcAndBunruiExistInMaster(
                    item.mdcCode,
                    item.bunruiCode
                )

                Log.d(
                    "tamaiDpc",
                    "shujutsuExists ${shujutsuExists} item.bunruiCode ${item.bunruiCode}"
                )

                // --- updateSelectionStateヘルパー関数を使って、各選択UIの状態を更新 ---
                updateSelectionState(
                    exists = shujutsuExists,
                    optionsFlow = _shujutsuOptions,
                    showSelectionFlow = _showShujutsuSelection,
                    getOptions = {
                        shujutsuRepository.getShujutsuNames(
                            item.mdcCode,
                            item.bunruiCode
                        )
                    }
                )
                updateSelectionState(
                    exists = shochi1Exists,
                    optionsFlow = _shochi1Options,
                    showSelectionFlow = _showShochi1Selection,
                    getOptions = { shochi1Repository.getNames(item.mdcCode, item.bunruiCode) }
                )
                updateSelectionState(
                    exists = shochi2Exists,
                    optionsFlow = _shochi2Options,
                    showSelectionFlow = _showShochi2Selection,
                    getOptions = { shochi2Repository.getNames(item.mdcCode, item.bunruiCode) }
                )
                updateSelectionState(
                    exists = fukushobyoExists,
                    optionsFlow = _fukushobyoOptions,
                    showSelectionFlow = _showFukushobyoSelection,
                    getOptions = { fukushobyoRepository.getNames(item.mdcCode, item.bunruiCode) }
                )
                // jushodoJcsOptionsはList<JushodoJcsJoken>型だが、ジェネリクス<T>のおかげで同じ関数を使える
                updateSelectionState(
                    exists = jushodoJcsExists,
                    optionsFlow = _jushodoJcsOptions,
                    showSelectionFlow = _showJushodoJcsSelection,
                    // ★ getJushodoJokenはList<JushodoJcsJoken>を返すが、型推論で正しく動作する
                    // ★ ただし、getJushodoJokenはListを返すように修正が必要（現在は単一オブジェクト）
                    getOptions = { createJushoJcsJokenOptionsList(item.mdcCode, item.bunruiCode) }
                )
                updateSelectionState(
                    exists = jushodoStrokeExists,
                    optionsFlow = _jushodoStrokeOptions,
                    showSelectionFlow = _showJushodoStrokeSelection,
                    // ★ getJushodoJokenはList<JushodoJcsJoken>を返すが、型推論で正しく動作する
                    // ★ ただし、getJushodoJokenはListを返すように修正が必要（現在は単一オブジェクト）
                    getOptions = {
                        createJushoStrokeJokenOptionsList(
                            item.mdcCode,
                            item.bunruiCode
                        )
                    }
                )
            } else {
                _showByotaiSelection.value = false
                _byotaiOptions.value = emptyList()
            }
        }
    }

    /**
     * 【追加】年齢条件の選択肢リストを生成するヘルパー関数
     */
    private suspend fun createNenreiOptionsList(
        mdcCode: String,
        bunruiCode: String
    ): List<LabelStringAndScore> {
        val nenreiJoken = nenreiRepository.getNenreiJoken(mdcCode, bunruiCode)
            ?: return emptyList() // データがなければ空リストを返して終了

        Log.d("tamaiDpc", "nenreiJoken data: $nenreiJoken")

        // 2. 取得したデータオブジェクトを使ってリストを構築
        return buildList {
            formatNenreiLabel(nenreiJoken.joken1Ijo, nenreiJoken.joken1Miman)?.let { labelText ->
                val score = nenreiJoken.joken1Value?.toIntOrNull() ?: 0
                add(LabelStringAndScore(labelText, score, nenreiJoken.jokenName))
            }
            formatNenreiLabel(nenreiJoken.joken2Ijo, nenreiJoken.joken2Miman)?.let { labelText ->
                val score = nenreiJoken.joken2Value?.toIntOrNull() ?: 0
                add(LabelStringAndScore(labelText, score, nenreiJoken.jokenName))
            }
            formatNenreiLabel(nenreiJoken.joken3Ijo, nenreiJoken.joken3Miman)?.let { labelText ->
                val score = nenreiJoken.joken3Value?.toIntOrNull() ?: 0
                add(LabelStringAndScore(labelText, score, nenreiJoken.jokenName))
            }
            formatNenreiLabel(nenreiJoken.joken4Ijo, nenreiJoken.joken4Miman)?.let { labelText ->
                val score = nenreiJoken.joken4Value?.toIntOrNull() ?: 0
                add(LabelStringAndScore(labelText, score, nenreiJoken.jokenName))
            }
            formatNenreiLabel(nenreiJoken.joken5Ijo, nenreiJoken.joken5Miman)?.let { labelText ->
                val score = nenreiJoken.joken5Value?.toIntOrNull() ?: 0
                add(LabelStringAndScore(labelText, score, nenreiJoken.jokenName))
            }
        }
    }

    // 検索クエリを保持するStateFlow
    private val _searchQuery = MutableStateFlow("")

    init {
        // データベースとリポジトリを初期化
        val dpcDao = AppDatabase.getDatabase(application).dpcDao()
        val shujutsuDao = AppDatabase.getDatabase(application).shujutsuDao()
        val shochi1Dao = AppDatabase.getDatabase(application).shochi1Dao()
        val shochi2Dao = AppDatabase.getDatabase(application).shochi2Dao()
        val fukushobyoDao = AppDatabase.getDatabase(application).fukushobyoDao()
        val jushodoJcsDao = AppDatabase.getDatabase(application).jushodoJcsDao()
        val nenreiDao = AppDatabase.getDatabase(application).nenreiDao()
        val jushodoShujutsuDao = AppDatabase.getDatabase(application).jushodoShujutsuDao()
        val jushodoStrokeDao = AppDatabase.getDatabase(application).jushodoStrokeDao()
        val shindangunBunruiTensuhyoDao =
            AppDatabase.getDatabase(application).shindangunBunruiTensuhyoDao()

        repository = DpcRepository(dpcDao)
        shujutsuRepository = ShujutsuRepository(shujutsuDao) // shujutsuRepositoryを初期化
        shochi1Repository = Shochi1Repository(shochi1Dao)
        shochi2Repository = Shochi2Repository(shochi2Dao)
        fukushobyoRepository = FukushobyoRepository(fukushobyoDao)
        jushodoJcsRepository = JushodoJcsRepository(jushodoJcsDao)
        nenreiRepository = NenreiRepository(nenreiDao)
        jushodoShujutsuRepository = JushodoShujutsuRepository(jushodoShujutsuDao)
        jushodoStrokeRepository = JushodoStrokeRepository(jushodoStrokeDao)
        shindangunBunruiTensuhyoRepository =
            ShindangunBunruiTensuhyoRepository(shindangunBunruiTensuhyoDao)

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
                repository.populateDatabaseFromCsvIfEmpty(getApplication())
                shujutsuRepository.populateDatabaseFromCsvIfEmpty(getApplication())
                shochi1Repository.populateDatabaseFromCsvIfEmpty(getApplication())
                shochi2Repository.populateDatabaseFromCsvIfEmpty(getApplication())
                fukushobyoRepository.populateDatabaseFromExcelIfEmpty(getApplication())
                jushodoJcsRepository.populateDatabaseFromExcelIfEmpty(getApplication())
                jushodoStrokeRepository.populateDatabaseFromExcelIfEmpty(getApplication())
                shindangunBunruiTensuhyoRepository.populateDatabaseFromCsvIfEmpty(getApplication())
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
            // --- 修正箇所: スペースでクエリを分割する ---
            val words = query.trim().split(Regex("\\s+"))

            // DAOの設計（最大3単語など）に合わせて単語を抽出
            val word1 = "%${words.getOrElse(0) { "" }}%"
            val word2 = if (words.size > 1) "%${words[1]}%" else "%%"
            val word3 = if (words.size > 2) "%${words[2]}%" else "%%"
            val word4 = if (words.size > 3) "%${words[3]}%" else "%%"

            Log.d("tamaiDpc", "word")

            // リポジトリの検索メソッドを呼び出す
            // (リポジトリ/DAO側もこれに合わせて3引数を受け取れるようにしておく必要があります)
            repository.searchIcdMulti(word1, word2, word3, word4)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // UIがアクティブな間だけ監視
            initialValue = emptyList() // 初期値は空のリスト
        )

    /**
     * ドロップダウンで病態名が選択されたときに呼び出されるメソッド
     * @param byotaiName 選択された病態名
     * @return 選択された病態名に対応する病態コード
     */

    suspend fun searchIcdByMcdAndBunrui(mdcCode: String, bunruiCode: String): String{
        return repository.searchIcdByMcdAndBunrui(mdcCode, bunruiCode)
    }
    suspend fun getBunruiNames(mdcCode: String, bunruiCode: String): String{
        return repository.getBunruiName(mdcCode, bunruiCode)
    }
    suspend fun getByotaiCode(byotaiName: String): String? {
        return repository.getByotaiCodeByName(byotaiName)
    }

    suspend fun getShujutsu1Code(shujutsu1Name: String): String? {
        return shujutsuRepository.getShujutsu1CodeByName(shujutsu1Name)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Applicationのインスタンスを取得
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // DpcScreenViewModelのインスタンスを生成して返す
                return DpcScreenViewModel(application) as T
            }
        }
    }

    private suspend fun createJushoJcsJokenOptionsList(
        mdcCode: String, bunruiCode: String
    ): List<LabelStringAndScore> {
        // リポジトリからjoken1〜5のすべての値を取得
        val jushoJcsJoken = jushodoJcsRepository.getJushodoJoken(mdcCode, bunruiCode)
        Log.d("tamaiDpc", "val jushoJcsJoken done $jushoJcsJoken $mdcCode $bunruiCode")

        // 2. オブジェクトがnullなら、空のリストを返して処理を終了（クラッシュ回避）
        if (jushoJcsJoken.isEmpty()) {
            return emptyList()
        }
        val data = jushoJcsJoken.first()
        return buildList {
            // joken1 用の処理
            processJoken(
                data.jokenName,
                data.joken1Ijo,
                data.joken1Miman,
                data.joken1Value
            )?.let { add(it) }

            // joken2 用の処理
            processJoken(
                data.jokenName,
                data.joken2Ijo,
                data.joken2Miman,
                data.joken2Value
            )?.let { add(it) }

            // 必要に応じて joken3〜5 も同様に追加
        }
    }

    /**
     * jokenNameが「年齢」かそれ以外かを判定し、LabelStringAndScoreを生成するヘルパー
     */
    private fun processJoken(
        jokenName: String,
        ijo: String?,
        miman: String?,
        value: String?
    ): LabelStringAndScore? {
        if (value.isNullOrBlank()) return null

        val score = value.toIntOrNull() ?: 0

        val labelText = if (jokenName == "年齢") {
            // 年齢の場合はカプセル化した関数を使用
            formatNenreiLabel(ijo, miman)
        } else if (jokenName == "ＪＣＳ") {
            formatJcsLabel(ijo, miman)
        } else {
            // それ以外（JCSなど）の場合は単純な結合
            if (!ijo.isNullOrBlank() && !miman.isNullOrBlank()) {
                "${ijo.toIntOrNull()}以上${miman.toIntOrNull()}未満"
            } else {
                null
            }
        }

        return labelText?.let {
            LabelStringAndScore(it, score, jokenName)
        }
    }

    private suspend fun createJushoShujutsuJokenOptionsList(
        mdcCode: String, bunruiCode: String
    ): List<LabelStringAndScore> {
        // リポジトリからjoken1〜5のすべての値を取得
        val jushoShujutsuJoken = jushodoShujutsuRepository.getJushodoJoken(mdcCode, bunruiCode)
        Log.d("tamaiDpc", "val jushoShujutsuJoken done $jushoShujutsuJoken $mdcCode $bunruiCode")

        // 2. オブジェクトがnullなら、空のリストを返して処理を終了（クラッシュ回避）
        if (jushoShujutsuJoken.isEmpty()) {
            return emptyList()
        }
        val data = jushoShujutsuJoken.first()
        Log.d("tamaiDpc", "val jushoShujutsu string done ")

        // nullでない有効な選択肢だけをリストに追加する
        return buildList {
            // joken1: 文字列がnullでなく、かつValueがnullまたは空でないことを確認
            if (data.joken1Name != null && data.joken1Code?.isNotBlank() == true) {
                add(
                    LabelStringAndScore(
                        jushoShujutsuJoken.first().joken1Name,
                        jushoShujutsuJoken.first().joken1Code.toInt(),
                        label = jushoShujutsuJoken.first().jokenName
                    ),
                )
            }
            Log.d("tamaiDpc", "here?1")
            // joken2: 文字列がnullでなく、かつValueがnullまたは空でないことを確認
            if (jushoShujutsuJoken.first().joken2Name != null && jushoShujutsuJoken.first().joken2Code.isNotBlank() == true) {
                add(
                    LabelStringAndScore(
                        jushoShujutsuJoken.first().joken2Name,
                        jushoShujutsuJoken.first().joken2Code.toInt(),
                        label = jushoShujutsuJoken.first().jokenName
                    ),
                )
            }
        }
    }

    private suspend fun createJushoStrokeJokenOptionsList(
        mdcCode: String, bunruiCode: String
    ): List<LabelStringAndScore> {
        val jushoStrokeJoken = jushodoStrokeRepository.getJushodoJoken(mdcCode, bunruiCode)
        Log.d("tamaiDpc", "val jushoStrokeJoken done $jushoStrokeJoken $mdcCode $bunruiCode")

        // 2. オブジェクトがnullなら、空のリストを返して処理を終了（クラッシュ回避）
        if (jushoStrokeJoken == null) {
            return emptyList()
        }

        Log.d("tamaiDpc", "val jushoShujutsu string done ")

        // nullでない有効な選択肢だけをリストに追加する
        return jushoStrokeJoken.mapNotNull { item ->
            // 名称とコードが両方存在する場合のみ、LabelStringAndScoreを作成
            if (item.joken1Name != null && !item.code.isNullOrBlank()) {
                LabelStringAndScore(
                    labelResId = item.joken1Name,
                    code = item.code.toIntOrNull() ?: 0,
                    label = item.label
                )
            } else {
                null // 条件に合わないデータは除外される
            }
        }
    }

    // DpcScreenViewModel.kt 内

    fun onDpcCodeInput(fullCode: String) {
        if (fullCode.length != 14) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. DPCコードを各パーツに分解
                // 01 2345 6 7 89 1011 12 13 (インデックス)
                // 06 0010 x x 01 00   0  x (例)
                val mdc = fullCode.substring(0, 2)
                val bunrui = fullCode.substring(2, 6)
                val byotai = fullCode.substring(6, 7)
                val nenrei = fullCode.substring(7, 8)
                val shujutsu = fullCode.substring(8, 10)
                val shochi1 = fullCode.substring(10, 11)
                val shochi2 = fullCode.substring(11, 12)
                val fukushobyo = fullCode.substring(12, 13)
                val jushodo = fullCode.substring(13, 14)

                // 2. Repositoryから該当する点数情報を取得
                val shindangun = shindangunBunruiTensuhyoRepository.getNames(fullCode)
                if (shindangun != null) {
                    _shindangunBunruiTensuhyoOptions.value = shindangun
                }

                // 3. UIの状態を更新するためのイベントを発火させるか、
                // Stateを直接更新する（DpcCodeはScreen側で持っているので、UIに分解結果を返す必要がある）
                // ※ DpcCodeをViewModelで管理するように変更するとよりスムーズです

            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * 年齢の「以上」「未満」の条件から表示用ラベルを生成する
 */
private fun formatNenreiLabel(ijo: String?, miman: String?): String? {
    val ijoVal = ijo?.toIntOrNull()
    val mimanVal = miman?.toIntOrNull()

    // どちらも無効なデータならnullを返す（または空文字）
    if (ijoVal == null || mimanVal == null) return null

    return buildString {
        if (ijoVal != 0) {
            append("${ijoVal}歳以上")
        }
        if (mimanVal != 999) {
            append("${mimanVal}歳未満")
        }
    }
}

private fun formatJcsLabel(ijo: String?, miman: String?): String? {
    val ijoVal = ijo?.toIntOrNull()
    val mimanVal = miman?.toIntOrNull()

    // どちらも無効なデータならnullを返す（または空文字）
    if (ijoVal == null || mimanVal == null) return null

    return buildString {
        append("${ijoVal}以上")
        if (mimanVal != 999) {
            append("${mimanVal}未満")
        }
    }
}


/*** 【共通化】選択UIの表示状態と選択肢リストを更新するヘルパー関数
 * @param T 選択肢の型 (String, JushodoJcsJoken など)
 *      * @param exists 該当データが存在するかどうかのBoolean
 *      * @param optionsFlow 更新する選択肢のMutableStateFlow
 *      * @param showSelectionFlow 更新する表示状態のMutableStateFlow
 *      * @param getOptions
 *  データを取得するためのsuspend関数
 *      */
private fun <T> CoroutineScope.updateSelectionState(
    exists: Boolean,
    optionsFlow: MutableStateFlow<List<T>>,
    showSelectionFlow: MutableStateFlow<Boolean>,
    getOptions: suspend () -> List<T>
) {
    launch {
        if (exists) {
            optionsFlow.value = getOptions()
            showSelectionFlow.value = true
        } else {
            optionsFlow.value = emptyList()
            showSelectionFlow.value = false
        }
    }
}














