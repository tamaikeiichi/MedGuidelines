package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.maleFemaleUnknown
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.data.yesNoUnknown
import com.keiichi.medguidelines.ui.component.FactorAlerts
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore

data class TotalScoreCurbAdrop(
    val adropTotalScore: Int,
    val curb65TotalScore: Int,
    val crb65TotalScore: Int
)

@Composable
fun AdropScreen(navController: NavController) {
    var adropTotalScore: Int? by remember { mutableStateOf<Int?>(null) }
    var curb65TotalScore: Int? by remember { mutableStateOf<Int?>(null) }
    var crb65TotalScore: Int? by remember { mutableStateOf<Int?>(null) }
    var adropLiteralScore by remember { mutableStateOf("") }
    var curb65LiteralScore by remember { mutableStateOf("") }
    var crb65LiteralScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = buildAnnotatedString {
        append(stringResource(id = R.string.curb65))
        append(": ")
        append(curb65LiteralScore)
        append(" (")
        append(curb65TotalScore?.toString() ?: "")
        append(")")
        append("\n")
        append(stringResource(id = R.string.crb65))
        append(": ")
        append(crb65LiteralScore)
        append(" (")
        append(crb65TotalScore?.toString() ?: "")
        append(")")
        append("\n")
        append(stringResource(id = R.string.aDrop))
        append(": ")
        append(adropLiteralScore)
        append(" (")
        append(adropTotalScore?.toString() ?: "")
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

            adropTotalScore = if (currentScores.adropTotalScore >= 100)
                null
            else
                currentScores.adropTotalScore
            curb65TotalScore = if (currentScores.curb65TotalScore >= 100)
                null else
                currentScores.curb65TotalScore
            crb65TotalScore = if (currentScores.crb65TotalScore >= 100)
                null else
                currentScores.crb65TotalScore

            adropLiteralScore = when (adropTotalScore) {
                in 0..0 -> stringResource(id = R.string.adropLiteralScoreMild)
                in 1..2 -> stringResource(id = R.string.adropLiteralScoreModerate)
                in 3..3 -> stringResource(id = R.string.adropLiteralScoreSevere)
                in 4..5 -> stringResource(id = R.string.adropLiteralScoreMostSevere)
                else -> stringResource(id = R.string.notAssessed)
            }
            curb65LiteralScore = when (curb65TotalScore) {
                in 0..1 -> stringResource(id = R.string.curb65LiteralScoreLow)
                in 2..2 -> stringResource(id = R.string.curb65LiteralScoreIntermediage)
                in 3..5 -> stringResource(id = R.string.curb65LiteralScoreHigh)
                else -> stringResource(id = R.string.notAssessed)
            }
            crb65LiteralScore = when (crb65TotalScore) {
                in 0..0 -> stringResource(id = R.string.crb65LiteralScoreLow)
                in 1..2 -> stringResource(id = R.string.crb65LiteralScoreIntermediage)
                in 3..4 -> stringResource(id = R.string.crb65LiteralScoreHigh)
                else -> stringResource(id = R.string.notAssessed)
            }
        }
    }
}

@Composable
fun adropTotalScore(): TotalScoreCurbAdrop {
    val un = remember { mutableDoubleStateOf(10.0) }
    val systolicBp = remember { mutableDoubleStateOf(120.0) }
    val diastolicBp = remember { mutableDoubleStateOf(70.0) }
    var age = remember { mutableDoubleStateOf(60.0) }
    val unCurb: Int
    val unAdrop: Int
    var gender: Int = 100
    val ageAdrop: Int
    var scoreConsciousness: Int = 100
    val systolicBpCurb: Int
    val systolicBpAdrop: Int
    val bpCurb: Int

    gender = buttonAndScoreWithScore(
        maleFemaleUnknown,
        R.string.gender,
        R.string.space,
        defaultSelectedOption = R.string.unknown,
        appendixLabel = {
            Row() {
                FactorAlerts(
                    text = R.string.aDrop,
                    value = gender - 100.0
                )
            }
        }
    )
    scoreConsciousness = buttonAndScoreWithScore(
        yesNoUnknown,
        R.string.orientationTitle,
        R.string.space,
        defaultSelectedOption = R.string.unknown,
        appendixLabel = {
            Row() {
                FactorAlerts(
                    text = R.string.aDrop,
                    value = scoreConsciousness - 100.0
                )
                FactorAlerts(
                    text = R.string.curb65,
                    value = scoreConsciousness - 100.0
                )
                FactorAlerts(
                    text = R.string.crb65,
                    value = scoreConsciousness - 100.0
                )
            }
        }
    )
    MedGuidelinesCard {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            InputValue(
                label = R.string.age,
                value = age,
                japaneseUnit = R.string.years,
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
            InputValue(
                label = R.string.ureaNitrogen,
                value = un,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 0.357,
                changedUnit = R.string.mmoll,
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
                    }
                }
            )

        }
    }

    if (un.doubleValue > 21.0) {
        unCurb = 1
        unAdrop = 1
    } else if (un.doubleValue > 19.6) {
        unCurb = 1
        unAdrop = 0
    } else {
        unCurb = 0
        unAdrop = 0
    }

    ageAdrop = if ((gender == 1 && age.doubleValue >= 70.0) ||
        (gender == 0 && age.doubleValue >= 65.0)) {
        1
    } else {0}
    val ageCurb: Int = if (age.doubleValue >= 65.0){
        1
    } else {0}

    val scoreRespiratoryRate = buttonAndScoreWithScore(
        yesNoUnknown,
        R.string.respirationRate30,
        R.string.space,
        defaultSelectedOption = R.string.unknown,
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
    MedGuidelinesCard {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            InputValue(
                label = R.string.systolicBloodPressure,
                value = systolicBp,
                japaneseUnit = R.string.mmhg,
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

            InputValue(
                label = R.string.diastolicBloodPressure,
                value = diastolicBp,
                japaneseUnit = R.string.mmhg,
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
        }
    }

    if (systolicBp.doubleValue <= 90.0) {
        systolicBpCurb = 0
        systolicBpAdrop = 1
    } else if (systolicBp.doubleValue < 90.0) {
        systolicBpCurb = 1
        systolicBpAdrop = 1
    } else {
        systolicBpCurb = 0
        systolicBpAdrop = 0
    }

    val diastolicBpCurb: Int = if (diastolicBp.doubleValue <= 60.0) {
        1 }  else {
        0 }
    bpCurb = if (systolicBpCurb == 1 || diastolicBpCurb == 1){
        1 }
    else {
        0 }

    val scoreC = buttonAndScoreWithScore(
        yesNoUnknown,
        R.string.respirationTitle,
        R.string.space,
        defaultSelectedOption = R.string.unknown,
        appendixLabel = {
            FactorAlerts(
                text = R.string.aDrop,
                value = 1.0
            )
        }
    )

    val adropTotalScore =
        ageAdrop + unAdrop + scoreC + scoreConsciousness + systolicBpAdrop
    val curb65TotalScore =
        scoreConsciousness + unCurb + scoreRespiratoryRate + bpCurb + ageCurb
    val crb65TotalScore =
        scoreConsciousness + scoreRespiratoryRate + bpCurb + ageCurb
    val totalScoreCurbAdrop =
        TotalScoreCurbAdrop(adropTotalScore, curb65TotalScore, crb65TotalScore)
    return totalScoreCurbAdrop
}

@Preview
@Composable
fun AdropPreview() {
    AdropScreen(navController = NavController(LocalContext.current))
}