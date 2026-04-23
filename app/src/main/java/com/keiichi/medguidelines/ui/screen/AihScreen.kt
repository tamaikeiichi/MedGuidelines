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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.LabelAndScore
import com.keiichi.medguidelines.data.aihEntryCriterion
import com.keiichi.medguidelines.data.raEntryCriterion
import com.keiichi.medguidelines.data.sleEntryCriterion
import com.keiichi.medguidelines.ui.component.CheckboxesAndExpandWithScore
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun AihScreen(navController: NavController){
    var entryCriterionScore by remember { mutableIntStateOf(0) }
    var displayScore by remember (entryCriterionScore, totalScore) { mutableStateOf(0) }
    displayScore = if (entryCriterionScore > 0) totalScore else 0
    var diagnosis by remember { mutableStateOf("") }
    diagnosis =
        if (entryCriterionScore == -1){
            when (displayScore) {
                in 10..100 -> parseStyledString(R.string.classifyAsSle).toString()
                //"established"
                in 0..9 -> parseStyledString(R.string.doNotMeetTheCriteria).toString()// "possible"
                else -> parseStyledString(R.string.na).toString()// "very unlikely"

        } else {
                when (displayScore) {
                    in 10..100 -> parseStyledString(R.string.classifyAsSle).toString()
                    //"established"
                    in 0..9 -> parseStyledString(R.string.doNotMeetTheCriteria).toString()// "possible"
                    else -> parseStyledString(R.string.na).toString()// "very unlikely"

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
    ){
        innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            entryCriterionScore = entryCriterion()
            if (entryCriterionScore > 0) {
                totalScore = sleTotalScore() // Assuming childPughTotalScore() returns an Int
                //Text(text = totalScore.toString())
            }
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