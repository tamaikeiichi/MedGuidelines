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
import com.keiichi.medguidelines.data.MfactorHcc
import com.keiichi.medguidelines.data.MfactorIntrahepaticCholangiocarcinoma
import com.keiichi.medguidelines.data.NfactorHcc
import com.keiichi.medguidelines.data.NfactorIntrahepaticCholangiocarcinoma
import com.keiichi.medguidelines.data.TfactorHcc
import com.keiichi.medguidelines.data.TfactorIntrahepaticCholangiocarcinoma
import com.keiichi.medguidelines.data.hccTNM
import com.keiichi.medguidelines.data.intrahepaticCholangiocarcinomaTNM
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore


@Composable
fun IntrahepaticCholangiocarcinomaTNMScreen(navController: NavController) {
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
                title = R.string.intrahepaticCholangiocarcinomaTNMTitle,
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
            literalScore = intrahepaticCholangiocarcinomaTNM[score.last()][score.first()]
        }
    }
}

@Composable
private fun tNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorIntrahepaticCholangiocarcinoma,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteIntrahepaticCholangiocarcinoma
    )
    val scoreB = buttonAndScore(
        NfactorIntrahepaticCholangiocarcinoma,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteHcc
    )
    val scoreC = buttonAndScore(
        MfactorIntrahepaticCholangiocarcinoma,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteHcc
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    } else {
        listOf(scoreA, scoreC + NfactorIntrahepaticCholangiocarcinoma.size - 1)
    }
    return score
}

@Preview
@Composable
fun IntrahepaticCholangiocarcinomaTNMScreenPreview() {
    IntrahepaticCholangiocarcinomaTNMScreen(navController = NavController(LocalContext.current))
}