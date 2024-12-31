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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.medguidelines.R
import com.example.medguidelines.data.Mfactor
import com.example.medguidelines.data.Nfactor
import com.example.medguidelines.data.Tfactor
import com.example.medguidelines.data.colorectalCancerTNM
import com.example.medguidelines.data.labDataNames
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.ScoreBottomAppBar
import com.example.medguidelines.ui.component.TitleTopAppBar


//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorectalTNMScreen() {
    var score by remember { mutableStateOf(listOf<Int>(0,0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "Stage $literalScore"

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.colorectalTNMTitle))
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
            score = ColorectalTNMScore()
            literalScore = colorectalCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
fun ColorectalTNMScore(): List<Int> {
    Column (
        Modifier
        //.verticalScroll(rememberScrollState()),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
    }
    val ScoreA = colorectalTNMButtonAndScore(
        Tfactor,
        stringResource(id = R.string.colorectalTTitle),
        R.string.TfactorTitleNote
    )
    val ScoreB = colorectalTNMButtonAndScore(
        Nfactor,
        stringResource(id = R.string.colorectalNtitle),
        R.string.NfactorTitleNote
    )
    val ScoreC = colorectalTNMButtonAndScore(
        Mfactor,
        stringResource(id = R.string.colorectalMtitle),
        R.string.MfactorTitleNote
    )
    val score = if (ScoreC == 0) {
        listOf(ScoreA, ScoreB)
    }
    else {
        listOf(ScoreA, ScoreC + Nfactor.size - 1)
    }
    return  score
}

@Composable
fun colorectalTNMButtonAndScore(
    factor : List<labDataNames>,
    title : String,
    titleNote : Int
): Int
{
    val radioOptions : List<labDataNames> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)
    val score: Int =
        radioOptions.indexOf(selectedOption).coerceAtLeast(0)
    return score
}


@Preview
@Composable
fun ColorectalTNMScreenPreview(){
    ColorectalTNMScreen()
}