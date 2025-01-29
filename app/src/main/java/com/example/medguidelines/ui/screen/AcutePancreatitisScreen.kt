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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.data.albuminGrade
import com.example.medguidelines.data.ascitesGrade
import com.example.medguidelines.data.bilirubinGrade
import com.example.medguidelines.data.encephalopathyGrade
import com.example.medguidelines.data.RadioButtonName
import com.example.medguidelines.data.noYes
import com.example.medguidelines.data.ptGrade
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun AcutePancreatitisScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var childPughScoreABC by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.childPughTitle),
                navController = navController,
                referenceText = R.string.space,
                referenceUrl = R.string.space
            )
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
    val scoreA = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisBaseExcessTitle,
        R.string.space
    )

    val scoreB = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisPaO2Title,
        R.string.space
    )

    val scoreC = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisBUNTitle,
        R.string.space
    )

    val scoreD = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisLDHTitle,
        R.string.space
    )

    val scoreE = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisPltTitle,
        R.string.space
    )

    val scoreF = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisCaTitle,
        R.string.space
    )

    val scoreG = acutePancreatitisButtonAndScore(
        noYes,
        R.string.acutePancreatitisSIRSTitle,
        R.string.space
    )




    val totalScore =
        bilirubinScore + albuminScore + ptScore + ascitesScore + encephalopathyScore
    //Text(text = totalScore.toString())
    return  totalScore
}

@Composable
fun acutePancreatitisButtonAndScore(
    factor : List<RadioButtonName>,
    title : Int,
    titleNote : Int
): Int
{
    val radioOptions : List<RadioButtonName> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)

    val score: Int =
        if (stringResource(id = selectedOption.stringId) == stringResource(id =radioOptions[0].stringId)) 0
        else if (stringResource(id = selectedOption.stringId) == stringResource(id =radioOptions[1].stringId)) 1
        else  2

    return score
}

@Preview
@Composable
fun AcutePancreatitisScreenPreview(){
    AcutePancreatitisScreen(navController = NavController(LocalContext.current))
}