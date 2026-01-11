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
import com.keiichi.medguidelines.data.BunruiEntity
import com.keiichi.medguidelines.data.DpcRepository
import com.keiichi.medguidelines.data.FukushobyoRepository
import com.keiichi.medguidelines.data.IcdEntity
import com.keiichi.medguidelines.data.JushodoJcsJoken
import com.keiichi.medguidelines.data.JushodoJcsRepository
import com.keiichi.medguidelines.data.JushodoShujutsuRepository
import com.keiichi.medguidelines.data.JushodoStrokeRepository
import com.keiichi.medguidelines.data.NenreiRepository
import com.keiichi.medguidelines.data.Shochi1Repository
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

// DpcScreenViewModel.kt のファイルレベルに追加

/**
 * DPCコードの各選択肢UIの状態とロジックを管理するためのデータクラス
 * @param T 選択肢の型 (例: String, JushodoJcsJoken)
 */
private data class SelectionState<T>(
    val showFlow: MutableStateFlow<Boolean>,
    val optionsFlow: MutableStateFlow<List<T>>,
    val checkExists: suspend (String) -> Boolean,
    val getOptions: suspend (String, String) -> List<T>
)

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
    val showJushodoShujutsuSelection: StateFlow<Boolean> = _showJushodoShujutsuSelection.asStateFlow()

    private val _showJushodoStrokeSelection = MutableStateFlow(false)
    val showJushodoStrokeSelection: StateFlow<Boolean> = _showJushodoStrokeSelection.asStateFlow()


    // 病態ドロップダウンの選択肢リスト
    private val _byotaiOptions = kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())
    val byotaiOptions: StateFlow<List<String>> = _byotaiOptions.asStateFlow()

    // 年齢ラジオボタン
    private val _nenreiOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val nenreiOptions: StateFlow<List<LabelStringAndScore>> = _nenreiOptions.asStateFlow()

    private val _shujutsuOptions =
        kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())
    val shujutsuOptions: StateFlow<List<String>> = _shujutsuOptions.asStateFlow()

    private val _shochi1Options = MutableStateFlow<List<String>>(emptyList())
    val shochi1Options: StateFlow<List<String>> = _shochi1Options.asStateFlow()

    private val _shochi2Options = MutableStateFlow<List<String>>(emptyList())
    val shochi2Options: StateFlow<List<String>> = _shochi2Options.asStateFlow()

    private val _fukushobyoOptions = MutableStateFlow<List<String>>(emptyList())
    val fukushobyoOptions: StateFlow<List<String>> = _fukushobyoOptions.asStateFlow()

    private val _jushodoJcsOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val jushodoJcsOptions: StateFlow<List<LabelStringAndScore>> = _jushodoJcsOptions.asStateFlow()
    private val _jushodoShujutsuOptions = MutableStateFlow<List<LabelStringAndScore>>(emptyList())
    val jushodoShujutsuOptions: StateFlow<List<LabelStringAndScore>> = _jushodoShujutsuOptions.asStateFlow()

    private val _jushodoStrokeOptions = MutableStateFlow<List<String>>(emptyList())
    val jushodoStrokeOptions: StateFlow<List<String>> = _jushodoStrokeOptions.asStateFlow()


    /**
     * 【修正案1】ICDリストの項目が選択されたときに呼び出されるメソッド
     * @param item 選択された IcdEntity
     */
    fun onIcdItemSelected(item: IcdEntity) {
        viewModelScope.launch {
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
            if (item.bunruiCode != null) {
                // nenrei_masterテーブルにbunruiCodeが存在するかチェック
                val nenreiDataExists = repository.checkBunruiExistsInNenrei(item.bunruiCode)
                val jushodoJcsDateExists =
                    jushodoJcsRepository.checkBunruiExistsInMaster(item.bunruiCode)
                val jushodoShujutsuDataExists =
                    jushodoShujutsuRepository.checkBunruiExistsInMaster(item.mdcCode,item.bunruiCode)
                val jushodoStrokeDataExists =
                    jushodoStrokeRepository.checkBunruiExistsInMaster(item.mdcCode,item.bunruiCode)
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
                        createJushodoStrokeJokenOptionsList(item.mdcCode, item.bunruiCode)
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
                val shujutsuExists = shujutsuRepository.checkBunruiExistsInShujutsu(item.bunruiCode)
                val shochi1Exists = shochi1Repository.checkBunruiExistsInShochi1(item.bunruiCode)
                val shochi2Exists = shochi2Repository.checkBunruiExistsInMaster(item.bunruiCode)
                val fukushobyoExists =
                    fukushobyoRepository.checkBunruiExistsInMaster(item.bunruiCode)
                val jushodoJcsExists =
                    jushodoJcsRepository.checkBunruiExistsInMaster(item.bunruiCode)
                val jushodoStrokeExists = jushodoStrokeRepository.checkBunruiExistsInMaster(item.mdcCode,item.bunruiCode)

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
                    getOptions = { jushodoStrokeRepository.getNames(item.mdcCode, item.bunruiCode) }
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
            //joken1
            if (!nenreiJoken.joken1Ijo.isNullOrBlank() && !nenreiJoken.joken1Miman.isNullOrBlank() && !nenreiJoken.joken1Value.isNullOrBlank()) {
                val labelResId =
                    "${nenreiJoken.joken1Ijo.toInt()}歳以上${nenreiJoken.joken1Miman.toInt()}歳未満"
                val score = nenreiJoken.joken1Value.toInt()
                val label = nenreiJoken.jokenName
                add(LabelStringAndScore(labelResId, score, label))
            }
            // joken2
            if (!nenreiJoken.joken2Ijo.isNullOrBlank() && !nenreiJoken.joken2Miman.isNullOrBlank() && !nenreiJoken.joken2Value.isNullOrBlank()) {
                val labelResId =
                    "${nenreiJoken.joken2Ijo.toInt()}歳以上${nenreiJoken.joken2Miman.toInt()}歳未満"
                val score = nenreiJoken.joken2Value.toInt()
                val label = nenreiJoken.jokenName
                add(LabelStringAndScore(labelResId, score, label))
            }
            // joken3
            if (!nenreiJoken.joken3Ijo.isNullOrBlank() && !nenreiJoken.joken3Miman.isNullOrBlank() && !nenreiJoken.joken3Value.isNullOrBlank()) {
                val labelResId =
                    "${nenreiJoken.joken3Ijo.toInt()}歳以上${nenreiJoken.joken3Miman.toInt()}歳未満"
                val score = nenreiJoken.joken3Value.toInt()
                val label = nenreiJoken.jokenName
                add(LabelStringAndScore(labelResId, score, label))
            }
            // joken4
            if (!nenreiJoken.joken4Ijo.isNullOrBlank() && !nenreiJoken.joken4Miman.isNullOrBlank() && !nenreiJoken.joken4Value.isNullOrBlank()) {
                val labelResId =
                    "${nenreiJoken.joken4Ijo.toInt()}歳以上${nenreiJoken.joken4Miman.toInt()}歳未満"
                val score = nenreiJoken.joken4Value.toInt()
                val label = nenreiJoken.jokenName
                add(LabelStringAndScore(labelResId, score, label))
            }
            // joken5
            if (!nenreiJoken.joken5Ijo.isNullOrBlank() && !nenreiJoken.joken5Miman.isNullOrBlank() && !nenreiJoken.joken5Value.isNullOrBlank()) {
                val labelResId =
                    "${nenreiJoken.joken5Ijo.toInt()}歳以上${nenreiJoken.joken5Miman.toInt()}歳未満"
                val score = nenreiJoken.joken5Value.toInt()
                val label = nenreiJoken.jokenName
                add(LabelStringAndScore(labelResId, score, label))
            }
        }


        // リポジトリからjoken1〜5のすべての値を取得


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
            // ★★★★★ ここからが修正箇所 ★★★★★
            // 1. nenrei_master に bunruiCode が存在するかチェック
            val nenreiDataExists = repository.checkBunruiExistsInNenrei(bunruiCode)

            // 2. 存在する場合 (true) のみ、年齢条件のUIを表示するロジックを実行
            if (nenreiDataExists) {
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
                    joken1Ijo.toInt().toString() + "歳以上" + joken1Miman.toInt()
                        .toString() + "歳未満"
                val joken2String: String? = if (joken2Ijo != null) {
                    joken2Ijo.toInt().toString() + "歳以上" + joken2Miman.toInt()
                        .toString() + "歳未満"
                } else {
                    null
                }
                val joken3String: String? = if (joken3Ijo != null) {
                    joken3Ijo.toInt().toString() + "歳以上" + joken3Miman.toInt()
                        .toString() + "歳未満"
                } else {
                    null
                }
                val joken4String: String? = if (joken4Ijo != null) {
                    joken4Ijo.toInt().toString() + "歳以上" + joken4Miman.toInt()
                        .toString() + "歳未満"
                } else {
                    null
                }
                val joken5String: String? = if (joken5Ijo != null) {
                    joken5Ijo.toInt().toString() + "歳以上" + joken5Miman.toInt()
                        .toString() + "歳未満"
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
                // 年齢条件UIを表示するためのロジック (以前の会話で作成したもの)
                // (リポジトリからjoken1Ijoなどを取得し、_nenreiOptionsを更新する処理)
                _showNenreiSelection.value = true // ★ true にする
            } else {
                // 存在しない場合は、UIを非表示にする
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


        repository = DpcRepository(dpcDao)
        shujutsuRepository = ShujutsuRepository(shujutsuDao) // shujutsuRepositoryを初期化
        shochi1Repository = Shochi1Repository(shochi1Dao)
        shochi2Repository = Shochi2Repository(shochi2Dao)
        fukushobyoRepository = FukushobyoRepository(fukushobyoDao)
        jushodoJcsRepository = JushodoJcsRepository(jushodoJcsDao)
        nenreiRepository = NenreiRepository(nenreiDao)
        jushodoShujutsuRepository = JushodoShujutsuRepository(jushodoShujutsuDao)
        jushodoStrokeRepository = JushodoStrokeRepository(jushodoStrokeDao)
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
                shujutsuRepository.populateDatabaseFromExcelIfEmpty(getApplication())
                shochi1Repository.populateDatabaseFromExcelIfEmpty(getApplication())
                shochi2Repository.populateDatabaseFromExcelIfEmpty(getApplication())
                fukushobyoRepository.populateDatabaseFromExcelIfEmpty(getApplication())
                jushodoJcsRepository.populateDatabaseFromExcelIfEmpty(getApplication())
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

    suspend fun getShujutsu1Code(shujutsu1Name: String): String? {
        return shujutsuRepository.getShujutsu1CodeByName(shujutsu1Name)
    }

    suspend fun getShochi1Code(shochi1Name: String): String? {
        return shochi1Repository.getCodeByName(shochi1Name)
    }

    suspend fun getShochi2Code(name: String): String? {
        return shochi2Repository.getCodeByName(name)
    }

    suspend fun getFukushobyoCode(name: String): String? {
        return fukushobyoRepository.getCodeByName(name)
    }

    suspend fun getJushodoJcsJoken(mdcCode: String, bunruiCode: String): List<JushodoJcsJoken> {
        return jushodoJcsRepository.getJushodoJoken(mdcCode, bunruiCode)
    }

    suspend fun getJushodoStrokeCode(name: String): String? {
        return jushodoStrokeRepository.getCodeByName(name)
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
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
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

    private suspend fun createJushoJcsJokenOptionsList(
        mdcCode: String, bunruiCode: String
    ): List<LabelStringAndScore> {
        // リポジトリからjoken1〜5のすべての値を取得
        val jushoJcsJoken = jushodoJcsRepository.getJushodoJoken(mdcCode, bunruiCode)
        Log.d("tamaiDpc", "val jushoJcsJoken done $jushoJcsJoken $mdcCode $bunruiCode")

        // 2. オブジェクトがnullなら、空のリストを返して処理を終了（クラッシュ回避）
        if (jushoJcsJoken == null) {
            return emptyList()
        }

        val joken1String: String? =
            if (jushoJcsJoken.isNotEmpty()) {
                if (
                    !jushoJcsJoken.first().joken1Ijo.isNullOrBlank()
                    && !jushoJcsJoken.first().joken1Miman.isNullOrBlank()
                ) {
                    "${jushoJcsJoken.first().joken1Ijo?.toInt()}以上${jushoJcsJoken.first().joken1Miman?.toInt()}未満"

                } else {
                    null
                }
            } else {
                return emptyList()
            }

        val joken2String: String? =
            if (jushoJcsJoken.isNotEmpty()) {
                if (
                    !jushoJcsJoken.first().joken2Ijo.isNullOrBlank()
                    && !jushoJcsJoken.first().joken2Miman.isNullOrBlank()
                ) {
                    "${jushoJcsJoken.first().joken2Ijo?.toInt()}以上${jushoJcsJoken.first().joken2Miman?.toInt()}未満"
                } else {
                    null
                }
            } else {
                return emptyList()
            }

        Log.d("tamaiDpc", "val jushoJcs string done $joken1String $joken2String")

        // nullでない有効な選択肢だけをリストに追加する
        return buildList {
            // joken1: 文字列がnullでなく、かつValueがnullまたは空でないことを確認
            if (joken1String != null && jushoJcsJoken.first().joken1Value?.isNotBlank() == true) {
                add(
                    LabelStringAndScore(
                        joken1String,
                        jushoJcsJoken.first().joken1Value?.toInt() ?: 0,
                        label = jushoJcsJoken.first().jokenName
                    ),
                )
            }
            Log.d("tamaiDpc", "here?1")
            // joken2: 文字列がnullでなく、かつValueがnullまたは空でないことを確認
            if (joken2String != null && jushoJcsJoken.first().joken2Value.isNotBlank() == true) {
                add(
                    LabelStringAndScore(
                        joken2String,
                        jushoJcsJoken.first().joken2Value?.toInt() ?: 0,
                        label = jushoJcsJoken.first().jokenName
                    ),
                )
            }
        }
    }

    private suspend fun createJushoShujutsuJokenOptionsList(
        mdcCode: String, bunruiCode: String
    ): List<LabelStringAndScore> {
        // リポジトリからjoken1〜5のすべての値を取得
        val jushoShujutsuJoken = jushodoShujutsuRepository.getJushodoJoken(mdcCode, bunruiCode)
        Log.d("tamaiDpc", "val jushoShujutsuJoken done $jushoShujutsuJoken $mdcCode $bunruiCode")

        // 2. オブジェクトがnullなら、空のリストを返して処理を終了（クラッシュ回避）
        if (jushoShujutsuJoken == null) {
            return emptyList()
        }

        Log.d("tamaiDpc", "val jushoShujutsu string done ")

        // nullでない有効な選択肢だけをリストに追加する
        return buildList {
            // joken1: 文字列がnullでなく、かつValueがnullまたは空でないことを確認
            if (jushoShujutsuJoken.first().joken1Name != null && jushoShujutsuJoken.first().joken1Code?.isNotBlank() == true) {
                add(
                    LabelStringAndScore(
                        jushoShujutsuJoken.first().joken1Name,
                        jushoShujutsuJoken.first().joken1Code?.toInt() ?: 0,
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
                        jushoShujutsuJoken.first().joken2Code?.toInt() ?: 0,
                        label = jushoShujutsuJoken.first().jokenName
                    ),
                )
            }
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














