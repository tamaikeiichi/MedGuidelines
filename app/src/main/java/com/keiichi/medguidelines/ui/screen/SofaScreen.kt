package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
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
    val glasgowComaScaleViewModel by viewModel.gcsScore.collectAsState()

    // Automatically recalculates when GCS changes
    val centralNervousSystemScore by remember(glasgowComaScaleViewModel) {
        derivedStateOf {
            when {
                glasgowComaScaleViewModel == 15.0 -> 0
                glasgowComaScaleViewModel in 13.0..14.0 -> 1
                glasgowComaScaleViewModel in 10.0..12.0 -> 2
                glasgowComaScaleViewModel in 6.0..9.0 -> 3
                glasgowComaScaleViewModel < 6.0 -> 4
                else -> 0 // Default case
            }
        }
    }

    var qSofaScore by remember { mutableIntStateOf(0) }
    val references = listOf(
        TextAndUrl(R.string.sepsis3, R.string.sofaUrl),
    )
    var fio2 by remember {mutableDoubleStateOf(0.21)}
    var pao2 by remember { mutableDoubleStateOf(100.0) }
    val calculatedRespiration by remember {
        derivedStateOf {
            if (fio2 > 0) pao2 / fio2 else 0.0
        }
    }
    var plateletes by remember { mutableDoubleStateOf(200.0) }
    var bilirubin by remember { mutableDoubleStateOf(1.0) }
    var diastolicBp by remember { mutableDoubleStateOf(75.0) }
    var systolicBp by remember { mutableDoubleStateOf(125.0) }
    var meanArterialPressure by remember { mutableDoubleStateOf(80.0) }
    val calculatedMeanArterialPressure by remember {
        derivedStateOf {
            diastolicBp + (systolicBp - diastolicBp)/3.0
        }
    }
    var glasgowComaScale by remember { mutableDoubleStateOf(15.0 ) }
    var creatinine by remember { mutableDoubleStateOf(1.0) }
    var urineOutput by remember { mutableDoubleStateOf(1000.0) }

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

//    LaunchedEffect(key1 = calculatedUrineTotalProteinCreatinineRatio) {
//        if (calculatedUrineTotalProteinCreatinineRatio != urineTotalProteinCreatinineRatio.doubleValue)
//            urineTotalProteinCreatinineRatio.doubleValue =
//                calculatedUrineTotalProteinCreatinineRatio
//    }

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
                        //listOf(
                        //optionsWithScores[0], optionsWithScores[1], optionsWithScores[2]
                    )
                }
                qSofaScore = defaultSelectedOptions.sumOf { it.score }
                CheckboxesAndExpandWithScore(
                    optionsWithScores = optionsWithScores,
                    title = R.string.qSofaCriteria,
                    defaultSelectedOption = defaultSelectedOptions,
                    isNumberDisplayed = false,
                    onOptionSelected = { newSelection ->
                        defaultSelectedOptions = newSelection
                    },
                )
                allSofaScores = inputAndCalculateSofa(
                    navController = navController,
                    centralNervousSystemScore = centralNervousSystemScore
                )
            }
        }
    }
}


//@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun inputAndCalculateSofa(
    navController: NavController,
    centralNervousSystemScore: Int
)
: SofaScores
{
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
                }
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
    return allScores
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