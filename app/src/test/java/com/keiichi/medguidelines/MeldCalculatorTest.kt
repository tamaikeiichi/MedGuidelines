package com.keiichi.medguidelines

import com.keiichi.medguidelines.data.MeldResult
import com.keiichi.medguidelines.data.calculateMeld
import com.keiichi.medguidelines.data.meld3Survival
import org.junit.Assert.assertEquals
import org.junit.Test

// Kim WR, et al. Gastroenterology. 2021 の例示表との照合
class MeldCalculatorTest {
    private data class Case(
        val female: Boolean, val bili: Double, val na: Double, val inr: Double,
        val cre: Double, val alb: Double,
        val expected: MeldResult, val mortality30: Double, val mortality90: Double
    )

    private val cases = listOf(
        Case(false, 2.5, 131.0, 1.0, 1.2, 3.8, MeldResult(12, 18, 16), 0.9, 2.6),
        Case(true, 2.5, 131.0, 1.0, 1.2, 3.8, MeldResult(12, 18, 17), 1.1, 3.1),
        Case(false, 6.0, 131.0, 1.5, 1.5, 3.5, MeldResult(22, 26, 25), 4.3, 12.1),
        Case(false, 6.0, 131.0, 1.5, 1.5, 2.2, MeldResult(22, 26, 26), 5.3, 14.5),
        Case(true, 6.0, 131.0, 1.5, 1.5, 2.2, MeldResult(22, 26, 27), 6.4, 17.5),
        Case(false, 12.0, 128.0, 2.2, 1.8, 2.0, MeldResult(30, 33, 34), 19.8, 47.1),
        Case(false, 12.0, 128.0, 2.2, 2.8, 2.0, MeldResult(35, 36, 38), 35.8, 72.3),
        Case(true, 12.0, 128.0, 2.2, 2.8, 2.0, MeldResult(35, 36, 39), 41.8, 79.1),
    )

    @Test
    fun scoresMatchPaperTable() {
        cases.forEach { c ->
            val result = calculateMeld(c.bili, c.cre, c.inr, c.na, c.alb, c.female, false)
            assertEquals(c.toString(), c.expected, result)
        }
    }

    // Stanford MELD 3.0 calculator (https://medcalculators.stanford.edu/meld) の計算式で
    // 生成した500例と照合（透析の入力はStanford側にないので透析なし）
    @Test
    fun scoresMatchStanfordCalculator() {
        val lines = javaClass.classLoader!!.getResourceAsStream("meld_stanford_cases.csv")
            .bufferedReader().readLines().drop(1).filter { it.isNotBlank() }
        lines.forEach { line ->
            val v = line.split(",")
            val result = calculateMeld(
                bilirubin = v[1].toDouble(),
                creatinine = v[2].toDouble(),
                inr = v[3].toDouble(),
                sodium = v[4].toDouble(),
                albumin = v[5].toDouble(),
                isFemale = v[0] == "1",
                dialysisTwiceInPastWeek = false
            )
            assertEquals(line, MeldResult(v[6].toInt(), v[7].toInt(), v[8].toInt()), result)
        }
    }

    @Test
    fun predictedMortalityMatchesPaperTable() {
        cases.forEach { c ->
            val m30 = (1 - meld3Survival(c.expected.meld3, 0.981)) * 100
            val m90 = (1 - meld3Survival(c.expected.meld3, 0.946)) * 100
            // 表は小数第1位表示なので，丸め誤差分（0.05%ポイント）のみ許容
            assertEquals(c.toString(), c.mortality30, m30, 0.05)
            assertEquals(c.toString(), c.mortality90, m90, 0.05)
        }
    }
}
