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
import com.example.medguidelines.data.RadioButtonName
import com.example.medguidelines.data.noYes
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun AcutePancreatitisScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var gradeByScore by remember { mutableStateOf("") }

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
                        text = "$gradeByScore ($totalScore)",
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
            totalScore = acutePancreatitisTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            gradeByScore = when (totalScore) {
                in 0..2 -> ""
                else -> stringResource(R.string.severe)
            }
        }
    }
}

data class acutePancreatitisData(
    val radioOptions: List<RadioButtonName>, val title: Int, val titleNote: Int, var score: Int = 0
)

val acutePancreatitisRadioButtonAndTitleAndNote = listOf(
    acutePancreatitisData(noYes, R.string.acutePancreatitisBaseExcessTitle, R.string.space),
    acutePancreatitisData(noYes, R.string.acutePancreatitisPaO2Title, R.string.space),
    acutePancreatitisData(noYes, R.string.acutePancreatitisBUNTitle, R.string.space),
    acutePancreatitisData(noYes, R.string.acutePancreatitisLDHTitle, R.string.space),
    acutePancreatitisData(noYes, R.string.acutePancreatitisPltTitle, R.string.space),
    acutePancreatitisData(noYes, R.string.acutePancreatitisCaTitle, R.string.space),
    acutePancreatitisData(noYes, R.string.acutePancreatitisSIRSTitle, R.string.space),
)

@Composable
fun acutePancreatitisTotalScore(): Int {
    var totalScore = 0
    for (data in acutePancreatitisRadioButtonAndTitleAndNote) {
        acutePancreatitisButtonAndScore(data)
        totalScore += data.score
    }
    return totalScore
}

@Composable
fun acutePancreatitisButtonAndScore(
    data: acutePancreatitisData
): acutePancreatitisData
{
    val radioOptions : List<RadioButtonName> = data.radioOptions
    var selectedOption by remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(
        radioOptions,
        selectedOption,
        { selectedOption = it },
        data.title,
        data.titleNote
    )
    data.score = radioOptions.indexOf(selectedOption).coerceAtLeast(0)
    return data
}

@Preview
@Composable
fun AcutePancreatitisScreenPreview(){
    AcutePancreatitisScreen(navController = NavController(LocalContext.current))
}