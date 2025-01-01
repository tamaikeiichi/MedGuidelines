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
import com.example.medguidelines.R
import com.example.medguidelines.data.Mfactor
import com.example.medguidelines.data.Nfactor
import com.example.medguidelines.data.Tfactor
import com.example.medguidelines.data.colorectalCancerTNM
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun AcuteTonsillitisAlgorithmScreen() {
    var score by remember { mutableStateOf(listOf(0,0)) }
    var literalScore by remember { mutableStateOf("") }

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
            literalScore = colorectalCancerTNM[score.last()][score.first()]
        }
    }
}


@Composable
fun acuteTonsillitisAlgorithmScore(): List<Int> {
    Column (
        Modifier
        //.verticalScroll(rememberScrollState()),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
    }
    val scoreA = colorectalTNMButtonAndScore(
        Tfactor,
        stringResource(id = R.string.colorectalTTitle),
        R.string.TfactorTitleNote
    )
    val scoreB = colorectalTNMButtonAndScore(
        Nfactor,
        stringResource(id = R.string.colorectalNtitle),
        R.string.NfactorTitleNote
    )
    val scoreC = colorectalTNMButtonAndScore(
        Mfactor,
        stringResource(id = R.string.colorectalMtitle),
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