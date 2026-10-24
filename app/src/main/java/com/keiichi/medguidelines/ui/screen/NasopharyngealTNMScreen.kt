package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.MfactorNasopharyngealCancer
import com.keiichi.medguidelines.data.NfactorNasopharyngealCancer
import com.keiichi.medguidelines.data.TfactorNasopharyngealCancer
import com.keiichi.medguidelines.data.nasopharyngealCancerTNM
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl


@Composable
fun NasopharyngealTNMScreen(navController: NavController) {
    var score by remember { mutableStateOf(listOf(0, 0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = buildAnnotatedString {
        append("Stage ")
        append(literalScore)
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.nasopharyngealTNMTitle,
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
            score = nasopharyngealTNMScore()
            literalScore = nasopharyngealCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
fun nasopharyngealTNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorNasopharyngealCancer,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteNasopharyngealCancer
    )
    val scoreB = buttonAndScore(
        NfactorNasopharyngealCancer,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteNasopharyngealCancer
    )
    val scoreC = buttonAndScore(
        MfactorNasopharyngealCancer,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteNasopharyngealCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    } else {
        listOf(scoreA, scoreC + NfactorNasopharyngealCancer.size - 1)
    }
    return score
}

@Preview
@Composable
fun NasopharyngealTNMScreenPreview() {
    NasopharyngealTNMScreen(navController = NavController(LocalContext.current))
}