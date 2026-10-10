package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.MeldResult
import com.keiichi.medguidelines.data.calculateMeld
import com.keiichi.medguidelines.data.meld3BaselineSurvival
import com.keiichi.medguidelines.data.meld3Survival
import com.keiichi.medguidelines.data.meldDialysis
import com.keiichi.medguidelines.data.meldSex
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesFlowRow
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed

@Composable
fun MeldScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var result by remember { mutableStateOf(MeldResult(meld = 6, meldNa = 6, meld3 = 6)) }
    val mortality90 = (1 - meld3Survival(result.meld3, meld3BaselineSurvival.last().second)) * 100

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.meldTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.meldRef, R.string.meldUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(displayText = buildAnnotatedString {
                append("MELD ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(result.meld.toString())
                }
                append("\n")
                append("  MELD-Na ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(result.meldNa.toString())
                }
                append("\nMELD 3.0 ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(result.meld3.toString())
                }
                append("\n")
                append(" (")
                append(stringResource(R.string.meld90DayMortality, mortality90))
                append(")")
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
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            result = meldInput()
            //meld3Prognosis(result.meld3)
        }
    }
}

@Composable
private fun meldInput(): MeldResult {
    val bilirubin = remember { mutableDoubleStateOf(1.0) }
    val creatinine = remember { mutableDoubleStateOf(1.0) }
    val ptInr = remember { mutableDoubleStateOf(1.0) }
    val sodium = remember { mutableDoubleStateOf(137.0) }
    val albumin = remember { mutableDoubleStateOf(3.5) }

    val sex = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = meldSex,
        title = R.string.sex,
        defaultSelectedOption = R.string.male,
        isNumberDisplayed = false
    )
    MedGuidelinesCard {
        MedGuidelinesFlowRow {
            InputValue(
                label = R.string.totalBilirubin,
                value = bilirubin,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 17.1,
                changedUnit = R.string.umolL
            )
            InputValue(
                label = R.string.creatinine,
                value = creatinine,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 88.4,
                changedUnit = R.string.umolL
            )
            InputValue(
                label = R.string.ptInr,
                value = ptInr,
                japaneseUnit = R.string.space
            )
            InputValue(
                label = R.string.sodium,
                value = sodium,
                japaneseUnit = R.string.mmoll
            )
            InputValue(
                label = R.string.albumin,
                value = albumin,
                japaneseUnit = R.string.gdL,
                changedValueRate = 10.0,
                changedUnit = R.string.gL
            )
        }
    }
    val dialysis = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = meldDialysis,
        title = R.string.dialysisTwiceInPastWeek,
        defaultSelectedOption = R.string.no,
        isNumberDisplayed = false
    )

    return calculateMeld(
        bilirubin = bilirubin.doubleValue,
        creatinine = creatinine.doubleValue,
        inr = ptInr.doubleValue,
        sodium = sodium.doubleValue,
        albumin = albumin.doubleValue,
        isFemale = sex == 1,
        dialysisTwiceInPastWeek = dialysis == 1
    )
}

@Composable
private fun meld3Prognosis(meld3: Int) {
    MedGuidelinesCard {
        Column {
            Text(
                text = stringResource(R.string.meld3Prognosis),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.textModifier()
            )
            meld3BaselineSurvival.forEach { (days, baseline) ->
                val survival = meld3Survival(meld3, baseline) * 100
                Text(
                    text = stringResource(R.string.meldSurvivalRow, days, survival, 100 - survival),
                    modifier = Modifier.textModifier()
                )
            }
            Text(
                text = stringResource(R.string.meldNaNote),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.textModifier()
            )
        }
    }
}

@Preview
@Composable
fun MeldScreenPreview() {
    MeldScreen(navController = NavController(LocalContext.current))
}
