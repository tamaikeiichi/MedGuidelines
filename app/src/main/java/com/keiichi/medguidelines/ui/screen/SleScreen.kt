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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.compose.secondaryDark
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
import com.keiichi.medguidelines.ui.component.TextAndExpand2Levels
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.parseStyledString
import org.apache.commons.math3.stat.interval.WilsonScoreInterval

/** 2019 EULAR/ACR: 合計点と，臨床項目（全身・血液・神経精神・皮膚粘膜・漿膜・筋骨格・腎）を満たすか */
data class SleScore(val total: Int, val hasClinicalCriterion: Boolean)

@Composable
fun SleScreen(navController: NavController) {
    var sleScore by remember { mutableStateOf(SleScore(0, false)) }
    var entryCriterionScore by remember { mutableIntStateOf(0) }

    // This is now a simple derived state. No need for its own `remember`.
    var displayScore by remember (entryCriterionScore, sleScore) { mutableStateOf(0) }
    displayScore = if (entryCriterionScore > 0) sleScore.total else 0
    var diagnosis by remember { mutableStateOf("") }
    // SLEと分類するには ≥10点 かつ 臨床項目を1つ以上満たすことが必要
    diagnosis = when {
        displayScore >= 10 && sleScore.hasClinicalCriterion ->
            parseStyledString(R.string.classifyAsSle).toString()
        displayScore >= 10 ->
            parseStyledString(R.string.sleClinicalCriterionRequired).toString()
        displayScore >= 0 -> parseStyledString(R.string.doNotMeetTheCriteria).toString()
        else -> parseStyledString(R.string.na).toString()
    }
    //var displayText by remember { mutableStateOf(AnnotatedString("")) }
    val displayText =
        buildAnnotatedString {
            append("Score: ")
            append(displayScore.toString())
            append(" (")
            append(diagnosis)
            append(")")
        }
    //var displayScore by remember { mutableIntStateOf(value = 0) }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = parseStyledString(R.string.sleTitle),
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.sleRefTitle, R.string.sleUrl),
                    TextAndUrl(R.string.slesJapRefTitle, R.string.sleJapsUrl),
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
            entryCriterionScore = entryCriterion()
            if (entryCriterionScore > 0) {
                sleScore = sleTotalScore()
                //Text(text = totalScore.toString())
            }
//        ScoreBottomAppBarVariable(
//            displayText = displayText, // It receives the final, pre-calculated text
//        )
    }
    }
}

@Composable
private fun entryCriterion(): Int {
    val entryCriterion = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = sleEntryCriterion,
        title = R.string.slesEntryCriterion,
        titleNote = R.string.slesEntryCriterionNote,
        defaultSelectedOption = R.string.eightyOrMore,
        isNumberDisplayed = false
    )
    return entryCriterion
}


@Composable
fun sleTotalScore(): SleScore {
    TextAndExpand2Levels(
        firstTitle = R.string.additiveCriteria,
        secondTitle = R.string.secondCommentSle,
    )
    val constitutionalScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = constitutional,
        title = R.string.constitutional,
        titleNote = R.string.constitutionalNote,
        defaultSelectedOption = R.string.none
    )
    val hematologicScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = hematologic,
        title = R.string.hematologic,
        titleNote = R.string.hematologicNote,
        defaultSelectedOption = R.string.none
    )
    val neuropsychiatricScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = neuropsychiatric,
        title = R.string.neuropsychiatric,
        titleNote = R.string.neuropsychiatricNote,
        defaultSelectedOption = R.string.none
    )
    val mucocutaneousScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = mucocutaneus,
        title = R.string.mucocutaneous,
        titleNote = R.string.mucocutaneousNote,
        defaultSelectedOption = R.string.none
    )
    val serosalScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = serosal,
        title = R.string.serosal,
        titleNote = R.string.serosalNote,
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
        titleNote = R.string.renalNote,
        defaultSelectedOption = R.string.none
    )
    val antiphospholipidScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = antiphospholipidAntibodies,
        title = R.string.antiphospholipid,
        titleNote = R.string.antiphospholipidNote,
        defaultSelectedOption = R.string.none
    )
    val complementScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = complementProtiens,
        title = R.string.complement,
        titleNote = R.string.complementNote,
        defaultSelectedOption = R.string.none
    )
    val sleSpecificAntibodiesScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = sleSpecificAntibodies,
        title = R.string.sleSpecificAntibodies,
        titleNote = R.string.sleSpecificAntibodiesNote,
        defaultSelectedOption = R.string.none
    )

    val clinicalScore =
        constitutionalScore +
                hematologicScore +
                neuropsychiatricScore +
                mucocutaneousScore +
                serosalScore +
                musculoskeletalScore +
                renalScore
    val immunologicScore =
        antiphospholipidScore +
                complementScore +
                sleSpecificAntibodiesScore

    return SleScore(
        total = clinicalScore + immunologicScore,
        hasClinicalCriterion = clinicalScore > 0
    )
}

@Preview
@Composable
fun SleScreenPreview() {
   MedGuidelinesTheme {
        SleScreen(navController = NavController(LocalContext.current))
    }

}