package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import com.keiichi.medguidelines.ui.screen.LabelStringAndScore

data class JushodoStrokeJoken(
    // データベースの列名と一致するように@ColumnInfoを使うのが確実
    @ColumnInfo(name = "mdc_code") val mdcCode: String,
    @ColumnInfo(name = "bunrui_code") val bunruiCode: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "joken1_name") val joken1Name: String,
)

@Dao
interface JushodoStrokeDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @Query("SELECT COUNT(*) FROM jushodo_stroke_master")
    suspend fun getCount(): Int

    @Query("SELECT joken1_name FROM jushodo_stroke_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<JushodoStrokeEntity>)

    /**
     * 指定された MDCコード と 分類コード の両方が一致する行が
     * マスターテーブルに存在するかどうかをチェックします。
     */
    @Query("SELECT EXISTS(SELECT 1 FROM jushodo_stroke_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInMaster(mdcCode: String?, bunruiCode: String): Boolean


    /**
     * 指定されたMDCコードと分類コードに一致する行から、
     * joken1とjoken2に関連するすべての値を一度に取得します。
     *      * @return JushodoJokenオブジェクト。見つからなければnull。
     *      */
    @Query(
        "SELECT mdc_code, bunrui_code, code,label, joken1_name " +
                "FROM jushodo_stroke_master " +
                "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode "
    )
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoStrokeJoken>

    @androidx.room.Query("SELECT code FROM jushodo_stroke_master WHERE joken1_name = :name LIMIT 1")
    suspend fun getCodeByName(name: String): String?
}


