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

    val normalHco3: Double = 23.0
    val normalPaCo2: Double = 40.0
    val normalPaO2: Double = 100.0

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
                        label = R.string.po2, value = paO2, unit = R.string.mmhg
                    )
                    InputValue(
                        label = R.string.pco2, value = paCo2, unit = R.string.mmhg
                    )
                    InputValue(
                        label = R.string.hco3, value = hco3, unit = R.string.meql
                    )
                    InputValue(
                        label = R.string.na, value = na, unit = R.string.meql
                    )
                    InputValue(
                        label = R.string.k, value = k, unit = R.string.meql
                    )
                    InputValue(
                        label = R.string.cl, value = cl, unit = R.string.meql
                    )
                    InputValue(
                        label = R.string.albumin, value = albumin, unit = R.string.mgdl
                    )
                }
            }
        }
    )
    if (ph.doubleValue < 7.38) {
        TextInCard(R.string.acidemia)
        if (hco3.doubleValue < 22 && paCo2.doubleValue < 42) {
            TextInCard(R.string.metabolicAcidosis)
            val calculateExpectedPaco2 = 1.5 * hco3.doubleValue + 8
            if (paO2.doubleValue > calculateExpectedPaco2) {
                TextInCard(R.string.additionalRespiratoryAcidosis)
            } else if (paO2.doubleValue < calculateExpectedPaco2) {
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
            } else if (correctedAnionGap >= 12) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        Column() {
                            BloodGasAnalysisText(R.string.highAnionGap)
                            BloodGasAnalysisText(R.string.highAnionGapDDx)
                        }
                    }
                )
            }
        } else if (paCo2.doubleValue > 42 && hco3.doubleValue > 22) {
            TextInCard(R.string.respiratoryAcidosis)
            val hco3change = (hco3.doubleValue - 23) / ((paCo2.doubleValue - 40) / 10)
            if (0.0 < hco3change && hco3change < 1.0) {
                TextInCard(R.string.additionalMetabolicAcidosis)
            } else if (1.0 <= hco3change && hco3change < 4.0) {
                TextInCard(R.string.acuteRespiratoryAcidosis)
            } else if (hco3change in 4.0..5.0) {
                TextInCard(R.string.chronicRespiratoryAcidosis)
            } else if (hco3change > 5.0) {
                TextInCard(R.string.additionalMetabolicAlkalosis)
            }
            val age = remember { mutableDoubleStateOf(65.0) }
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    NumberInTextField(
                        label = R.string.age, value = age, width = 120,
                    )
                }
            )
            val aaDifference = 150 - paO2.doubleValue - 1.25 * paCo2.doubleValue
            var correctedAaDifference = aaDifference
            if (age.doubleValue >= 65) {
                correctedAaDifference = aaDifference / 2
            }
            if (correctedAaDifference <= 10) {
                TextInCard(R.string.HypoventilationWithoutIntrinsicLungDisease)
            } else if (correctedAaDifference > 10) {
                TextInCard(R.string.HypoventilationWithIntrinsicLungDiseaseVentilationPerfusionMismatchOrBoth)
            }
        }
    } else if (ph.doubleValue > 7.42) {
        TextInCard(R.string.alkalemia)
        if (hco3.doubleValue > 26 && paCo2.doubleValue >= 38) {
            TextInCard(R.string.metabolicAlkalosis)
            val calculateExpectedPaco2 = 0.7 * (hco3.doubleValue - 24) + 40
            if (paCo2.doubleValue < calculateExpectedPaco2) {
                TextInCard(R.string.additionalRespiratoryAlkalosis)
            } else if (paCo2.doubleValue > calculateExpectedPaco2) {
                TextInCard(R.string.additionalRespiratoryAcidosis)
            }
        } else if (hco3.doubleValue < 26 && paCo2.doubleValue < 38) {
            TextInCard(R.string.respiratoryAlkalosis)
            val hco3Change =
                (hco3.doubleValue - normalHco3) / (paCo2.doubleValue - normalPaCo2) / 10
            if (0.0 < hco3Change && hco3Change < 2.0) {
                TextInCard(R.string.additionalMetabolicAlkalosis)
            } else if (2.0 <= hco3Change && hco3Change < 4.0) {
                TextInCard(R.string.acuteRespiratoryAlkalosis)
            } else if (hco3Change in 4.0..5.0) {
                TextInCard(R.string.chronicRespiratoryAlkalosis)
            } else if (hco3Change > 5.0) {
                TextInCard(R.string.additionalMetabolicAcidosis)
            }
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
            val aaDifference = 150 - paO2.doubleValue - 1.25 * paCo2.doubleValue
            var correctedAaDifference = aaDifference
            if (age.doubleValue >= 65) {
                correctedAaDifference = aaDifference / 2
            }
            if (correctedAaDifference <= 10) {
                TextInCard(R.string.HypoventilationWithoutIntrinsicLungDisease)
            } else if (correctedAaDifference > 10) {
                TextInCard(R.string.HypoventilationWithIntrinsicLungDiseaseVentilationPerfusionMismatchOrBoth)
            }
        }
    } else {
        if (hco3.doubleValue > (normalHco3 + 3) && paCo2.doubleValue > (normalPaCo2 + 5)) {
            TextInCard(R.string.metabolicAlkalosisAndRespiratoryAcidosis)
        } else if (hco3.doubleValue < (normalHco3 - 3) && paCo2.doubleValue < (normalPaCo2 - 5)) {
            TextInCard(R.string.metabolicAcidosisAndRespiratoryAlkalosis)
        }
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
