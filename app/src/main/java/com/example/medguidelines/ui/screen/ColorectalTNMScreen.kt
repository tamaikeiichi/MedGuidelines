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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.data.Mfactor
import com.example.medguidelines.data.Nfactor
import com.example.medguidelines.data.Tfactor
import com.example.medguidelines.data.colorectalCancerTNM
import com.example.medguidelines.data.RadioButtonName
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.ScoreBottomAppBar
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun ColorectalTNMScreen(navController: NavController) {
    var score by remember { mutableStateOf(listOf(0,0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "Stage $literalScore"

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.colorectalTNMTitle),
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
            score = colorectalTNMScore()
            literalScore = colorectalCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
fun colorectalTNMScore(): List<Int> {
    val scoreA = colorectalTNMButtonAndScore(
        Tfactor,
        R.string.colorectalTTitle,
        R.string.TfactorTitleNote
    )
    val scoreB = colorectalTNMButtonAndScore(
        Nfactor,
        R.string.colorectalNtitle,
        R.string.NfactorTitleNote
    )
    val scoreC = colorectalTNMButtonAndScore(
        Mfactor,
        R.string.colorectalMtitle,
        R.string.MfactorTitleNote
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    }
    else {
        listOf(scoreA, scoreC + Nfactor.size - 1)
    }
    return  score
}

@Composable
fun colorectalTNMButtonAndScore(
    factor : List<RadioButtonName>,
    title : Int,
    titleNote : Int
): Int
{
    val radioOptions : List<RadioButtonName> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)
    val score: Int =
        radioOptions.indexOf(selectedOption).coerceAtLeast(0)
    return score
}

@Preview
@Composable
fun ColorectalTNMScreenPreview(){
    ColorectalTNMScreen(navController = NavController(LocalContext.current))
}