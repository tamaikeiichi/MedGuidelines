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

@Composable
fun GammaCalculateScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    val gamma = remember { mutableDoubleStateOf(0.05) }
    val totalVolume = remember { mutableDoubleStateOf(50.0) }
    val bodyWeight = remember { mutableDoubleStateOf(60.0) }
    val flowRate = remember { mutableDoubleStateOf(2.0) }
    val totalDrugDose = remember { mutableDoubleStateOf(0.0) }
    val totalDrugDoseRound = Math.round(totalDrugDose.doubleValue * 100.0) / 100.0
    val totalVolumeRound = totalVolume.doubleValue.toInt()

    LaunchedEffect(gamma.doubleValue, bodyWeight.doubleValue, flowRate.doubleValue) {
        totalDrugDose.doubleValue =
            (gamma.doubleValue * bodyWeight.doubleValue * 60 // ug/hr
                    / flowRate.doubleValue)* totalVolume.doubleValue / 1000
    }
//
//    LaunchedEffect(gamma, bodyWeight, totalDrugDose, flowRate) {
//        totalVolume.doubleValue =
//            (totalDrugDose.doubleValue / 1000) / (gamma.doubleValue * bodyWeight.doubleValue * 60 / flowRate.doubleValue)
//    }

    LaunchedEffect(gamma, bodyWeight, totalVolume, totalDrugDose) {
        flowRate.doubleValue =
            (gamma.doubleValue * bodyWeight.doubleValue * 60) /1000 / totalVolume.doubleValue * totalVolume.doubleValue
    }

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
        bottomBar = {
            ResultBottomAppBar {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.Drug))
                        append(" ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(totalDrugDoseRound.toString())
                        }
                        append(" ")
                        append(stringResource(R.string.mg))
                        append(" / ")
                        append(stringResource(R.string.saline))
                        append(" ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(totalVolumeRound.toString())
                        }
                        append(" ")
                        append(stringResource(R.string.ml))


                    },
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.2.em,
                    modifier = Modifier.padding(
                        Dimensions.textPadding
                    )
                )
            }
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
                MedGuidelinesFlowRow {
                    InputValue(
                        label = R.string.targetDosageOfDrug,
                        value = gamma,
                        japaneseUnit = R.string.ugkgmin
                    )
                    InputValue(
                        label = R.string.bodyWeight,
                        value = bodyWeight,
                        japaneseUnit = R.string.kg
                    )
                }
            }
            MedGuidelinesCard {
                MedGuidelinesFlowRow {
                    InputValue(
                        label = R.string.totalDrugDose,
                        value = totalDrugDose,
                        japaneseUnit = R.string.mg
                    )
                    InputValue(
                        label = R.string.totalVolume,
                        value = totalVolume,
                        japaneseUnit = R.string.ml
                    )

                    InputValue(
                        label = R.string.flowRate,
                        value = flowRate,
                        japaneseUnit = R.string.mlhr
                    )
                }
            }
//            totalDrugDose.doubleValue =
//                (gamma.doubleValue * bodyWeight.doubleValue * 60 / flowRate.doubleValue) * totalVolume.doubleValue / 1000
//            MedGuidelinesCard {
//                Text(
//                    buildAnnotatedString {
//                        append(stringResource(R.string.Drug))
//                        append(" ")
//                        withStyle(
//                            style = SpanStyle(fontWeight = FontWeight.Bold)
//                        ) {
//                            append(totalDrugDoseRound.toString())
//                        }
//                        append(" ")
//                        append(stringResource(R.string.mg))
//                        append(" / ")
//                        append(stringResource(R.string.saline))
//                        append(" ")
//                        withStyle(
//                            style = SpanStyle(fontWeight = FontWeight.Bold)
//                        ) {
//                            append(totalVolumeRound.toString())
//                        }
//                        append(" ")
//                        append(stringResource(R.string.ml))
//
//
//                    },
//                    modifier = Modifier.padding(
//                        Dimensions.textPadding
//                    )
//                )
//
//
//            }
        }
    }
}

@Preview
@Composable
fun gammaCalculateScreenPreview() {
        GammaCalculateScreen(
            navController = NavController(LocalContext.current)
    )
}