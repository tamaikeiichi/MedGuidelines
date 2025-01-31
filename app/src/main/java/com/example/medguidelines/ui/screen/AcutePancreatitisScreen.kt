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
import com.example.medguidelines.data.CTGradeInflammation
import com.example.medguidelines.data.CTGradePoorContrast
import com.example.medguidelines.data.noYes
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun AcutePancreatitisScreen(navController: NavController) {
    var gradeByScore by remember { mutableStateOf("") }
    var cTGradeNumeric by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TitleTopAppBar(title = stringResource(id = R.string.acutePancreatitisTitle),
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
                        text = "$gradeByScore${stringResource(id = R.string.acutePancreatitis)}, CT Grade $cTGradeNumeric",
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
            var prognosticFactor = 0
            for (data in acutePancreatitisPrognosticFactorRadioButtonAndTitleAndNote) {
                acutePancreatitisButtonAndScore(data)
                prognosticFactor += data.score
            }

            var CTGrade = 0
            for (data in acutePancreatitisCTGradeRadioButtonAndTitleAndNote) {
                acutePancreatitisButtonAndScore(data)
                CTGrade += data.score
            }

            gradeByScore = if (prognosticFactor >= 3 || CTGrade >= 2) {
                stringResource(R.string.severe)
            } else {
                stringResource(R.string.mild)
            }

            cTGradeNumeric = when (CTGrade) {
                in 0..1 -> 1
                in 2..2 -> 2
                else -> 3

            }
        }
    }
}

data class AcutePancreatitisData(
    val radioOptions: List<Int>, val title: Int, val titleNote: Int, var score: Int = 0
)

val acutePancreatitisPrognosticFactorRadioButtonAndTitleAndNote = listOf(
    AcutePancreatitisData(noYes, R.string.acutePancreatitisBaseExcessTitle, R.string.space),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisPaO2Title, R.string.space),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisBUNTitle, R.string.space),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisLDHTitle, R.string.space),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisPltTitle, R.string.space),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisCaTitle, R.string.space),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisSIRSTitle, R.string.acutePancreatitisSIRSTitleNote),
)

val acutePancreatitisCTGradeRadioButtonAndTitleAndNote = listOf(
    AcutePancreatitisData(CTGradeInflammation, R.string.acutePancreatitisCTGradeInflammationTitle, R.string.space),
    AcutePancreatitisData(CTGradePoorContrast, R.string.acutePancreatitisCTGradePoorContrastTitle, R.string.acutePancreatitisCTGradePoorContrastTitleNote),
)

@Composable
fun acutePancreatitisButtonAndScore(
    data: AcutePancreatitisData
): AcutePancreatitisData
{
    val radioOptions : List<Int> = data.radioOptions
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