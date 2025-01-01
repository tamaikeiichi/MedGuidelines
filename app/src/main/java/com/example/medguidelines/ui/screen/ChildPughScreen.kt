package com.example.medguidelines.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.medguidelines.R
import com.example.medguidelines.data.albuminGrade
import com.example.medguidelines.data.ascitesGrade
import com.example.medguidelines.data.bilirubinGrade
import com.example.medguidelines.data.encephalopathyGrade
import com.example.medguidelines.data.RadioButtonName
import com.example.medguidelines.data.ptGrade
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun ChildPughScreen() {
    var totalScore by remember { mutableIntStateOf(0) }
    var childPughScoreABC by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.childPughTitle))
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Child-Pugh $childPughScoreABC ($totalScore)",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = childPughTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            childPughScoreABC = when (totalScore) {
                in 5..6 -> "A"
                in 7..9 -> "B"
                else -> "C"
            }
        }
    }
}

@Composable
fun childPughTotalScore(): Int {
    val bilirubinScore = childPughButtonAndScore(
        bilirubinGrade,
        R.string.bilirubinTitle,
        R.string.bilirubinTitleNote
    )
    val albuminScore =
        childPughButtonAndScore(
            albuminGrade,
            R.string.albuminTitle,
            R.string.albuminTitleNote
        )
    val ptScore = childPughButtonAndScore(ptGrade,
        R.string.ptTitle,
        R.string.ptTitleNote
    )
    val ascitesScore =
        childPughButtonAndScore(
            ascitesGrade,
            R.string.ascitesTitle,
            R.string.ascitesTitleNote
        )
    val encephalopathyScore = childPughButtonAndScore(
        encephalopathyGrade,
        R.string.encephalopaphyTitle,
        R.string.encephalopaphyTitleNote
    )

    val totalScore =
        bilirubinScore + albuminScore + ptScore + ascitesScore + encephalopathyScore
    //Text(text = totalScore.toString())
    return  totalScore
}

@Composable
fun childPughButtonAndScore(
    factor : List<RadioButtonName>,
    title : Int,
    titleNote : Int
): Int
{
    val radioOptions : List<RadioButtonName> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)

    val score: Int =
        if (stringResource(id = selectedOption.stringId) == stringResource(id =radioOptions[0].stringId)) 1
        else if (stringResource(id = selectedOption.stringId) == stringResource(id =radioOptions[1].stringId)) 2
        else  3

    return score
}

@Preview
@Composable
fun ChildPughScreenPreview(){
    ChildPughScreen()
}