package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.FactorAlerts
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.parseStyledString

data class TotalScore(
    val chads2Score: Int,
    val helte2s2Score: Int
)

@Composable
fun Chads2Screen(navController: NavController) {
    var totalScore: TotalScore by remember { mutableStateOf(TotalScore(0, 0)) }
    var chads2StrokeRate by remember { mutableStateOf("") }
    var helte2s2StrokeRate by remember { mutableStateOf("") }
    val chads2Result = buildAnnotatedString {
        append("CHADS2 (")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(totalScore.chads2Score.toString())
        }
        append(") ")
        append(parseStyledString(R.string.strokeRateWithoutAnticoagulant))
        append(" ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(chads2StrokeRate.toString())
        }
        append(parseStyledString(R.string.per100PatientYears))
    }
    val helte2s2Result = buildAnnotatedString {
        append(parseStyledString(R.string.helte2s2))
        append(" (")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(totalScore.helte2s2Score.toString())
        }
        append(") ")
        append(parseStyledString(R.string.strokeRateWithoutAnticoagulant))
        append(" ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(helte2s2StrokeRate)
        }
        append(parseStyledString(R.string.per100PatientYears))
    }
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.chads2AndHelte2s2Title,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.chads2, R.string.chads2Url),
                    TextAndUrl(R.string.helte2s2, R.string.helte2s2Url)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBar(displayText =
                buildAnnotatedString {
                    append("CHADS2 (")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(totalScore.chads2Score.toString())
                    }
                    append(") ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(chads2StrokeRate.toString())
                    }
                    append("%")
                    append(parseStyledString(R.string.perYear))
                    append("\n")
                    append(parseStyledString(R.string.helte2s2))
                    append(" (")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(totalScore.helte2s2Score.toString())
                    }
                    append(") ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(helte2s2StrokeRate)
                    }
                    append("%")
                    append(parseStyledString(R.string.perYear))

                })
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = totalScore()

            chads2StrokeRate = when (totalScore.chads2Score) {
                0 -> "1.9"
                1 -> "2.8"
                2 -> "4.0"
                3 -> "5.9"
                4 -> "8.5"
                5 -> "12.5"
                else -> "18.2"
            }
            helte2s2StrokeRate = when (totalScore.helte2s2Score) {
                0 -> "0.57"
                1 -> "0.73"
                2 -> "1.37"
                3 -> "2.59"
                4 -> "3.96"
                else -> "5.82"
            }
            MedGuidelinesCard(
                containerColor = MaterialTheme.colorScheme.onError,
                contentColor = MaterialTheme.colorScheme.error
            ) {
                //Column() {
                Text(
                    text = chads2Result,
                    modifier = Modifier.padding(Dimensions.textPadding)

                )
            }
            MedGuidelinesCard(
                containerColor = MaterialTheme.colorScheme.onError,
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = helte2s2Result,
                    modifier = Modifier.padding(Dimensions.textPadding)
                )
                //}
            }
        }
    }
}

@Composable
private fun totalScore(): TotalScore {
    var scoreAge85 by remember{mutableIntStateOf(0)}
//    FactorAlerts(
//        text = R.string.chads2,
//        factor = 1.0)
    val scoreCongestiveHeartFailure =
        buttonAndScore(
            noYes,
            R.string.congestiveHearFaiLureHistoryTitle,
            R.string.space,
            appendixLabel = {FactorAlerts(
                text = R.string.chads2,
                factor = 1.0
            )}
        )
    val scoreHypertension = buttonAndScore(
        noYes,
        R.string.hepertensionHistoryTitle,
        R.string.space,
        appendixLabel = {
            Row(){
                FactorAlerts(
                    text = R.string.chads2,
                    factor = 1.0
                )
                FactorAlerts(
                    text = R.string.helte2s2,
                    factor = 1.0
                )
            }
        }
    )
    val scoreAge75 = buttonAndScore(
        noYes,
        R.string.age75Title,
        R.string.space,
        appendixLabel = {
            Row(){
                FactorAlerts(
                    text = R.string.chads2,
                    factor = 1.0
                )
                FactorAlerts(
                    text = R.string.helte2s2,
                    factor = 1.0
                )
            }
        }
    )
    if(scoreAge75 == 1) {
        Column(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            scoreAge85 = buttonAndScore(
                noYes,
                R.string.extremeElderly,
                R.string.space,
                appendixLabel = {
                    FactorAlerts(
                        text = R.string.helte2s2,
                        factor = 1.0
                    )
                }
            )
        }
    }
    val scoreDiabetes = buttonAndScore(
        noYes,
        R.string.diabetesMellitusHistoryTitle,
        R.string.space,
        appendixLabel = {FactorAlerts(
            text = R.string.chads2,
            factor = 1.0
        )}
    )
    val scoreStroke = buttonAndScore(
        noYes,
        R.string.strokeOrTIASymptomsPreviouslyTitle,
        R.string.space ,       appendixLabel = {
            Row(){
                FactorAlerts(
                    text = R.string.chads2,
                    factor = 1.0
                )
                FactorAlerts(
                    text = R.string.helte2s2,
                    factor = 1.0
                )
            }
        }
    )
    val scoreLowBmi = buttonAndScore(
        noYes,
        R.string.lowBmi,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.helte2s2,
                factor = 1.0
            )
        }
    )
    val scoreAf = buttonAndScore(
        noYes,
        R.string.typeOfAf,
        R.string.space,
        appendixLabel = {
            FactorAlerts(
                text = R.string.helte2s2,
                factor = 1.0
            )
        }
    )
    val totalScore: TotalScore = TotalScore(
        chads2Score = scoreCongestiveHeartFailure + scoreHypertension + scoreAge75 + scoreDiabetes + (scoreStroke * 2),
        helte2s2Score = scoreHypertension + scoreLowBmi + scoreAf + scoreAge75 + (scoreAge85 * 2) + (scoreStroke * 2)
    )
    return totalScore
}

@Preview
@Composable
fun Chads2Preview() {
    Chads2Screen(navController = NavController(LocalContext.current))
}