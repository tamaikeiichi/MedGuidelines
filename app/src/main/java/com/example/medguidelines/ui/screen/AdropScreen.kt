package com.example.medguidelines.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medguidelines.R
import com.example.medguidelines.data.ageGrade
import com.example.medguidelines.data.albuminGrade
import com.example.medguidelines.data.ascitesGrade
import com.example.medguidelines.data.bilirubinGrade
import com.example.medguidelines.data.dehydrationGrade
import com.example.medguidelines.data.encephalopathyGrade
import com.example.medguidelines.data.labDataNames
import com.example.medguidelines.data.orientationGrade
import com.example.medguidelines.data.pressureGrade
import com.example.medguidelines.data.ptGrade
import com.example.medguidelines.data.respirationGrade
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.ScoreBottomAppBar
import com.example.medguidelines.ui.component.TitleTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdropScreen() {
    var totalScore by remember { mutableStateOf(0) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "$literalScore ($totalScore)"

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.aDropTitle))
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
            totalScore = adropTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            literalScore = when (totalScore) {
                in 0 .. 0 -> stringResource(id = R.string.adropLiteralScoreMild)
                in 1..2 -> stringResource(id = R.string.adropLiteralScoreModerate)
                in 3..3 -> stringResource(id = R.string.adropLiteralScoreSevere)
                else -> stringResource(id = R.string.adropLiteralScoreMostSevere)
            }
        }
    }
}

@Composable
fun adropTotalScore(): Int {
    Column (
        Modifier
        //.verticalScroll(rememberScrollState()),
        //horizontalAlignment = Alignment.CenterHorizontally
    ) {
    }
    val ScoreA = adropButtonAndScore(
        ageGrade,
        stringResource(id = R.string.ageTitle),
        R.string.space
    )
    val ScoreB = adropButtonAndScore(
        dehydrationGrade,
        stringResource(id = R.string.dehydrationTitle),
        R.string.space
    )
    val ScoreC = adropButtonAndScore(
        respirationGrade,
            stringResource(id = R.string.respirationTitle),
        R.string.space
    )
    val ScoreD = adropButtonAndScore(
        orientationGrade,
        stringResource(id = R.string.orientationTitle),
        R.string.space
    )
    val ScoreE = adropButtonAndScore(
        pressureGrade,
        stringResource(id = R.string.pressureTitle),
        R.string.space
    )
    val totalScore =
        ScoreA + ScoreB + ScoreC + ScoreD + ScoreE
    return  totalScore
}

//data class TotalScoreClass (
//    val totalScoreValue : Int
//)

@Composable
fun adropButtonAndScore(
    factor : List<labDataNames>,
    title : String,
    titleNote : Int
): Int
{
    val radioOptions : List<labDataNames> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)

    val score: Int =
        if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[0].stringid)) 0
        else 1

    return score
}


@Preview
@Composable
fun AdropPreview(){
    AdropScreen()
}