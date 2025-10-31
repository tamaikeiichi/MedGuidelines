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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.albuminGrade
import com.keiichi.medguidelines.data.ascitesGrade
import com.keiichi.medguidelines.data.bilirubinGrade
import com.keiichi.medguidelines.data.coombsNegativeHemolyticAnemia
import com.keiichi.medguidelines.data.encephalopathyGrade
import com.keiichi.medguidelines.data.kayserFleischerRings
import com.keiichi.medguidelines.data.liverCopper
import com.keiichi.medguidelines.data.mutationAnalysis
import com.keiichi.medguidelines.data.neurologicSymptoms
import com.keiichi.medguidelines.data.ptGrade
import com.keiichi.medguidelines.data.serumCeruloplasmin
import com.keiichi.medguidelines.data.urinaryCopper
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.parseStyledString
import org.apache.commons.math3.stat.interval.WilsonScoreInterval

@Composable
fun WilsonScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var diagnosis by remember { mutableStateOf("") }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.wilsonTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.wilsonRefTitle, R.string.wilsonUrl)
                )
            )
        },
        bottomBar = {
            ResultBottomAppBar {
                Text(
                    text = "Score: $totalScore ($diagnosis)",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = wilsonTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            diagnosis = when (totalScore) {
                in 4..20 -> parseStyledString(R.string.established).toString()
                //"established"
                in 3..3 -> parseStyledString(R.string.possible).toString()// "possible"
                else -> parseStyledString(R.string.veryUnlikely).toString()// "very unlikely"
            }
        }
    }
}

@Composable
fun wilsonTotalScore(): Int {
    val  KayserFleischerRingsScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = kayserFleischerRings,
        title = R.string.KFrings,
        titleNote = R.string.kayserFleischerRingsNote,
        defaultSelectedOption =  R.string.absent
    )
    val neurologicScore =
        buttonAndScoreWithScoreDisplayed(
            neurologicSymptoms,
            R.string.neurologicSymptoms,
            R.string.neurologicSymptomsNote,
            defaultSelectedOption =  R.string.absent
        )
    val ceruloplasminScore = buttonAndScoreWithScoreDisplayed(
        serumCeruloplasmin,
        R.string.serumCeruloplasmin,
        R.string.space
    )
    val coombsScore =
        buttonAndScoreWithScoreDisplayed(
            coombsNegativeHemolyticAnemia,
            R.string.coombsNegativeHymolyticAnemia,
            R.string.space,
            defaultSelectedOption =  R.string.absent
        )
    val liveCopperScore = buttonAndScoreWithScoreDisplayed(
        liverCopper,
        R.string.liverCopper,
        R.string.liverCopperNote,
        defaultSelectedOption =  R.string.normal08
    )
    val urinaryCopperScore = buttonAndScoreWithScoreDisplayed(
        urinaryCopper,
        R.string.urinaryCopper,
        R.string.urinaryCopperNote
    )
    val mutationScore = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = mutationAnalysis,
        title = R.string.mutationAnalysis,
        titleNote = R.string.atp7bMutation,
        defaultSelectedOption =  R.string.noMutation
    )
    val totalScore =
        KayserFleischerRingsScore +
                neurologicScore +
                ceruloplasminScore +
                coombsScore +
                liveCopperScore +
                urinaryCopperScore +
                mutationScore

    return totalScore
}

@Preview
@Composable
fun WilsonScorePreview() {
    WilsonScreen(navController = NavController(LocalContext.current))
}