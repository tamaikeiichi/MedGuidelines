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
import com.keiichi.medguidelines.data.igg
import com.keiichi.medguidelines.data.jointInvolvement
import com.keiichi.medguidelines.data.liverHistology
import com.keiichi.medguidelines.data.otherAutoimmuneDisease
import com.keiichi.medguidelines.data.raEntryCriterion
import com.keiichi.medguidelines.data.seropositivity
import com.keiichi.medguidelines.data.sleEntryCriterion
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
    var displayScore by remember (entryCriterionScore, totalScore) { mutableStateOf(0) }
    //displayScore = if (entryCriterionScore > 0) totalScore else 0
    var diagnosis by remember { mutableStateOf("") }
    diagnosis =
        if (entryCriterionScore == -1){
            when (displayScore) {
                in 16..100 -> parseStyledString(R.string.definiteAih).toString()
                //"established"
                else -> parseStyledString(R.string.probableAih).toString()// "possible"
            }
        } else {
                when (displayScore) {
                    in 18..100 -> parseStyledString(R.string.definiteAih).toString()
                    //"established"
                    else -> parseStyledString(R.string.probableAih).toString()// "possible"
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
            if (entryCriterionScore > 0) {
                //totalScore = sleTotalScore() // Assuming childPughTotalScore() returns an Int
                //Text(text = totalScore.toString())
            }
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
        title = R.string.alpAstRatio
    )
    val igg = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = igg,
        title = R.string.serumGliblinsOrIgGAboveNormal
    )
    val ana = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = ana,
        title = R.string.anaSmaLkm1
    )
    val ama = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = ama,
        title= R.string.ama
    )
    val viralMarker = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = viralMarker,
        title = R.string.hepatitisViralMarker
    )
    val drug = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = drugHistory,
        title = R.string.drugHistory
    )
    val alcohol = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = averageAlcoholIntake,
        title = R.string.averageAlcoholIntake
    )
    val histology = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = liverHistology,
        title = R.string.liverHistology
    )
    val otherAutoimune = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = otherAutoimmuneDisease,
        title = R.string.otherAutoimmuneDisease
    )
    val seropositivity = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = seropositivity,
        title = R.string.seropositivityForOther
    )

    val totalScore =
        0
    return totalScore
}

@Preview
@Composable
fun AihScreenPreview() {
    MedGuidelinesTheme {
        AihScreen(navController = NavController(LocalContext.current))
    }

}