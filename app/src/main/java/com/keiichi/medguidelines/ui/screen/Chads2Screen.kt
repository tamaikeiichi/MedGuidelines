package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore


@Composable
fun Chads2Screen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "" +
            //"$literalScore
            "Score: $totalScore"

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.chads2Title,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBar(displayText = displayString)
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = totalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            literalScore = when (totalScore) {
                in 0..0 -> stringResource(id = R.string.adropLiteralScoreMild)
                in 1..2 -> stringResource(id = R.string.adropLiteralScoreModerate)
                in 3..3 -> stringResource(id = R.string.adropLiteralScoreSevere)
                else -> stringResource(id = R.string.adropLiteralScoreMostSevere)
            }
        }
    }
}

@Composable
private fun totalScore(): Int {
    val scoreA = buttonAndScore(
        noYes,
        R.string.congestiveHearFaiLureHistoryTitle,
        R.string.space
    )
    val scoreB = buttonAndScore(
        noYes,
        R.string.hepertensionHistoryTitle,
        R.string.space
    )
    val scoreC = buttonAndScore(
        noYes,
        R.string.age75Title,
        R.string.space
    )
    val scoreD = buttonAndScore(
        noYes,
        R.string.diabetesMellitusHistoryTitle,
        R.string.space
    )
    val scoreE = buttonAndScore(
        noYes,
        R.string.strokeOrTIASymptomsPreviouslyTitle,
        R.string.space
    )
    val totalScore =
        scoreA + scoreB + scoreC + scoreD + (scoreE *2)
    return totalScore
}


@Preview
@Composable
fun Chads2Preview() {
    Chads2Screen(navController = NavController(LocalContext.current))
}