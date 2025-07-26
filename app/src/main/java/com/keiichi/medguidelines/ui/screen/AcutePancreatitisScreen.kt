package com.keiichi.medguidelines.ui.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.CTGradeInflammation
import com.keiichi.medguidelines.data.CTGradePoorContrast
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.ui.component.GraphAndThreshold
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.RadioButtonAndExpand
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.cardModifier

@Composable
fun AcutePancreatitisScreen(navController: NavController) {
    var gradeByScore by remember { mutableStateOf("") }
    var cTGradeNumeric by remember { mutableIntStateOf(0) }
    var prognosticFactor by remember { mutableIntStateOf(0) }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.acutePancreatitisTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
        bottomBar = {
            ResultBottomAppBar(
               // barHeight = 100.dp
            ) {

                Text(
                    text = "$gradeByScore${stringResource(id = R.string.acutePancreatitis)}\nCT Grade $cTGradeNumeric",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.2.em,
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {

            prognosticFactor =0
            for (data in acutePancreatitisPrognosticFactorRadioButtonAndTitleAndNote) {
                acutePancreatitisButtonAndScore(data)

                prognosticFactor += data.score
            }

            var CTGrade = 0
            for (data in acutePancreatitisCTGradeRadioButtonAndTitleAndNote) {
                acutePancreatitisButtonAndScore(data)
                CTGrade += data.score
            }

            Card(
                modifier = Modifier.cardModifier(),
                colors = CardDefaults.cardColors(
                    containerColor =MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.prognosticFactor),
                    modifier = Modifier.textModifier(),
                    color = MaterialTheme.colorScheme.primary
                )
                GraphAndThreshold(
                    maxValue = 9.0F,
                    minValue = 0.0F,
                    firstThreshold = 3.0F,
                    firstLabel = stringResource(R.string.space),
                    secondLabel = stringResource(R.string.severe),
                    score = prognosticFactor.toDouble(),
                )
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
    AcutePancreatitisData(noYes, R.string.acutePancreatitisCRPTitle, R.string.space),
    AcutePancreatitisData(
        noYes,
        R.string.acutePancreatitisSIRSTitle,
        R.string.acutePancreatitisSIRSTitleNote
    ),
    AcutePancreatitisData(noYes, R.string.acutePancreatitisAgeTitle, R.string.space),
)

val acutePancreatitisCTGradeRadioButtonAndTitleAndNote = listOf(
    AcutePancreatitisData(
        CTGradeInflammation,
        R.string.acutePancreatitisCTGradeInflammationTitle,
        R.string.space
    ),
    AcutePancreatitisData(
        CTGradePoorContrast,
        R.string.acutePancreatitisCTGradePoorContrastTitle,
        R.string.acutePancreatitisCTGradePoorContrastTitleNote
    ),
)

@Composable
fun acutePancreatitisButtonAndScore(
    data: AcutePancreatitisData
): AcutePancreatitisData {
    val radioOptions: List<Int> = data.radioOptions
    var selectedOption by rememberSaveable { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(
        radioOptions,
        selectedOption,
        { selectedOption = it },
        data.title,
        data.titleNote,

    )
    data.score = radioOptions.indexOf(selectedOption).coerceAtLeast(0)
    return data
}

@Preview
@Composable
fun AcutePancreatitisScreenPreview() {
    AcutePancreatitisScreen(navController = NavController(LocalContext.current))
}