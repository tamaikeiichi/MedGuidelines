package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.aldreteCirculation
import com.keiichi.medguidelines.data.aldreteConsciousness
import com.keiichi.medguidelines.data.aldreteExtremeties
import com.keiichi.medguidelines.data.aldreteRespiration
import com.keiichi.medguidelines.data.aldreteSaturation
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun AldreteScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var diagnosis by remember { mutableStateOf("") }

    val displayString = buildAnnotatedString{
        append("Score: $totalScore ($diagnosis)")
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.aldreteTitle,
                navController = navController,
                helpMessageResId = R.string.pacu,
                references = listOf(
                    TextAndUrl(R.string.aldreteRefTitle, R.string.aldreteUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable (
                displayText = displayString,
                fontSize = 30.sp,
            )
//            {
//                Text(
//                    text = "Score: $totalScore ($diagnosis)",
//                    fontSize = 30.sp,
//                    textAlign = TextAlign.Center,
//                    lineHeight = 1.2.em
//                )
//            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = aldreteTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            diagnosis = when (totalScore) {
                in 9..10 -> parseStyledString(R.string.acceptableForDischarge).toString()
                //"established"
                in 0..8 -> parseStyledString(R.string.closeObservation).toString()// "possible"
                else -> parseStyledString(R.string.notAssessed).toString()// "very unlikely"
            }
        }
    }
}

@Composable
fun aldreteTotalScore(): Int {
    val  activityScore = buttonAndScoreWithScore(
        optionsWithScores = aldreteExtremeties,
        title = R.string.acitivity,
        //titleNote = R.string.space,
        //defaultSelectedOption =  R.string.fourExtremities
    )
    val respirationScore =
        buttonAndScoreWithScore(
            aldreteRespiration,
            R.string.respiration,
            //R.string.space,
            //defaultSelectedOption =  R.string.absent
        )
    val circulationScore = buttonAndScoreWithScore(
        aldreteCirculation,
        R.string.circulation,
        R.string.systolicBloodPressure
    )
    val consciousnessScore =
        buttonAndScoreWithScore(
            aldreteConsciousness,
            R.string.cousciousness,
            R.string.space,
            //defaultSelectedOption =  R.string.absent
        )
    val saturationScore = buttonAndScoreWithScore(
        aldreteSaturation,
        R.string.o2saturation,
        //R.string.space,
        //defaultSelectedOption =  R.string.normal08
    )

    val totalScore =
        activityScore +
                respirationScore +
                circulationScore +
                consciousnessScore +
                saturationScore

    return totalScore
}

@Preview
@Composable
fun AldreteScorePreview() {
    AldreteScreen(navController = NavController(LocalContext.current))
}