package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.medguidelines.R
import com.example.medguidelines.data.NMfactor
import com.example.medguidelines.data.Tfactor
import com.example.medguidelines.data.UiccTnm
import com.example.medguidelines.data.UiccTnmIndex
import com.example.medguidelines.data.ageGrade
import com.example.medguidelines.data.colorectalCancerTNM
import com.example.medguidelines.data.dehydrationGrade
import com.example.medguidelines.data.labDataNames
import com.example.medguidelines.data.orientationGrade
import com.example.medguidelines.data.pressureGrade
import com.example.medguidelines.data.respirationGrade
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.ScoreBottomAppBar
import com.example.medguidelines.ui.component.TitleTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorectalTNMScreen() {
    var score by remember { mutableStateOf(listOf<Int>(0,0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = "$literalScore ($totalScore)"

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
            score = ColorectalTNMScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            literalScore = colorectalCancerTNM[score.first][score.second]
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
        ""
    )
    val ScoreB = colorectalTNMButtonAndScore(
        NMfactor,
        stringResource(id = R.string.colorectalNMtitle),
        ""
    )
    val score = listOf(ScoreA, ScoreB)
    return  score
}

@Composable
fun colorectalTNMButtonAndScore(
    factor : List<UiccTnmIndex>,
    title : String,
    titleNote : String
): Int
{
    val radioOptions : List<UiccTnmIndex> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)

    val score: Int =
        radioOptions.indexOf(selectedOption).coerceAtLeast(0)
//        if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[0].stringid)) 0
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[1].stringid)) 1
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[2].stringid)) 2
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[3].stringid)) 3
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[4].stringid)) 4
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[5].stringid)) 5
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[6].stringid)) 6
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[7].stringid)) 7
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[8].stringid)) 8
//        else if (stringResource(id = selectedOption.stringid) == stringResource(id =radioOptions[9].stringid)) 9
//        else 10

    return score
}


@Preview
@Composable
fun AdropPreview(){
    AdropScreen()
}