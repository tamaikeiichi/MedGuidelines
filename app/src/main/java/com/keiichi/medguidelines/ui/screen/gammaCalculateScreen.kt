package com.keiichi.medguidelines.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.ecog
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesFlowRow
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun GammaCalculateScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var gamma1 = remember { mutableDoubleStateOf(0.05) }
    var totalVolume1 = remember { mutableDoubleStateOf(50.0) }
    var bodyWeight1 = remember { mutableDoubleStateOf(60.0) }
    var flowRate1 = remember { mutableDoubleStateOf(2.0) }
    var totalDrugDose1 = remember { mutableDoubleStateOf(4.5) }
    var totalDrugDoseRound1 = Math.round(totalDrugDose1.doubleValue * 100.0) / 100.0
    var flowRateRound1 = Math.round(flowRate1.doubleValue * 100.0) / 100.0
    var totalVolumeRound1 = totalVolume1.doubleValue.toInt()

    var gamma2 = remember { mutableDoubleStateOf(0.05) }
    var totalVolume2 = remember { mutableDoubleStateOf(50.0) }
    var bodyWeight2 = remember { mutableDoubleStateOf(50.0) }
    var flowRate2 = remember { mutableDoubleStateOf(1.5) }
    var totalDrugDose2 = remember { mutableDoubleStateOf(5.0) }
    var totalDrugDoseRound2 = Math.round(totalDrugDose2.doubleValue * 100.0) / 100.0
    var flowRateRound2 = Math.round(flowRate2.doubleValue * 100.0) / 100.0
    var totalVolumeRound2 = totalVolume2.doubleValue.toInt()

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.infusionCalculator,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),

            ) {
            MedGuidelinesCard {
                Column() {
                    Text(
                        text = parseStyledString(R.string.findDrugDosage),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 1.2.em,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                Dimensions.textPadding
                            )
                    )

                    MedGuidelinesFlowRow {
                        InputValue(
                            label = R.string.bodyWeight,
                            value = bodyWeight1,
                            japaneseUnit = R.string.kg
                        )
                        InputValue(
                            label = R.string.targetDosageOfDrug,
                            value = gamma1,
                            japaneseUnit = R.string.ugkgmin
                        )
                        InputValue(
                            label = R.string.flowRate,
                            value = flowRate1,
                            japaneseUnit = R.string.mlhr
                        )
                        InputValue(
                            label = R.string.totalVolume,
                            value = totalVolume1,
                            japaneseUnit = R.string.ml
                        )
                    }
                }
            //}
            if (flowRate1.doubleValue != 0.0) {
                totalDrugDose1.doubleValue =
                    (gamma1.doubleValue * bodyWeight1.doubleValue * 60 / flowRate1.doubleValue) *
                            totalVolume1.doubleValue / 1000
            } else {
                totalDrugDose1.doubleValue = 0.0
            }
            //MedGuidelinesCard {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.Drug))
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold)
                        ) {
                            append(totalDrugDoseRound1.toString())
                        }
                        append(" ")
                        append(stringResource(R.string.mg))
                        append(" / ")
                        append(stringResource(R.string.saline))
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold)
                        ) {
                            append(totalVolumeRound1.toString())
                        }
                        append(" ")
                        append(stringResource(R.string.ml))
                    },
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.2.em,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            Dimensions.textPadding
                        )
                )
            }
            MedGuidelinesCard {
                Column() {
                    Text(
                        text = parseStyledString(R.string.findFlowRate),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left,
                        lineHeight = 1.2.em,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                Dimensions.textPadding
                            )
                    )
                    MedGuidelinesFlowRow {
                        InputValue(
                            label = R.string.bodyWeight,
                            value = bodyWeight2,
                            japaneseUnit = R.string.kg
                        )
                        InputValue(
                            label = R.string.targetDosageOfDrug,
                            value = gamma2,
                            japaneseUnit = R.string.ugkgmin
                        )
                        InputValue(
                            label = R.string.totalDrugDose,
                            value = totalDrugDose2,
                            japaneseUnit = R.string.mg
                        )
                        InputValue(
                            label = R.string.totalVolume,
                            value = totalVolume2,
                            japaneseUnit = R.string.ml
                        )
                    }
                }
            //}
            flowRate2.doubleValue =
                (gamma2.doubleValue * bodyWeight2.doubleValue * 60) / 1000 /
                        (totalDrugDose2.doubleValue) * totalVolume2.doubleValue
            //MedGuidelinesCard {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.flowRate))
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.error
                                ,fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(flowRateRound2.toString())
                        }
                        append(" ")
                        append(stringResource(R.string.mlhr))
                    },
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.2.em,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            Dimensions.textPadding
                        )
                )
            }
        }
    }
}

@Preview
@Composable
fun GammaCalculateScreenPreview() {
    GammaCalculateScreen(
        navController = NavController(LocalContext.current)
    )
}