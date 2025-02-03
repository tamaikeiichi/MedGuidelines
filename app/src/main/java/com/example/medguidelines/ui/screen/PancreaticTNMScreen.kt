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
import com.example.medguidelines.data.MfactorColorectal
import com.example.medguidelines.data.MfactorPancreatic
import com.example.medguidelines.data.NfactorColorectal
import com.example.medguidelines.data.NfactorPancreatic
import com.example.medguidelines.data.TfactorColorectal
import com.example.medguidelines.data.TfactorPancreatic
import com.example.medguidelines.data.pancreaticCancerTNM
import com.example.medguidelines.ui.component.ScoreBottomAppBar
import com.example.medguidelines.ui.component.TitleTopAppBar
import com.example.medguidelines.ui.component.buttonAndScore

@Composable
fun PancreaticTNMScreen(navController: NavController) {
    var score by remember { mutableStateOf(listOf(0,0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "Stage $literalScore"

    Scaffold(
        topBar = {
            TitleTopAppBar(title =  R.string.pancreaticTNMTitle,
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
            literalScore = pancreaticCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
fun TNMScore(): List<Int> {
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
    }
    else {
        listOf(scoreA, scoreC + NfactorPancreatic.size - 1)
    }
    return  score
}

@Preview
@Composable
fun PnacreaticTNMScreenPreview(){
    PancreaticTNMScreen(navController = NavController(LocalContext.current))
}