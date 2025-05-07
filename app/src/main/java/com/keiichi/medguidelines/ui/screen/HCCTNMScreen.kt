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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.MfactorLung
import com.keiichi.medguidelines.data.NfactorLung
import com.keiichi.medguidelines.data.TfactorHcc
import com.keiichi.medguidelines.data.TfactorLung
import com.keiichi.medguidelines.data.hccTNM
import com.keiichi.medguidelines.data.lungCancerTNM
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore


@Composable
fun HCCTNMScreen(navController: NavController) {
    var score by remember { mutableStateOf(listOf(0, 0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "Stage $literalScore"

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.hccTNMTitle,
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
            score = tNMScore()
            literalScore = hccTNM[score.last()][score.first()]
        }
    }
}

@Composable
private fun tNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorHcc,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteHcc
    )
    val scoreB = buttonAndScore(
        NfactorLung,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteLungCancer
    )
    val scoreC = buttonAndScore(
        MfactorLung,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteLungCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    } else {
        listOf(scoreA, scoreC + NfactorLung.size - 1)
    }
    return score
}

@Preview
@Composable
fun LungTNMScreenPreview() {
    LungTNMScreen(navController = NavController(LocalContext.current))
}