package com.keiichi.medguidelines.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.GraphAndThreshold
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesFlowRow
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import kotlin.math.exp

@SuppressLint("DefaultLocale")
@Composable
fun LilleModelScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var score by remember { mutableDoubleStateOf(0.000) }
    var scoreRound by remember { mutableStateOf("0.00") }
    var sixMonthSurvival: String = ""

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.lilleModelInr,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.lilleModel, R.string.lilleModelUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBar(displayText = buildAnnotatedString {
                append(scoreRound)
                append(" 6-month survival is ")
                append(sixMonthSurvival)
            }
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
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),

            ) {
            score = score()
            scoreRound = String.format("%.2f", score)
            sixMonthSurvival = if (score >= 0.45) "25%" else "85%"
        }

    }
}

@Composable
private fun score(): Double {
    val age = remember { mutableDoubleStateOf(45.0) }
    val albumin = remember { mutableDoubleStateOf(4.0) }
    val bilirubinDay0 = remember { mutableDoubleStateOf(1.0) }
    val bilirubinDay7 = remember { mutableDoubleStateOf(1.0) }
    val creatinine = remember { mutableDoubleStateOf(1.0) }
    val ptInr = remember { mutableDoubleStateOf(1.0) }
    var renalInsufficiency = remember { mutableStateOf(false) }
    var score by remember { mutableDoubleStateOf(0.0) }

    MedGuidelinesCard {
        MedGuidelinesFlowRow {
            InputValue(
                label = R.string.age,
                value = age,
                japaneseUnit = R.string.years
            )
            InputValue(
                label = R.string.albumin,
                value = albumin,
                japaneseUnit = R.string.gdL,
                changedValueRate = 10.0,
                changedUnit = R.string.gL
            )
            InputValue(
                label = R.string.bilirubinDay0,
                value = bilirubinDay0,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 17.1,
                changedUnit = R.string.umolL
            )
            InputValue(
                label = R.string.bilirubinDay7,
                value = bilirubinDay7,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 17.1,
                changedUnit = R.string.umolL
            )
            InputValue(
                label = R.string.creatinine,
                value = creatinine,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 88.4,
                changedUnit = R.string.mmoll
            )
            InputValue(
                label = R.string.ptInr,
                value = ptInr,
                japaneseUnit = R.string.space
            )
        }
    }
    MedGuidelinesCard {
        GraphAndThreshold(
            maxValue = 1.0F,
            minValue = 0.0F,
            firstThreshold = 0.45F,
            //secondThreshold = 0.0F,
            firstLabel = stringResource(R.string.lowRisk),
            secondLabel = stringResource(R.string.highRisk),
            score = (score * 100).toInt().toDouble() / 100.0,
        )
    }
    renalInsufficiency.value = creatinine.doubleValue > 1.3
    val renalInsufficiencyNumericValue = if (renalInsufficiency.value) 1.0 else 0.0

    val r = 3.19 -
            (0.101 * age.doubleValue) +
            (0.147 * 10 * albumin.doubleValue) +
            (0.0165 * 17.1 * (bilirubinDay7.doubleValue - bilirubinDay0.doubleValue)) -
            (0.206 * renalInsufficiencyNumericValue) -
            (0.0065 * 17.1 * bilirubinDay0.doubleValue) -
            (0.0096 * ptInr.doubleValue)
    score = (exp(-r) / (1 + exp(-r)))
    return score
}

@Preview
@Composable
fun LilleModelScreenPreview() {
    LilleModelScreen(
        navController = NavController(LocalContext.current)
    )
}