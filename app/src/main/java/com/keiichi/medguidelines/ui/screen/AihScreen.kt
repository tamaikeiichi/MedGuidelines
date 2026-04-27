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
import com.keiichi.medguidelines.data.aihEntryCriterion
import com.keiichi.medguidelines.data.aihSex
import com.keiichi.medguidelines.data.alpAstRatio
import com.keiichi.medguidelines.data.ama
import com.keiichi.medguidelines.data.ana
import com.keiichi.medguidelines.data.averageAlcoholIntake
import com.keiichi.medguidelines.data.drugHistory
import com.keiichi.medguidelines.data.hla
import com.keiichi.medguidelines.data.igg
import com.keiichi.medguidelines.data.liverHistology
import com.keiichi.medguidelines.data.otherAutoimmuneDisease
import com.keiichi.medguidelines.data.responseToTherapy
import com.keiichi.medguidelines.data.seropositivity
import com.keiichi.medguidelines.data.viralMarker
import com.keiichi.medguidelines.ui.component.CheckboxesAndExpandWithScore
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun AihScreen(navController: NavController){
    var totalScore by remember { mutableIntStateOf(0) }

    var entryCriterionScore by remember { mutableIntStateOf(0) }
    val displayScore = totalScore
    val diagnosis =
        if (entryCriterionScore == -1) {
            when (displayScore) {
                in 16..100 -> parseStyledString(R.string.definiteAih).toString()
                in 10..15 -> parseStyledString(R.string.probableAih)
                else -> parseStyledString(R.string.belowCriteria).toString()
            }
        } else {
            when (displayScore) {
                in 18..100 -> parseStyledString(R.string.definiteAih).toString()
                in 12..17 -> parseStyledString(R.string.probableAih).toString()
                else -> parseStyledString(R.string.belowCriteria).toString()
            }
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
                title = parseStyledString(R.string.aihScoringSystem),
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.aihRefTitle, R.string.aihUrl),
                    TextAndUrl(R.string.aihRefTitle, R.string.sleJapsUrl),
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(
                displayText = displayText,
            )
        }
    ){
        innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            entryCriterionScore = entryCriterion()
            totalScore = aihTotalScore()

        }
    }
}

@Composable
private fun entryCriterion(): Int {
     //val score = defaultSelectedOptions.sumOf { it.score } // Parent calculates the score// In Component Signature

    val score = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = aihEntryCriterion,
        title = R.string.interpretationOfAggregateScores,
        //titleNote = R.string.raEntryCriterionNote,
        //defaultSelectedOption = R.string.atLeastOneJoint,
        isNumberDisplayed = false
    )
    return score
}

@Composable
fun aihTotalScore(): Int{
    val sex = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = aihSex,
        title = R.string.sex,

    )
    val alpAstRatio = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = alpAstRatio,
        title = R.string.alpAstRatio,
        titleNote = R.string.alpAstRatioNote
    )
    val igg = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = igg,
        title = R.string.serumGliblinsOrIgGAboveNormal
    )
    val ana = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = ana,
        title = R.string.anaSmaLkm1,
        titleNote = R.string.anaSmaLkm1Note

    )
    val ama = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = ama,
        title= R.string.ama
    )
    val viralMarker = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = viralMarker,
        title = R.string.hepatitisViralMarker,
        titleNote =  R.string.hepatitisViralMarkerNote
    )
    val drug = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = drugHistory,
        title = R.string.drugHistory,
        titleNote = R.string.drugHistoryNote
    )
    val alcohol = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = averageAlcoholIntake,
        title = R.string.averageAlcoholIntake
    )
    val optionsWithScores = liverHistology
    var defaultSelectedOptions by remember {
        mutableStateOf(listOf<LabelAndScore>())
    }
    val histology = defaultSelectedOptions.sumOf { it.score }
    CheckboxesAndExpandWithScore(

        optionsWithScores = liverHistology,
        defaultSelectedOption = defaultSelectedOptions,
        onOptionSelected = { newSelection ->
            defaultSelectedOptions = newSelection
        },
        title = R.string.liverHistology,
        //titleNote = R.string.biliaryChangeNote,
        isNumberDisplayed = true
    )
    val otherAutoimune = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = otherAutoimmuneDisease,
        title = R.string.otherAutoimmuneDisease,
        titleNote = R.string.otherAutoimmuneDiseaseNote
    )
    var seropositivityScore: Int = 0
    var hlaScore: Int = 0
    if (ana == 0) {
        seropositivityScore = buttonAndScoreWithScoreDisplayed(
            optionsWithScores = seropositivity,
            title = R.string.seropositivityForOther,
            titleNote = R.string.seropositivityForOtherNote
        )
        hlaScore = buttonAndScoreWithScoreDisplayed(
            optionsWithScores = hla,
            title = R.string.hla,
            titleNote = R.string.hlaNote
        )
    }
    val response  = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = responseToTherapy,
        title= R.string.responseToTherapy,
        titleNote = R.string.responseToTherapyNote
    )

    val totalScore = sex + alpAstRatio + igg + ana + ama + viralMarker +
            drug + alcohol + histology + otherAutoimune + seropositivityScore +
            hlaScore + response
    return totalScore
}

@Preview
@Composable
fun AihScreenPreview() {
    MedGuidelinesTheme {
        AihScreen(navController = NavController(LocalContext.current))
    }

}