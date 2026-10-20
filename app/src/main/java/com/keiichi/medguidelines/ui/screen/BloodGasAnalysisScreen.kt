package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.InputValueEachDigitNPNN
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.NumberInTextField
import com.keiichi.medguidelines.ui.component.TextInCard
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.parseStyledString
import com.keiichi.medguidelines.ui.component.TextAndUrl

// 判定は Berend K, et al. N Engl J Med. 2014;371(15):1434-1445 の Table 1, Figure 1, Figure 2 に準拠
// 基準値: PaCO2 40 mmHg, HCO3 24 mmol/L
private const val NORMAL_HCO3 = 24.0
private const val NORMAL_PACO2 = 40.0
// 予測PaCO2（Winters式など）の許容幅 ±2 mmHg
private const val EXPECTED_PACO2_TOLERANCE = 2.0

@Composable
fun BloodGasAnalysisScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.bloodGasAnalysisTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.bloodGasAnalysisRef, R.string.bloodGasAnalysisUrl)
                )
            )
        },
        modifier = Modifier
            .pointerInput(Unit) { // Use pointerInput with detectTapGestures
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus() // Clear focus on tap outside
                    }
                )
            },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                BloodGasAnalysisInput()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodGasAnalysisInput() {
    val ph = remember { mutableDoubleStateOf(7.41) }
    val paO2 = remember { mutableDoubleStateOf(100.0) }
    val paCo2 = remember { mutableDoubleStateOf(40.0) }
    val hco3 = remember { mutableDoubleStateOf(24.0) }
    val na = remember { mutableDoubleStateOf(145.0) }
    val k = remember { mutableDoubleStateOf(4.5) }
    val cl = remember { mutableDoubleStateOf(98.0) }
    val albumin = remember { mutableDoubleStateOf(3.9) }

    BloodGasAnalysisCard(
        bloodGasAnalysisContent = {
            MedGuidelinesCard {
                FlowRow(
                    modifier = Modifier
                        .padding(4.dp),
                    itemVerticalAlignment = Alignment.Bottom
                ) {
                    InputValueEachDigitNPNN(
                        label = R.string.ph, value = ph, unit = R.string.space
                    )
                    InputValue(
                        label = R.string.po2, value = paO2, japaneseUnit = R.string.mmhg
                    )
                    InputValue(
                        label = R.string.pco2, value = paCo2, japaneseUnit = R.string.mmhg
                    )
                    InputValue(
                        label = R.string.hco3, value = hco3, japaneseUnit = R.string.meql
                    )
                    InputValue(
                        label = R.string.na, value = na, japaneseUnit = R.string.meql
                    )
                    InputValue(
                        label = R.string.k, value = k, japaneseUnit = R.string.meql
                    )
                    InputValue(
                        label = R.string.cl, value = cl, japaneseUnit = R.string.meql
                    )
                    InputValue(
                        label = R.string.albumin, value = albumin, japaneseUnit = R.string.gdL
                    )
                }
            }
        }
    )
    if (ph.doubleValue < 7.38) {
        TextInCard(R.string.acidemia)
        // 代謝性アシドーシスは HCO3 <22 のみで判定する（PaCO2 >42 の合併は予測PaCO2で判定）
        if (hco3.doubleValue < 22) {
            TextInCard(R.string.metabolicAcidosis)
            // Winters式: 予測PaCO2 = 1.5 × HCO3 + 8 ± 2
            val expectedPaco2 = 1.5 * hco3.doubleValue + 8
            if (paCo2.doubleValue > expectedPaco2 + EXPECTED_PACO2_TOLERANCE) {
                TextInCard(R.string.additionalRespiratoryAcidosis)
            } else if (paCo2.doubleValue < expectedPaco2 - EXPECTED_PACO2_TOLERANCE) {
                TextInCard(R.string.additionalRespiratoryAlkalosis)
            }
            val anionGap = na.doubleValue - cl.doubleValue - hco3.doubleValue
            var correctedAnionGap = anionGap
            if (albumin.doubleValue < 3.9) {
                correctedAnionGap = anionGap + 2.5 * (3.9 - albumin.doubleValue)
            }
            if (correctedAnionGap in 8.0..12.0) {
                TextInCard(R.string.normalAnionGap)
                val urineNa = remember { mutableDoubleStateOf(20.0) }
                val urineK = remember { mutableDoubleStateOf(4.0) }
                val urineCl = remember { mutableDoubleStateOf(90.0) }
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        FlowRow(
                            modifier = Modifier
                                .padding(4.dp)
                        ) {
                            NumberInTextField(
                                label = R.string.urineNa, value = urineNa, width = 120,
                            )
                            NumberInTextField(
                                label = R.string.urineK, value = urineK, width = 120,
                            )
                            NumberInTextField(
                                label = R.string.urineCl, value = urineCl, width = 120,
                            )
                        }
                    }
                )
                val urineAnionGap = urineNa.doubleValue - urineCl.doubleValue + urineK.doubleValue
                if (urineAnionGap >= 0) {
                    TextInCard(R.string.rta)
                } else {
                    TextInCard(R.string.urinaryAnionGapNegativeDDx)
                }
            } else if (correctedAnionGap > 12) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        Column() {
                            BloodGasAnalysisText(R.string.highAnionGap)
                            BloodGasAnalysisText(R.string.highAnionGapDDx)
                        }
                    }
                )
            }
        } else if (paCo2.doubleValue > 42) {
            TextInCard(R.string.respiratoryAcidosis)
            // PaCO2が40から10 mmHg上昇するごとのHCO3の増加量
            val hco3Change =
                (hco3.doubleValue - NORMAL_HCO3) / ((paCo2.doubleValue - NORMAL_PACO2) / 10)
            if (hco3Change < 1.0) {
                TextInCard(R.string.additionalMetabolicAcidosis)
            } else if (hco3Change < 4.0) {
                TextInCard(R.string.acuteRespiratoryAcidosis)
            } else if (hco3Change <= 5.0) {
                TextInCard(R.string.chronicRespiratoryAcidosis)
            } else {
                TextInCard(R.string.additionalMetabolicAlkalosis)
            }
            AaDifferenceJudgement(paO2.doubleValue, paCo2.doubleValue)
        }
    } else if (ph.doubleValue > 7.42) {
        TextInCard(R.string.alkalemia)
        // 代謝性アルカローシスは HCO3 >26 のみで判定する（PaCO2 <38 の合併は予測PaCO2で判定）
        if (hco3.doubleValue > 26) {
            TextInCard(R.string.metabolicAlkalosis)
            // 予測PaCO2 = 0.7 × (HCO3 − 24) + 40 ± 2
            val expectedPaco2 = 0.7 * (hco3.doubleValue - NORMAL_HCO3) + NORMAL_PACO2
            if (paCo2.doubleValue < expectedPaco2 - EXPECTED_PACO2_TOLERANCE) {
                TextInCard(R.string.additionalRespiratoryAlkalosis)
            } else if (paCo2.doubleValue > expectedPaco2 + EXPECTED_PACO2_TOLERANCE) {
                TextInCard(R.string.additionalRespiratoryAcidosis)
            }
        } else if (paCo2.doubleValue < 38) {
            TextInCard(R.string.respiratoryAlkalosis)
            // PaCO2が40から10 mmHg低下するごとのHCO3の減少量
            val hco3Change =
                (NORMAL_HCO3 - hco3.doubleValue) / ((NORMAL_PACO2 - paCo2.doubleValue) / 10)
            if (hco3Change < 2.0) {
                TextInCard(R.string.additionalMetabolicAlkalosis)
            } else if (hco3Change < 4.0) {
                TextInCard(R.string.acuteRespiratoryAlkalosis)
            } else if (hco3Change <= 5.0) {
                TextInCard(R.string.chronicRespiratoryAlkalosis)
            } else {
                TextInCard(R.string.additionalMetabolicAcidosis)
            }
            AaDifferenceJudgement(paO2.doubleValue, paCo2.doubleValue)
        }
    } else {
        if (hco3.doubleValue > (NORMAL_HCO3 + 3) && paCo2.doubleValue > (NORMAL_PACO2 + 5)) {
            TextInCard(R.string.metabolicAlkalosisAndRespiratoryAcidosis)
        } else if (hco3.doubleValue < (NORMAL_HCO3 - 3) && paCo2.doubleValue < (NORMAL_PACO2 - 5)) {
            TextInCard(R.string.metabolicAcidosisAndRespiratoryAlkalosis)
        }
    }
}

/**
 * A-aDO2 = 150 − PaO2 − 1.25 × PaCO2（海抜0 m，室内気）
 * 正常は ≤10 mmHg（高齢者は ≤20 mmHg）
 */
@Composable
private fun AaDifferenceJudgement(paO2: Double, paCo2: Double) {
    val age = remember { mutableDoubleStateOf(65.0) }
    BloodGasAnalysisCard(
        bloodGasAnalysisContent = {
            FlowRow(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                NumberInTextField(
                    label = R.string.age, value = age, width = 120,
                )
            }
        }
    )
    val aaDifference = 150 - paO2 - 1.25 * paCo2
    val upperLimit = if (age.doubleValue >= 65) 20.0 else 10.0
    if (aaDifference <= upperLimit) {
        TextInCard(R.string.HyperventilationWithoutIntrinsicLungDisease)
    } else {
        TextInCard(R.string.HyperventilationWithIntrinsicLungDiseaseVentilationPerfusionMismatchOrBoth)
    }
}

@Composable
fun BloodGasAnalysisText(text: Int) {
    Text(
        text = parseStyledString(text),
        modifier = Modifier
            .padding(10.dp)
    )
}

@Composable
fun BloodGasAnalysisCard(bloodGasAnalysisContent: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        bloodGasAnalysisContent()
    }
}


@Preview
@Composable
fun BloodGasAnalysisScreenPreview() {
    BloodGasAnalysisScreen(navController = NavController(LocalContext.current))
}
