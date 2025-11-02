package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.LabelAndScore
import com.keiichi.medguidelines.data.acutePhaseReactants
import com.keiichi.medguidelines.data.antiphospholipidAntibodies
import com.keiichi.medguidelines.data.complementProtiens
import com.keiichi.medguidelines.data.constitutional
import com.keiichi.medguidelines.data.durationOfSymptoms
import com.keiichi.medguidelines.data.hematologic
import com.keiichi.medguidelines.data.jointInvolvement
import com.keiichi.medguidelines.data.mucocutaneus
import com.keiichi.medguidelines.data.musculoskeletal
import com.keiichi.medguidelines.data.neuropsychiatric
import com.keiichi.medguidelines.data.raEntryCriterion
import com.keiichi.medguidelines.data.renal
import com.keiichi.medguidelines.data.serology
import com.keiichi.medguidelines.data.serosal
import com.keiichi.medguidelines.data.sleSpecificAntibodies
import com.keiichi.medguidelines.ui.component.CheckboxesAndExpandWithScore
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndExpand2Levels
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun RaScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var entryCriterionScore by remember { mutableIntStateOf(0) }

    // This is now a simple derived state. No need for its own `remember`.
    var displayScore by remember (entryCriterionScore, totalScore) { mutableStateOf(0) }
    displayScore = if (entryCriterionScore >= 2) totalScore else 0
    var diagnosis by remember { mutableStateOf("") }
    diagnosis = when (displayScore) {
        in 6..100 -> parseStyledString(R.string.classifyAsRa).toString()
        //"established"
        in 0..5 -> parseStyledString(R.string.doNotMeetTheCriteria).toString()// "possible"
        else -> parseStyledString(R.string.na).toString()// "very unlikely"
    }
    val displayText =
        buildAnnotatedString {
            append("Score: ")
            append(displayScore.toString())
            append(" (")
            append(diagnosis)
            append(")")
        }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = parseStyledString(R.string.raTitle),
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.raRefTitle, R.string.raUrl),
                    //TextAndUrl(R.string.slesJapRefTitle, R.string.sleJapsUrl),
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(
                displayText = displayText,
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            entryCriterionScore = entryCriterionRa()
            if (entryCriterionScore >= 2) {
                totalScore = raTotalScore() // Assuming childPughTotalScore() returns an Int
            }
        }
    }
}

@Composable
fun entryCriterionRa(): Int {
    val optionsWithScores = raEntryCriterion
    var defaultSelectedOptions by remember {
        mutableStateOf(listOf(optionsWithScores[0], optionsWithScores[1]))
    }
    var selectedOptions by remember { mutableStateOf<List<LabelAndScore>>(emptyList()) }
    val score = defaultSelectedOptions.sumOf { it.score } // Parent calculates the score// In Component Signature

    val entryCriterion = CheckboxesAndExpandWithScore(
        optionsWithScores = optionsWithScores,
        title = R.string.raEntryCriterion,
        titleNote = R.string.raEntryCriterionNote,
        defaultSelectedOption = defaultSelectedOptions,
        isNumberDisplayed = false,
        onOptionSelected = { newSelection ->
            defaultSelectedOptions = newSelection
        },
    )
    return score
}


@Composable
fun raTotalScore(): Int {
    val jointInvolvementScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = jointInvolvement,
        title = R.string.jointInvolvementRa,
        titleNote = R.string.jointInvolvementNote,
        defaultSelectedOption = R.string.none
    )
    val serologyScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = serology,
        title = R.string.serology,
        titleNote = R.string.serologyNote,
        defaultSelectedOption = R.string.negativeRfAndNegativeAcpa
    )
    val acutePhaseReactantsScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = acutePhaseReactants,
        title = R.string.acutePhaseReactants,
        titleNote = R.string.nacutePhaseReactantsNote,
        defaultSelectedOption = R.string.normalCrpAndNormalEsr
    )
    val durationOfSymptomsScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = durationOfSymptoms,
        title = R.string.durationOfSymptoms,
        titleNote = R.string.durationOfSymptomsNote,
        defaultSelectedOption = R.string.lessThanSixWeeks
    )

    val totalScore =
        jointInvolvementScore +
                serologyScore +
                acutePhaseReactantsScore +
                durationOfSymptomsScore

    return totalScore
}

@Preview
@Composable
fun RaScreenPreview() {
    MedGuidelinesTheme {
        RaScreen(navController = NavController(LocalContext.current))
    }

}