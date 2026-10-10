package com.keiichi.medguidelines.data

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

// Kim WR, et al. Gastroenterology. 2021;161(6):1887-1895
data class MeldResult(
    val meld: Int,
    val meldNa: Int,
    val meld3: Int
)

/**
 * bilirubin, creatinine: mg/dL, sodium: mmol/L, albumin: g/dL
 */
fun calculateMeld(
    bilirubin: Double,
    creatinine: Double,
    inr: Double,
    sodium: Double,
    albumin: Double,
    isFemale: Boolean,
    dialysisTwiceInPastWeek: Boolean
): MeldResult {
    // bilirubin, INR, creatinineは1.0未満なら1.0とする
    val bili = bilirubin.coerceAtLeast(1.0)
    val ptInr = inr.coerceAtLeast(1.0)
    val na = sodium.coerceIn(125.0, 137.0)

    // MELD: creatinineの上限は4.0（透析例は4.0）
    val creMeld = if (dialysisTwiceInPastWeek) 4.0 else creatinine.coerceIn(1.0, 4.0)
    val meld = (9.57 * ln(creMeld) + 3.78 * ln(bili) + 11.20 * ln(ptInr) + 6.43).roundToInt()

    // MELD-Na: MELD > 11の場合のみ適用（OPTN）
    val meldNa = if (meld > 11) {
        (meld + 1.32 * (137 - na) - 0.033 * meld * (137 - na)).roundToInt()
    } else {
        meld
    }

    // MELD 3.0: creatinineの上限は3.0（透析例は3.0），albuminは1.5〜3.5
    val creMeld3 = if (dialysisTwiceInPastWeek) 3.0 else creatinine.coerceIn(1.0, 3.0)
    val alb = albumin.coerceIn(1.5, 3.5)
    val meld3 = ((if (isFemale) 1.33 else 0.0) +
            4.56 * ln(bili) +
            0.82 * (137 - na) -
            0.24 * (137 - na) * ln(bili) +
            9.09 * ln(ptInr) +
            11.14 * ln(creMeld3) +
            1.85 * (3.5 - alb) -
            1.83 * (3.5 - alb) * ln(creMeld3) +
            6).roundToInt()

    return MeldResult(meld = meld, meldNa = meldNa, meld3 = meld3)
}

// MELD 3.0の生存関数のベースライン S0(t)（t: 日）
val meld3BaselineSurvival = listOf(
    15 to 0.991,
    30 to 0.981,
    45 to 0.971,
    60 to 0.963,
    75 to 0.955,
    90 to 0.946
)

/**
 * S(t) = S0(t)^exp[0.17698 × MELD 3.0 − 3.56]
 * 整数に丸めたMELD 3.0を用い，0.17698 × MELD 3.0 を小数第1位に丸める
 * （この方法で論文の予測死亡率の表と全例一致する）
 */
fun meld3Survival(meld3: Int, baselineSurvival: Double): Double {
    val linear = (0.17698 * meld3 * 10).roundToInt() / 10.0
    return baselineSurvival.pow(exp(linear - 3.56))
}
