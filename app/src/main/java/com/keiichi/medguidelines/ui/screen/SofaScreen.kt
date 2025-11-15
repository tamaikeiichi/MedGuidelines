package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.LabelAndScore
import com.keiichi.medguidelines.data.qSofa
import com.keiichi.medguidelines.data.sofaCardiovascular
import com.keiichi.medguidelines.data.sofaCentralNervousSystem
import com.keiichi.medguidelines.data.sofaCoagulation
import com.keiichi.medguidelines.data.sofaLiver
import com.keiichi.medguidelines.data.sofaRenal
import com.keiichi.medguidelines.data.sofaRespiration
import com.keiichi.medguidelines.ui.component.CheckboxesAndExpandWithScore
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.viewModel.SofaViewModel

data class SofaScores(
    val respiration: Int,
    val coagulation: Int,
    val liver: Int,
    val cardiovascular: Int,
    val centralNervousSystem: Int,
    val renal: Int,
) {}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SofaScreen(
    navController: NavController,
    viewModel: SofaViewModel,
) {
    // Collect the GCS score from the ViewModel as state
//    val glasgowComaScaleViewModel by viewModel.gcsComponents.collectAsState()
    val gcsComponents by viewModel.gcsComponents.collectAsState()

    val centralNervousSystemScore by remember(gcsComponents) {
        derivedStateOf {
            val totalGcsScore = gcsComponents.e + gcsComponents.v + gcsComponents.m

            when (totalGcsScore) {
                15 -> 0
                in 13..14 -> 1
                in 10..12 -> 2
                in 6..9 -> 3
                else -> 4 // Covers scores less than 6
            }
        }
    }

    var qSofaScore by remember { mutableIntStateOf(0) }
    val references = listOf(
        TextAndUrl(R.string.sepsis3, R.string.sofaUrl),
    )

    var allSofaScores by remember {
        mutableStateOf(
            SofaScores(
                0,
                0,
                0,
                0,
                0,
                0
            )
        )
    }
    val sofaScoresSum by remember {
        derivedStateOf {
            allSofaScores.respiration +
                    allSofaScores.coagulation +
                    allSofaScores.liver +
                    allSofaScores.cardiovascular +
                    allSofaScores.centralNervousSystem +
                    allSofaScores.renal
        }
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = buildAnnotatedString {
                    append(stringResource(R.string.sofaTitle))
                },
                navController = navController,
                references = references,
                helpTitleResId = R.string.sofaAndQSofa,
                helpMessageResId = R.string.sofaAndQSofaInfo
            )
        },
        bottomBar = {
            val displayText =
                buildAnnotatedString {
                    append(stringResource(R.string.qSofaScore))
                    append(" ")
                    append(qSofaScore.toString())
                    append("\n")
                    append(stringResource(R.string.sofaScore))
                    append(" ")
                    append(sofaScoresSum.toString())
                }
            ScoreBottomAppBarVariable(
                displayText = displayText
            )
        },
        modifier = Modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(Dimensions.lazyColumnPadding),
            state = rememberLazyListState()
        ) {
            item {
                val optionsWithScores = qSofa
                var defaultSelectedOptions by remember {
                    mutableStateOf(
                        emptyList<LabelAndScore>()
                    )
                }
                qSofaScore = defaultSelectedOptions.sumOf { it.score } // Parent calculates the score// In Component Signature

                CheckboxesAndExpandWithScore(
                    optionsWithScores = optionsWithScores,
                    title = R.string.qSofaCriteria,
                    defaultSelectedOption = defaultSelectedOptions,
                    isNumberDisplayed = false,
                    onOptionSelected = { newSelection ->
                        defaultSelectedOptions = newSelection
                    },
                )
                inputAndCalculateSofa(
                    navController = navController,
                    centralNervousSystemScore = centralNervousSystemScore,
                    onScoresChanged = { newScores ->
                        allSofaScores = newScores // Update the state in the parent
                    },
                )
            }
        }
    }
}


@Composable
private fun inputAndCalculateSofa(
    navController: NavController,
    centralNervousSystemScore: Int,
    onScoresChanged: (SofaScores) -> Unit,
) {
            val respiration = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaRespiration,
                title = R.string.sofaRespiration,
                defaultSelectedOption = sofaRespiration[0].labelResId,
            )

            val coagulation = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaCoagulation,
                title = R.string.sofaCoagulation,
                defaultSelectedOption = sofaCoagulation[0].labelResId,
            )
            val liver = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaLiver,
                title = R.string.sofaLiver,
                defaultSelectedOption = sofaLiver[0].labelResId,
            )
            val cardiovascular = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaCardiovascular,
                title = R.string.sofaCardiovascular,
                titleNote = R.string.map,
                defaultSelectedOption = sofaCardiovascular[0].labelResId,
            )
            val centralNervousSystem = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaCentralNervousSystem,
                title = R.string.sofaCentralNervousSystem,
                defaultSelectedOption = sofaCentralNervousSystem.find { it.score == centralNervousSystemScore }?.labelResId
                    ?: sofaCentralNervousSystem[0].labelResId, // Fallback to the first option
                onTitleClick = {
                    navController.navigate("GlasgowComaScaleScreen") // Your navigation route
                },
                //titleNote = R.string.map
            )
            val renal = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaRenal,
                title = R.string.sofaRenal,
                defaultSelectedOption = sofaRenal[0].labelResId,
            )

    val allScores =
        SofaScores(
            respiration,
            coagulation,
            liver,
            cardiovascular,
            centralNervousSystem,
            renal
        )
    LaunchedEffect(allScores) {
        onScoresChanged(allScores)
    }
    //return allScores
}

@Preview
@Composable
fun SofaScreenPreview() {
    SofaScreen(
        navController = NavController(LocalContext.current),
        viewModel = SofaViewModel())
}

//@Preview
//@Composable
//fun SofaScreenPreview2(){
//    inputAndCalculateSofa()
//}

//@Preview(showBackground = true, name = "Single Button Component")
//@Composable
//fun SingleButtonPreview() {
//    // This is how you correctly preview the single component.
//    // By wrapping it in a Surface, it gets a background and becomes visible.
//    Surface {
//        val respiration = buttonAndScoreWithScoreDisplayed(
//            optionsWithScores = sofaRespiration,
//            title = R.string.respiration,
//            defaultSelectedOption = sofaRespiration[0].labelResId,
//        )
//    }
//}

//@Preview(showBackground = true, name = "Single Button Component")
//@Composable
//fun SingleButtonPreview2() {
//    // This is how you correctly preview the single component.
//    // By wrapping it in a Surface, it gets a background and becomes visible.
//    Surface {
//        val respiration = buttonAndScoreWithScore(
//            optionsWithScores = sofaRespiration,
//            title = R.string.respiration,
//            defaultSelectedOption = sofaRespiration[0].labelResId,
//        )
//    }
//}

//@Preview
//@Composable
//fun SofaScreenPreview4(){
//    Text(text = "hello")
//}