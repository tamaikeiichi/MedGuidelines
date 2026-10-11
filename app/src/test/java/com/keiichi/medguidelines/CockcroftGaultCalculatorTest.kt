package com.keiichi.medguidelines

import com.keiichi.medguidelines.data.WeightBasis
import com.keiichi.medguidelines.data.adjustedBodyWeight
import com.keiichi.medguidelines.data.bodyMassIndex
import com.keiichi.medguidelines.data.calculateCockcroftGault
import com.keiichi.medguidelines.data.ckdEpi2021Gfr
import com.keiichi.medguidelines.data.cockcroftGaultCrCl
import com.keiichi.medguidelines.data.cystatinGfr
import com.keiichi.medguidelines.data.idealBodyWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Cockcroft DW, Gault MH. Nephron. 1976;16(1):31-41
class CockcroftGaultCalculatorTest {

    @Test
    fun crClForMale() {
        // (140 - 70) × 60 / (72 × 1.0)
        assertEquals(58.33, cockcroftGaultCrCl(70.0, 60.0, 1.0, false), 0.01)
    }

    @Test
    fun crClForFemaleIs85PercentOfMale() {
        val male = cockcroftGaultCrCl(70.0, 60.0, 1.0, false)
        val female = cockcroftGaultCrCl(70.0, 60.0, 1.0, true)
        assertEquals(male * 0.85, female, 0.0001)
    }

    @Test
    fun crClIsZeroWhenCreatinineIsNotEntered() {
        assertEquals(0.0, cockcroftGaultCrCl(70.0, 60.0, 0.0, false), 0.0)
    }

    @Test
    fun crClIsNotNegativeOverAge140() {
        assertEquals(0.0, cockcroftGaultCrCl(150.0, 60.0, 1.0, false), 0.0)
    }

    @Test
    fun devineIdealBodyWeight() {
        // 165 cm = 64.96 inch, male: 50 + 2.3 × 4.96
        assertEquals(61.41, idealBodyWeight(165.0, false), 0.01)
        assertEquals(56.91, idealBodyWeight(165.0, true), 0.01)
        // 60 inch = 152.4 cm で基準値
        assertEquals(50.0, idealBodyWeight(152.4, false), 0.01)
        assertEquals(45.5, idealBodyWeight(152.4, true), 0.01)
    }

    @Test
    fun adjustedBodyWeightIsIdealPlus40PercentOfExcess() {
        assertEquals(76.85, adjustedBodyWeight(100.0, idealBodyWeight(165.0, false)), 0.01)
    }

    @Test
    fun bmiNeedsHeight() {
        assertEquals(22.04, bodyMassIndex(165.0, 60.0)!!, 0.01)
        assertNull(bodyMassIndex(0.0, 60.0))
    }

    @Test
    fun actualBodyWeightOnlyWithoutHeight() {
        val result = calculateCockcroftGault(70.0, 60.0, 0.0, 1.0, 0.8, false)
        assertEquals(58.33, result.crClActual, 0.01)
        assertNull(result.bmi)
        assertNull(result.idealBodyWeight)
        assertNull(result.crClIdeal)
        assertEquals(WeightBasis.ACTUAL, result.recommendedBasis)
        assertEquals(result.crClActual, result.recommendedCrCl, 0.0)
    }

    @Test
    fun lowBmiUsesActualBodyWeight() {
        // 45 kg, 175 cm -> BMI 14.7
        val result = calculateCockcroftGault(70.0, 45.0, 175.0, 1.0, 0.8, false)
        assertEquals(WeightBasis.ACTUAL, result.recommendedBasis)
        assertEquals(45.0, result.recommendedWeight, 0.0)
        assertEquals(result.crClActual, result.recommendedCrCl, 0.0)
    }

    @Test
    fun normalBmiUsesIdealBodyWeight() {
        // 60 kg, 165 cm -> BMI 22.0
        val result = calculateCockcroftGault(70.0, 60.0, 165.0, 1.0, 0.8, false)
        assertEquals(WeightBasis.IDEAL, result.recommendedBasis)
        assertEquals(61.41, result.recommendedWeight, 0.01)
        assertEquals(59.71, result.recommendedCrCl, 0.01)
    }

    @Test
    fun highBmiUsesAdjustedBodyWeight() {
        // 100 kg, 165 cm -> BMI 36.7
        val result = calculateCockcroftGault(70.0, 100.0, 165.0, 1.0, 0.8, false)
        assertEquals(WeightBasis.ADJUSTED, result.recommendedBasis)
        assertEquals(76.85, result.recommendedWeight, 0.01)
        assertEquals(74.71, result.recommendedCrCl, 0.01)
    }

    // Inker LA, et al. N Engl J Med. 2021;385(19):1737-1749 (2021 CKD-EPI race-free creatinine)
    @Test
    fun ckdEpi2021GfrForMaleAboveKappa() {
        // Cr 1.0 > κ(0.9) → max項が効く
        assertEquals(80.97, ckdEpi2021Gfr(70.0, 1.0, false), 0.01)
    }

    @Test
    fun ckdEpi2021GfrForFemaleAboveKappa() {
        // Cr 1.0 > κ(0.7) → max項が効く
        assertEquals(60.61, ckdEpi2021Gfr(70.0, 1.0, true), 0.01)
    }

    @Test
    fun ckdEpi2021GfrForMaleBelowKappa() {
        // Cr 0.5 < κ(0.9) → min項が効く
        assertEquals(109.73, ckdEpi2021Gfr(70.0, 0.5, false), 0.01)
    }

    @Test
    fun ckdEpi2021GfrIsZeroWhenCreatinineIsNotEntered() {
        assertEquals(0.0, ckdEpi2021Gfr(70.0, 0.0, false), 0.0)
    }

    // Horio M, et al. Clin Exp Nephrol. 2013;17(3):352-358
    @Test
    fun cystatinGfrForMale() {
        assertEquals(90.61, cystatinGfr(70.0, 0.8, false), 0.01)
    }

    @Test
    fun cystatinGfrForFemale() {
        assertEquals(83.61, cystatinGfr(70.0, 0.8, true), 0.01)
    }

    @Test
    fun cystatinGfrIsZeroWhenNotEntered() {
        assertEquals(0.0, cystatinGfr(70.0, 0.0, false), 0.0)
    }
}
