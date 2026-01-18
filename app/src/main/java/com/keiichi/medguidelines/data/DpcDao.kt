package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DpcDao {
    // --- ICDマスターテーブル (icd_master) に対する操作 ---

    /**
     * ICDマスターテーブルに複数のデータを一括で挿入します。
     * 既にあるデータは上書きします。
     * @param icdList 挿入したいIcdEntityのリスト
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIcd(icdList: List<IcdEntity>)

    /**
     * ICDマスターテーブルの全件数を取得します。
     * データが投入済みかどうかのチェックに使用します。
     * @return テーブルの行数
     */
    @Query("SELECT COUNT(*) FROM icd_master")
    suspend fun getIcdCount(): Int

    /**
     * 名称(icdNumber)またはICDコード(icdCode)で部分一致検索を行います。
     * 検索結果はFlowでラップされているため、データ変更が自動的にUIに通知されます。
     * @param query 検索文字列 (例: "%検索語%")
     * @return 条件に一致したIcdEntityのリストをFlowで返す
     */
    @Query("SELECT * FROM icd_master " +
            "WHERE normalized_icd_name LIKE :query " +
            "OR icdCode LIKE :query " +
            "OR  bunruiCode LIKE :query")
    fun searchIcd(query: String): Flow<List<IcdEntity>>

    // C:/Users/tamai/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/DpcDao.kt
    /**
    * スペース区切りのAND検索に対応させるため、複数の検索ワードを受け取れるようにします。
    * 3つまでの単語に対応する例です。
    */
    @Query("""
        SELECT * FROM icd_master 
        WHERE (normalized_icd_name LIKE :word1 OR mdcCode LIKE :word1 OR bunruiCode LIKE :word1 OR icdCode LIKE :word1)
        AND (normalized_icd_name LIKE :word2 OR mdcCode LIKE :word2 OR bunruiCode LIKE :word2 OR icdCode LIKE :word2)
        AND (normalized_icd_name LIKE :word3 OR mdcCode LIKE :word3 OR bunruiCode LIKE :word3 OR icdCode LIKE :word3)
        AND (normalized_icd_name LIKE :word4 OR mdcCode LIKE :word4 OR bunruiCode LIKE :word4 OR icdCode LIKE :word4)
    """)
    fun searchIcdMulti(
        word1: String,
        word2: String = "%%",
        word3: String = "%%",
        word4: String = "%%"
    ): Flow<List<IcdEntity>>

    // --- 病態マスターテーブル (byotai_master) に対する操作 ---
    /**
     * 病態マスターテーブルに複数のデータを一括で挿入します。
     */
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAllByotai(byotaiList: List<ByotaiEntity>)

    /**
     * 病態マスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM byotai_master")
    suspend fun getByotaiCount(): Int

    /**
     * 指定されたMDCコードに一致する、ユニークなMDCコードのリストを取得します。
     * （MDCコードが含まれているかどうかのチェック用）
     */
    @androidx.room.Query("SELECT DISTINCT mdc_code FROM byotai_master WHERE mdc_code = :mdcCode")
    suspend fun getUniqueMdc(mdcCode: String): List<String>

    /**
     * 指定された分類コードに一致する、ユニークな分類コードのリストを取得します。
     * （分類コードが含まれているかどうかのチェック用）
     */
    @androidx.room.Query("SELECT DISTINCT bunrui_code FROM byotai_master WHERE bunrui_code = :bunruiCode")
    suspend fun getUniqueBunrui(bunruiCode: String): List<String>

    /**
     * 指定されたMDCコードと分類コードに一致する病態名（8列目）のリストを取得します。
     * (ドロップダウンの選択肢用)
     */
    @androidx.room.Query("SELECT byotai_name FROM byotai_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getByotaiNames(mdcCode: String, bunruiCode: String): List<String>



    @androidx.room.Query("SELECT joken1_ijo " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken1Ijo(mdcCode: String, bunruiCode: String): String
    @androidx.room.Query("SELECT joken1_miman " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken1Miman(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken1_value " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken1Value(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken2_ijo " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken2Ijo(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken2_miman " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken2Miman(mdcCode: String, bunruiCode: String): String

    @Query("SELECT joken2_value " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken2Value(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken3_ijo " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken3Ijo(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken3_miman " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken3Miman(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken3_value " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken3Value(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken4_ijo " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode " )
    suspend fun getNenreiJoken4Ijo(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken4_miman " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken4Miman(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken4_value " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken4Value(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken5_ijo " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken5Ijo(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken5_miman " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken5Miman(mdcCode: String, bunruiCode: String): String
    @Query("SELECT joken5_value " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNenreiJoken5Value(mdcCode: String, bunruiCode: String): String

    /**
     * 指定された病態名に一致する行の病態コード（3列目）を取得します。
     */
    @androidx.room.Query("SELECT byotai_code FROM byotai_master WHERE byotai_name = :byotaiName LIMIT 1")
    suspend fun getByotaiCodeByName(byotaiName: String): String?

// --- ここからBunruiEntity用のメソッドを追加 ---

    /**
     * 分類マスターテーブルに複数のデータを一括で挿入します。
     */
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAllBunrui(bunruiList: List<BunruiEntity>)

    /**
     * 分類マスターテーブルの件数を取得します。
     * (データが投入済みかどうかのチェックに使用します)
     */
    @androidx.room.Query("SELECT COUNT(*) FROM bunrui_master")
    suspend fun getBunruiCount(): Int

    /**
     * 分類名称(bunrui_name)または分類コード(bunrui_code)で部分一致検索を行います。
     * @param query 検索文字列
     * @return 条件に一致したBunruiEntityのリストをFlowで返す
     */
    @androidx.room.Query("SELECT * FROM bunrui_master WHERE bunrui_name LIKE :query OR bunrui_code LIKE :query")
    fun searchBunrui(query: String): Flow<List<BunruiEntity>>

    /**
     * MDCマスターテーブルに複数のデータを一括で挿入します。
     */
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAllMdc(mdcList: List<MdcEntity>)

    /**
     * MDCマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM mdc_master")
    suspend fun getMdcCount(): Int

    /**
     * Nenreiマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM nenrei_master")
    suspend fun getNenreiCount(): Int
    // --- 必要に応じて、他の11個のテーブルに対する操作もここに追加していきます ---

    /**
     * Nenreiマスターテーブルに複数のデータを一括で挿入します。
     */
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAllNenrei(nenreiList: List<NenreiEntity>)

    /**
     * 指定されたMDCコードに一致する、ユニークなMDCコードのリストを取得します。
     * （MDCコードが含まれているかどうかのチェック用）
     */
    @androidx.room.Query("SELECT DISTINCT mdc_code " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode")
    suspend fun getUniqueMdcFromNenrei(mdcCode: String): List<String>

    /**
     * 指定された分類コードに一致する、ユニークな分類コードのリストを取得します。
     * （分類コードが含まれているかどうかのチェック用）
     */
    @androidx.room.Query("SELECT DISTINCT bunrui_code " +
            "FROM nenrei_master " +
            "WHERE bunrui_code = :bunruiCode")
    suspend fun getUniqueBunruiFromNenrei(bunruiCode: String): List<String>

    /**
     * 指定されたMDCコードと分類コードに一致する脳卒中発症時　区分名称
     */
    @androidx.room.Query("SELECT DISTINCT nenrei_stroke_name " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getStrokeNames(mdcCode: String, bunruiCode: String): List<String>

    /**
     * nenrei_masterテーブルに、指定されたbunruiCodeを持つレコードが存在するかどうかをチェックする
     * @param bunruiCode チェックする分類コード
     * @return 存在すれば true, しなければ false
     */
    @Query("SELECT EXISTS(SELECT 1 FROM nenrei_master WHERE bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInNenreiMaster(bunruiCode: String): Boolean




}