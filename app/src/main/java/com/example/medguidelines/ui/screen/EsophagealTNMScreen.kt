package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.data.MfactorEsophageal
import com.example.medguidelines.data.NfactorEsophageal
import com.example.medguidelines.data.TfactorEsophageal
import com.example.medguidelines.data.esophagealCancerTNM
import com.example.medguidelines.ui.component.ScoreBottomAppBar
import com.example.medguidelines.ui.component.TitleTopAppBar
import com.example.medguidelines.ui.component.buttonAndScore

@Composable
fun EsophagealTNMScreen(navController: NavController) {
    var score by remember { mutableStateOf(listOf(0,0)) }
    var literalScore by remember { mutableStateOf("") }
    val displayString = "Stage $literalScore"
    Scaffold(
        topBar = {
            TitleTopAppBar(title =  R.string.esophagealTNMTitle,
                navController = navController,
                referenceText = R.string.space,
                referenceUrl = R.string.space
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
            literalScore = esophagealCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
private fun TNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorEsophageal,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteEsophagealCancer
    )
    val scoreB = buttonAndScore(
        NfactorEsophageal,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteEsophagealCancer
    )
    val scoreC = buttonAndScore(
        MfactorEsophageal,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteEsophagealCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    }
    else {
        listOf(scoreA, scoreC + NfactorEsophageal.size - 1)
    }
    return  score
}

@Preview
@Composable
fun EsophagealTNMScreenPreview(){
    EsophagealTNMScreen(navController = NavController(LocalContext.current))
}