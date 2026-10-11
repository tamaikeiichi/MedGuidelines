package com.keiichi.medguidelines.data

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// Cockcroft DW, Gault MH. Nephron. 1976;16(1):31-41
// 体重の使い分けはMDCalc (https://www.mdcalc.com/calc/43/) に準拠

enum class WeightBasis {
    ACTUAL,
    IDEAL,
    ADJUSTED
}

data class CockcroftGaultResult(
    val actualBodyWeight: Double,
    val crClActual: Double,
    val gfr: Double,
    val gfrCys: Double,
    val bmi: Double?,
    val idealBodyWeight: Double?,
    val adjustedBodyWeight: Double?,
    val crClIdeal: Double?,
    val crClAdjusted: Double?,
    val recommendedBasis: WeightBasis,
    val recommendedWeight: Double,
    val recommendedCrCl: Double
)

/**
 * CrCl = (140 - age) × weight × (0.85 if female) / (72 × creatinine)
 * age: 年, weight: kg, creatinine: mg/dL
 */
fun cockcroftGaultCrCl(
    age: Double,
    weight: Double,
    creatinine: Double,
    isFemale: Boolean
): Double {
    if (creatinine <= 0.0 || weight <= 0.0) return 0.0
    val sexFactor = if (isFemale) 0.85 else 1.0
    return ((140 - age).coerceAtLeast(0.0) * weight * sexFactor) / (72 * creatinine)
}

/** Devine式の理想体重（kg）。height: cm */
fun idealBodyWeight(height: Double, isFemale: Boolean): Double {
    val inchesOver60 = height / 2.54 - 60
    val base = if (isFemale) 45.5 else 50.0
    return (base + 2.3 * inchesOver60).coerceAtLeast(0.0)
}

/** 補正体重（kg）= 理想体重 + 0.4 × (実体重 - 理想体重) */
fun adjustedBodyWeight(weight: Double, idealBodyWeight: Double): Double =
    idealBodyWeight + 0.4 * (weight - idealBodyWeight)

/** BMI (kg/m^2)。height: cm */
fun bodyMassIndex(height: Double, weight: Double): Double? {
    if (height <= 0.0) return null
    val heightMeter = height / 100.0
    return weight / (heightMeter * heightMeter)
}

/**
 * 2021 CKD-EPI race-free creatinine式による推算GFR (mL/min/1.73m^2)
 * GFR = 142 × min(Cr/κ, 1)^α × max(Cr/κ, 1)^-1.200 × 0.9938^age × (1.012 if female)
 * κ = 0.7 (female) or 0.9 (male), α = -0.241 (female) or -0.302 (male)
 * Inker LA, et al. N Engl J Med. 2021;385(19):1737-1749
 * creatinine: mg/dL
 */
fun ckdEpi2021Gfr(age: Double, creatinine: Double, isFemale: Boolean): Double {
    if (creatinine <= 0.0 || age <= 0.0) return 0.0
    val kappa = if (isFemale) 0.7 else 0.9
    val alpha = if (isFemale) -0.241 else -0.302
    val sexFactor = if (isFemale) 1.012 else 1.0
    val ratio = creatinine / kappa
    return 142.0 *
            min(ratio, 1.0).pow(alpha) *
            max(ratio, 1.0).pow(-1.200) *
            0.9938.pow(age) *
            sexFactor
}

/**
 * シスタチンCによる推算GFR (mL/min/1.73m^2)
 * 男性: (104 × CysC^-1.019 × 0.996^age) − 8
 * 女性: (104 × CysC^-1.019 × 0.996^age × 0.929) − 8
 * Horio M, et al. Clin Exp Nephrol. 2013;17(3):352-358
 * cystatinC: mg/L
 */
fun cystatinGfr(age: Double, cystatinC: Double, isFemale: Boolean): Double {
    if (cystatinC <= 0.0 || age <= 0.0) return 0.0
    val sexFactor = if (isFemale) 0.929 else 1.0
    return 104.0 * cystatinC.pow(-1.019) * 0.996.pow(age) * sexFactor - 8.0
}

/**
 * height <= 0（未入力）のときは実体重のみで計算する。
 * 身長があればBMIで推奨体重を選ぶ: BMI < 18.5は実体重，18.5以上25未満は理想体重，
 * 25以上は補正体重（MDCalc）。
 */
fun calculateCockcroftGault(
    age: Double,
    weight: Double,
    height: Double,
    creatinine: Double,
    cystatinC: Double,
    isFemale: Boolean
): CockcroftGaultResult {
    val crClActual = cockcroftGaultCrCl(age, weight, creatinine, isFemale)
    val gfr = ckdEpi2021Gfr(age, creatinine, isFemale)
    val gfrCys = cystatinGfr(age, cystatinC, isFemale)
    val bmi = bodyMassIndex(height, weight)

    if (bmi == null) {
        return CockcroftGaultResult(
            actualBodyWeight = weight,
            crClActual = crClActual,
            gfr = gfr,
            gfrCys = gfrCys,
            bmi = null,
            idealBodyWeight = null,
            adjustedBodyWeight = null,
            crClIdeal = null,
            crClAdjusted = null,
            recommendedBasis = WeightBasis.ACTUAL,
            recommendedWeight = weight,
            recommendedCrCl = crClActual
        )
    }

    val ibw = idealBodyWeight(height, isFemale)
    val adjBw = adjustedBodyWeight(weight, ibw)
    val crClIdeal = cockcroftGaultCrCl(age, ibw, creatinine, isFemale)
    val crClAdjusted = cockcroftGaultCrCl(age, adjBw, creatinine, isFemale)

    val basis = when {
        bmi < 18.5 -> WeightBasis.ACTUAL
        bmi < 25.0 -> WeightBasis.IDEAL
        else -> WeightBasis.ADJUSTED
    }
    val recommendedWeight = when (basis) {
        WeightBasis.ACTUAL -> weight
        WeightBasis.IDEAL -> ibw
        WeightBasis.ADJUSTED -> adjBw
    }
    val recommendedCrCl = when (basis) {
        WeightBasis.ACTUAL -> crClActual
        WeightBasis.IDEAL -> crClIdeal
        WeightBasis.ADJUSTED -> crClAdjusted
    }

    return CockcroftGaultResult(
        actualBodyWeight = weight,
        crClActual = crClActual,
        gfr = gfr,
        gfrCys = gfrCys,
        bmi = bmi,
        idealBodyWeight = ibw,
        adjustedBodyWeight = adjBw,
        crClIdeal = crClIdeal,
        crClAdjusted = crClAdjusted,
        recommendedBasis = basis,
        recommendedWeight = recommendedWeight,
        recommendedCrCl = recommendedCrCl
    )
}
