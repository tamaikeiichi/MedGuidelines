package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.medguidelines.R
import com.example.medguidelines.data.colorectalCancerTNM
import com.example.medguidelines.data.RadioButtonName
import com.example.medguidelines.data.absencePresence
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun AcuteTonsillitisAlgorithmScreen() {
    var score by remember { mutableIntStateOf(0) }
    //var literalScore by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.acuteTonsillitisAlgorithmTitle))
        },
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            score = acuteTonsillitisAlgorithmScore()
            //literalScore = colorectalCancerTNM[score.last()][score.first()]
        }
    }
}

@Composable
fun acuteTonsillitisAlgorithmScore(): Int {
        val scoreA = acuteTonsillitisAlgorithmButtonAndScore(
            absencePresence,
            R.string.temperatureTitle,
            R.string.TfactorTitleNote
        )
        return scoreA
    }

@Composable
fun acuteTonsillitisAlgorithmButtonAndScore(
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
fun AcuteTonsillitisAlgorithmScreenPreview() {
    AcuteTonsillitisAlgorithmScreen()
}