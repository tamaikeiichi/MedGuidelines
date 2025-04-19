package com.keiichi.medguidelines.ui.screen

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
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.MfactorColorectal
import com.keiichi.medguidelines.data.NfactorColorectal
import com.keiichi.medguidelines.data.TfactorColorectal
import com.keiichi.medguidelines.data.colorectalCancerTNM
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.textAndUrl

@Composable
fun ColorectalTNMScreen(navController: NavController) {
    var score by remember { mutableStateOf(listOf(0,0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "Stage $literalScore"

    Scaffold(
        topBar = {
            TitleTopAppBar(title =  R.string.colorectalTNMTitle,
                navController = navController,
                references = listOf(
                    textAndUrl(R.string.space, R.string.space)
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
            score = colorectalTNMScore()
            literalScore = colorectalCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
fun colorectalTNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorColorectal,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteColorectalCancer
    )
    val scoreB = buttonAndScore(
        NfactorColorectal,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteColorectalCancer
    )
    val scoreC = buttonAndScore(
        MfactorColorectal,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteColorectalCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    }
    else {
        listOf(scoreA, scoreC + NfactorColorectal.size - 1)
    }
    return  score
}

@Preview
@Composable
fun ColorectalTNMScreenPreview(){
    ColorectalTNMScreen(navController = NavController(LocalContext.current))
}