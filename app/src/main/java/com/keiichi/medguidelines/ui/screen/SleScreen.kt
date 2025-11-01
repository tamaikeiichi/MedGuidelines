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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.albuminGrade
import com.keiichi.medguidelines.data.antiphospholipidAntibodies
import com.keiichi.medguidelines.data.ascitesGrade
import com.keiichi.medguidelines.data.bilirubinGrade
import com.keiichi.medguidelines.data.complementProtiens
import com.keiichi.medguidelines.data.constitutional
import com.keiichi.medguidelines.data.coombsNegativeHemolyticAnemia
import com.keiichi.medguidelines.data.encephalopathyGrade
import com.keiichi.medguidelines.data.hematologic
import com.keiichi.medguidelines.data.kayserFleischerRings
import com.keiichi.medguidelines.data.liverCopper
import com.keiichi.medguidelines.data.mucocutaneus
import com.keiichi.medguidelines.data.musculoskeletal
import com.keiichi.medguidelines.data.mutationAnalysis
import com.keiichi.medguidelines.data.neurologicSymptoms
import com.keiichi.medguidelines.data.neuropsychiatric
import com.keiichi.medguidelines.data.ptGrade
import com.keiichi.medguidelines.data.renal
import com.keiichi.medguidelines.data.serosal
import com.keiichi.medguidelines.data.serumCeruloplasmin
import com.keiichi.medguidelines.data.sleEntryCriterion
import com.keiichi.medguidelines.data.sleSpecificAntibodies
import com.keiichi.medguidelines.data.urinaryCopper
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.parseStyledString
import org.apache.commons.math3.stat.interval.WilsonScoreInterval

@Composable
fun SleScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var diagnosis by remember { mutableStateOf("") }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = parseStyledString(R.string.sleTitle),
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.sleRefTitle, R.string.sleUrl)
                )
            )
        },
        bottomBar = {
            val displayText =
                buildAnnotatedString {
                    append("Score: ")
                    append(totalScore.toString())
                    append(" (")
                    append(diagnosis)
                    append(")")
                }
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
            val entryCriterionScore = entryCriterion()
            if (entryCriterionScore > 0) {
                totalScore = sleTotalScore() // Assuming childPughTotalScore() returns an Int
                //Text(text = totalScore.toString())

                diagnosis = when (totalScore) {
                    in 10..100 -> parseStyledString(R.string.classifyAsSle).toString()
                    //"established"
                    in 0..9 -> parseStyledString(R.string.doNotMeetTheCriteria).toString()// "possible"
                    else -> parseStyledString(R.string.na).toString()// "very unlikely"
                }
            }
        }
    }
}

@Composable
fun entryCriterion(): Int {
    val entryCriterion = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = sleEntryCriterion,
        title = R.string.slesEntryCriterion,
        titleNote = R.string.slesEntryCriterionNote,
        defaultSelectedOption = R.string.absent,
        isNumberDisplayed = false
    )
    return entryCriterion
}


@Composable
fun sleTotalScore(): Int {
    val constitutionalScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = constitutional,
        title = R.string.constitutional,
        defaultSelectedOption = R.string.none
    )
    val hematologicScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = hematologic,
        title = R.string.hematologic,
        defaultSelectedOption = R.string.none
    )
    val neuropsychiatricScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = neuropsychiatric,
        title = R.string.neuropsychiatric,
        defaultSelectedOption = R.string.none
    )
    val mucocutaneousScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = mucocutaneus,
        title = R.string.mucocutaneous,
        defaultSelectedOption = R.string.none
    )
    val serosalScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = serosal,
        title = R.string.serosal,
        defaultSelectedOption = R.string.none
    )
    val musculoskeletalScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = musculoskeletal,
        title = R.string.musculoskeletal,
        defaultSelectedOption = R.string.none
    )
    val renalScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = renal,
        title = R.string.renal,
        defaultSelectedOption = R.string.none
    )
    val antiphospholipidScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = antiphospholipidAntibodies,
        title = R.string.antiphospholipid,
        defaultSelectedOption = R.string.none
    )
    val complementScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = complementProtiens,
        title = R.string.complement,
        defaultSelectedOption = R.string.none
    )
    val sleSpecificAntibodiesScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = sleSpecificAntibodies,
        title = R.string.sleSpecificAntibodies,
        defaultSelectedOption = R.string.none
    )

    val totalScore =
        constitutionalScore +
                hematologicScore +
                neuropsychiatricScore +
                mucocutaneousScore +
                serosalScore +
                musculoskeletalScore +
                renalScore +
                antiphospholipidScore +
                complementScore +
                sleSpecificAntibodiesScore

    return totalScore
}

@Preview
@Composable
fun SleScreenPreview() {
    SleScreen(navController = NavController(LocalContext.current))
}