package com.keiichi.medguidelines.data

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
 * height <= 0（未入力）のときは実体重のみで計算する。
 * 身長があればBMIで推奨体重を選ぶ: BMI < 18.5は実体重，18.5以上25未満は理想体重，
 * 25以上は補正体重（MDCalc）。
 */
fun calculateCockcroftGault(
    age: Double,
    weight: Double,
    height: Double,
    creatinine: Double,
    isFemale: Boolean
): CockcroftGaultResult {
    val crClActual = cockcroftGaultCrCl(age, weight, creatinine, isFemale)
    val bmi = bodyMassIndex(height, weight)

    if (bmi == null) {
        return CockcroftGaultResult(
            actualBodyWeight = weight,
            crClActual = crClActual,
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
