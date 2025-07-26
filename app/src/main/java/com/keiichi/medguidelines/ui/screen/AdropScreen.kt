package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.ui.component.FactorAlerts
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl

data class TotalScoreCurbAdrop(
    val adropTotalScore: Int,
    val curb65TotalScore: Int,
    val crb65TotalScore: Int
)

@Composable
fun AdropScreen(navController: NavController) {
    var adropTotalScore by remember { mutableIntStateOf(0) }
    var curb65TotalScore by remember { mutableIntStateOf(0) }
    var crb65TotalScore by remember { mutableIntStateOf(0) }
    var adropLiteralScore by remember { mutableStateOf("") }
    var curb65LiteralScore by remember { mutableStateOf("") }
    var crb65LiteralScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = buildAnnotatedString {
        append(stringResource(id = R.string.curb65))
        append(": ")
        append(curb65LiteralScore)
        append(" (")
        append(curb65TotalScore.toString())
        append(")")
        append("\n")
        append(stringResource(id = R.string.crb65))
        append(": ")
        append(crb65LiteralScore)
        append(" (")
        append(crb65TotalScore.toString())
        append(")")
        append("\n")
        append(stringResource(id = R.string.aDrop))
        append(": ")
        append(adropLiteralScore)
        append(" (")
        append(adropTotalScore.toString())
        append(")")
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.Curb65AdropTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.Curb65, R.string.Curb65Url)
                ),
                helpMessageResId = R.string.curb65aDrophelp
            )
        },
        bottomBar = {
            ScoreBottomAppBar(
                displayText = displayString,
                barHeight = 180.dp,
                fontSize = 25.sp
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            val currentScores: TotalScoreCurbAdrop = adropTotalScore()

            // Update the state variables from the single result
            // This will trigger recomposition if the currentScores object's content differs
            // from what was used to set these state variables in the previous composition.
            adropTotalScore = currentScores.adropTotalScore
            curb65TotalScore = currentScores.curb65TotalScore
            crb65TotalScore = currentScores.crb65TotalScore

            adropLiteralScore = when (adropTotalScore) {
                in 0..0 -> stringResource(id = R.string.adropLiteralScoreMild)
                in 1..2 -> stringResource(id = R.string.adropLiteralScoreModerate)
                in 3..3 -> stringResource(id = R.string.adropLiteralScoreSevere)
                else -> stringResource(id = R.string.adropLiteralScoreMostSevere)
            }
            curb65LiteralScore = when (curb65TotalScore) {
                in 0..1 -> stringResource(id = R.string.curb65LiteralScoreLow)
                in 2..2 -> stringResource(id = R.string.curb65LiteralScoreIntermediage)
                in 3..5 -> stringResource(id = R.string.curb65LiteralScoreHigh)
                else -> stringResource(id = R.string.error)
            }
            crb65LiteralScore = when (crb65TotalScore) {
                in 0..0 -> stringResource(id = R.string.crb65LiteralScoreLow)
                in 1..2 -> stringResource(id = R.string.crb65LiteralScoreIntermediage)
                in 3..4 -> stringResource(id = R.string.crb65LiteralScoreHigh)
                else -> stringResource(id = R.string.error)
            }
        }
    }
}

@Composable
fun adropTotalScore(): TotalScoreCurbAdrop {
    val scoreConsciousness = buttonAndScore(
        noYes,
        R.string.orientationTitle,
        R.string.space,
        appendixLabel = {
            Row() {
                FactorAlerts(
                    text = R.string.aDrop,
                    value = 1.0
                )
                FactorAlerts(
                    text = R.string.curb65,
                    value = 1.0
                )
                FactorAlerts(
                    text = R.string.crb65,
                    value = 1.0
                )
            }
        }
    )
    val scoreUre = buttonAndScore(
        noYes,
        R.string.urea7mmol,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.curb65,
                value = 1.0
            )
        }
    )
    val scoreRespiratoryRate = buttonAndScore(
        noYes,
        R.string.respirationRate30,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.curb65,
                value = 1.0
            )
        }
    )
    val scoreBloodPressure = buttonAndScore(
        noYes,
        R.string.bloodPressure9060,
        R.string.space,
        appendixLabel = {
            Row(){
                FactorAlerts(
                    text = R.string.curb65,
                    value = 1.0
                )
                FactorAlerts(
                    text = R.string.crb65,
                    value = 1.0
                )
            }
        }
    )
    val scoreAge = buttonAndScore(
        noYes,
        R.string.age65,
        R.string.space,
        appendixLabel = {
            Row() {
                FactorAlerts(
                    text = R.string.curb65,
                    value = 1.0
                )
                FactorAlerts(
                    text = R.string.crb65,
                    value = 1.0
                )
            }
        }
    )
    val scoreA = buttonAndScore(
        noYes,
        R.string.ageTitle,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.aDrop,
                value = 1.0
            )
        }
    )
    val scoreB = buttonAndScore(
        noYes,
        R.string.dehydrationTitle,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.aDrop,
                value = 1.0
            )
        }
    )
    val scoreC = buttonAndScore(
        noYes,
        R.string.respirationTitle,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.aDrop,
                value = 1.0
            )
        }
    )
    val scoreE = buttonAndScore(
        noYes,
        R.string.pressureTitle,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.aDrop,
                value = 1.0
            )
        }
    )
    val adropTotalScore =
        scoreA + scoreB + scoreC + scoreConsciousness + scoreE
    val curb65TotalScore =
        scoreConsciousness + scoreUre + scoreRespiratoryRate + scoreBloodPressure + scoreAge
    val crb65TotalScore =
        scoreConsciousness + scoreRespiratoryRate + scoreBloodPressure + scoreAge
    val totalScoreCurbAdrop = TotalScoreCurbAdrop(adropTotalScore, curb65TotalScore, crb65TotalScore)
    return totalScoreCurbAdrop
}

@Preview
@Composable
fun AdropPreview() {
    AdropScreen(navController = NavController(LocalContext.current))
}