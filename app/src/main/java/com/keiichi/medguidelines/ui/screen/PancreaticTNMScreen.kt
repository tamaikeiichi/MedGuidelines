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
import com.keiichi.medguidelines.data.MfactorPancreatic
import com.keiichi.medguidelines.data.NfactorPancreatic
import com.keiichi.medguidelines.data.TfactorPancreatic
import com.keiichi.medguidelines.data.pancreaticCancerTNM
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl

@Composable
fun PancreaticTNMScreen(navController: NavController) {
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
                title = R.string.pancreaticTNMTitle,
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
            score = TNMScore()
            literalScore = pancreaticCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
private fun TNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorPancreatic,
        R.string.TFactorTitle,
        R.string.TfactorTitleNotePancreaticCancer
    )
    val scoreB = buttonAndScore(
        NfactorPancreatic,
        R.string.NFactorTitle,
        R.string.NfactorTitleNotePancreaticCancer
    )
    val scoreC = buttonAndScore(
        MfactorPancreatic,
        R.string.MFactorTitle,
        R.string.MfactorTitleNotePancreaticCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    } else {
        listOf(scoreA, scoreC + NfactorPancreatic.size - 1)
    }
    return score
}

@Preview
@Composable
fun PnacreaticTNMScreenPreview() {
    PancreaticTNMScreen(navController = NavController(LocalContext.current))
}