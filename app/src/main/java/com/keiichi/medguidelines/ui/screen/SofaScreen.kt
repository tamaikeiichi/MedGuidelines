package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.sofaCardiovascular
import com.keiichi.medguidelines.data.sofaCentralNervousSystem
import com.keiichi.medguidelines.data.sofaCoagulation
import com.keiichi.medguidelines.data.sofaLiver
import com.keiichi.medguidelines.data.sofaRenal
import com.keiichi.medguidelines.data.sofaRespiration
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed

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
) {
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
            )
        },
        bottomBar = {
            val displayText =
                buildAnnotatedString {
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
                inputAndCalculateSofa(
                    onScoresChanged = {
                        allSofaScores = it
                    }
                )
            }
        }
    }
}


//@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun inputAndCalculateSofa(
    onScoresChanged: (SofaScores) -> Unit
//    calculatedRespiration: MutableDoubleState,
//    calculatedMeanArterialPressure: MutableDoubleState,
) {
    var changedFactor1Unit by remember { mutableStateOf(true) }
    var changedFactor2Unit by remember { mutableStateOf(true) }
    var changedFactor3Unit by remember { mutableStateOf(true) }

//    var respiration by remember { mutableIntStateOf(0) }
//    var coagulation by remember { mutableIntStateOf(0) }
//    var liver by remember { mutableIntStateOf(0) }
//    var cardiovascular by remember { mutableIntStateOf(0) }
//    var centralNervousSystem by remember { mutableIntStateOf(0) }
//    var renal by remember { mutableIntStateOf(0) }


//    MedGuidelinesCard(
//        modifier = Modifier
//            .padding(
//                Dimensions.cardPadding
//            )
//    ) {
//        FlowRow(
//            modifier = Modifier
//                .padding(4.dp)
//                .wrapContentHeight(
//                    align = Alignment.Bottom
//                ),
//            itemVerticalAlignment = Alignment.Bottom,
//        ) {
            val respiration = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaRespiration,
                title = R.string.respiration,
                defaultSelectedOption = sofaRespiration[0].labelResId,
            )

            val coagulation = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaCoagulation,
                title = R.string.coagulation,
                defaultSelectedOption = sofaCoagulation[0].labelResId,
            )
            val liver = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaLiver,
                title = R.string.liver,
                defaultSelectedOption = sofaLiver[0].labelResId,
            )
            val cardiovascular = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaCardiovascular,
                title = R.string.cardiovascular,
                defaultSelectedOption = sofaCardiovascular[0].labelResId,
            )
            val centralNervousSystem = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaCentralNervousSystem,
                title = R.string.centralNervousSystem,
                defaultSelectedOption = sofaCentralNervousSystem[0].labelResId,
            )
            val renal = buttonAndScoreWithScoreDisplayed(
                optionsWithScores = sofaRenal,
                title = R.string.renal,
                defaultSelectedOption = sofaRenal[0].labelResId,
            )
    LaunchedEffect(respiration, coagulation, liver, cardiovascular, centralNervousSystem, renal) {
        onScoresChanged(
            SofaScores(
                respiration = respiration,
                coagulation = coagulation,
                liver = liver,
                cardiovascular = cardiovascular,
                centralNervousSystem = centralNervousSystem,
                renal = renal
            )
        )
    }
//        }
//    }

//    val allScores =
//        SofaScores(
//            respiration,
//            coagulation,
//            liver,
//            cardiovascular,
//            centralNervousSystem,
//            renal
//        )
//    onScoresChanged(allScores)
    //allScores.roundToTwoDecimals()
    //return allScores
}

//@Preview
//@Composable
//fun SofaScreenPreview() {
//    SofaScreen(navController = NavController(LocalContext.current))
//}

//@Preview
//@Composable
//fun SofaScreenPreview2(){
//    inputAndCalculateSofa()
//}

@Preview(showBackground = true, name = "Single Button Component")
@Composable
fun SingleButtonPreview() {
    // This is how you correctly preview the single component.
    // By wrapping it in a Surface, it gets a background and becomes visible.
    Surface {
        val respiration = buttonAndScoreWithScoreDisplayed(
            optionsWithScores = sofaRespiration,
            title = R.string.respiration,
            defaultSelectedOption = sofaRespiration[0].labelResId,
        )
    }
}

@Preview(showBackground = true, name = "Single Button Component")
@Composable
fun SingleButtonPreview2() {
    // This is how you correctly preview the single component.
    // By wrapping it in a Surface, it gets a background and becomes visible.
    Surface {
        val respiration = buttonAndScoreWithScore(
            optionsWithScores = sofaRespiration,
            title = R.string.respiration,
            defaultSelectedOption = sofaRespiration[0].labelResId,
        )
    }
}

@Preview
@Composable
fun SofaScreenPreview4(){
    Text(text = "hello")
}