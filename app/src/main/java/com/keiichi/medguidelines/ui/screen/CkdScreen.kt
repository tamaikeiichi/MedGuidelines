package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
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
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.GraphAndThreshold
import com.keiichi.medguidelines.ui.component.GraphAndThresholdSixSegments
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import kotlin.math.roundToInt

data class CkdScores(
    var urineAlbumin: Double,
    var urineCreatinine: Double,
    var urineAlbuminCreatinineRatio: Double,
    var urineTotalProtein: Double,
    var urineTotalProteinCreatinineRatio: Double,
    var gfr: Double,
) {
    fun roundToTwoDecimals() {
        urineAlbumin = roundDouble(urineAlbumin)
        urineCreatinine = roundDouble(urineCreatinine)
        urineAlbuminCreatinineRatio = roundDouble(urineAlbuminCreatinineRatio)
        urineTotalProtein = roundDouble(urineTotalProtein)
        urineTotalProteinCreatinineRatio = roundDouble(urineTotalProteinCreatinineRatio)
        gfr = roundDouble(gfr)
    }

    private fun roundDouble(value: Double): Double {
        return Math.round(value * 100.0) / 100.0
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CkdScreen(
    navController: NavController,
) {
    val references = listOf(
        TextAndUrl(R.string.ckdGuideline, R.string.ckdGuidelineUrl),
    )
    val urineAlbumin = remember { mutableDoubleStateOf(0.00) }
    val urineCreatinine = remember { mutableDoubleStateOf(0.0) }
    //val urineAlbuminCreatinineRatio = remember { mutableDoubleStateOf(0.0) }
    val urineTotalProtein = remember { mutableDoubleStateOf(0.0) }
    val urineTotalProteinCreatinineRatio = remember { mutableDoubleStateOf(0.0) }
    val gfr = remember { mutableDoubleStateOf(100.0) }
    var allCkdScores by remember {
        mutableStateOf(
            CkdScores(
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0
            )
        )
    }
    val calculatedUrineAlbuminCreatinineRatio by remember {
        derivedStateOf {
            if ((urineAlbumin.doubleValue != 0.0) &&
                (urineCreatinine.doubleValue != 0.0)
            ) {
                (
                        calculateUrineAlbuminCreatinineRatio(
                            urineAlbumin.doubleValue,
                            urineCreatinine.doubleValue,
                        )//.roundToInt()
                        )
            } else {
                0.0
            }
        }
    }
    val calculatedUrineTotalProteinCreatinineRatio by remember {
        derivedStateOf {
            if ((urineTotalProtein.doubleValue != 0.0) &&
                (urineCreatinine.doubleValue != 0.0)
            ) {
                (((
                        calculateUrineTotalProteinCreatinineRatio(
                            urineTotalProtein.doubleValue,
                            urineCreatinine.doubleValue,
                        ) * 100.0
                        ).roundToInt()
                        ) / 100.0
                        )
            } else {
                0.0
            }
        }
    }

    var prognosis by remember { mutableStateOf("") }
    var gStage by remember { mutableStateOf("") }
    var aStage by remember { mutableStateOf("") }
    var aStageAlbumin by remember { mutableIntStateOf(0) }
    var aStageTotalProtein by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = calculatedUrineTotalProteinCreatinineRatio) {
        if (calculatedUrineTotalProteinCreatinineRatio != urineTotalProteinCreatinineRatio.doubleValue)
            urineTotalProteinCreatinineRatio.doubleValue =
                calculatedUrineTotalProteinCreatinineRatio
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = buildAnnotatedString {
                    append(stringResource(R.string.ckdGuidelineTitle))
                },
                navController = navController,
                references = references,
            )
        },
        bottomBar = {
            val displayText =
                buildAnnotatedString {
                    append(gStage)
                    append(" ")
                    append(aStage)
                    append("\n")
                    append(prognosis)
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
                allCkdScores = inputAndCalculateCkd(
                    urineAlbumin = urineAlbumin,
                    urineCreatinine = urineCreatinine,
                    urineTotalProtein = urineTotalProtein,
                    gfr = gfr,
                    calculatedUrineAlbuminCreatinineRatio = calculatedUrineAlbuminCreatinineRatio,
                    calculatedUrineTotalProteinCreatinineRatio = calculatedUrineTotalProteinCreatinineRatio
                )
                allCkdScores.roundToTwoDecimals()
                aStageAlbumin = when (allCkdScores.urineAlbuminCreatinineRatio) {
                    in 0.0..<30.0 -> 1
                    in 30.0..<300.0 -> 2
                    in 300.0..<Double.MAX_VALUE -> 3
                    else -> 0
                }
                aStageTotalProtein = when (allCkdScores.urineTotalProteinCreatinineRatio) {
                    in 0.0..<0.15 -> 1
                    in 0.15..<0.5 -> 2
                    in 0.5..<Double.MAX_VALUE -> 3
                    else -> 0
                }
                aStage = when (maxOf(aStageAlbumin, aStageTotalProtein)) {
                    1 -> stringResource(R.string.a1)
                    2 -> stringResource(R.string.a2)
                    3 -> stringResource(R.string.a3)
                    else -> stringResource(R.string.na)
                }
                prognosis = when {
                    (allCkdScores.gfr >= 60.0 &&
                            (aStage == stringResource(R.string.a1))
                            )
                        -> stringResource(R.string.lowRisk)

                    (allCkdScores.gfr >= 45 &&
                            (aStage == stringResource(R.string.a1))
                            )
                        -> stringResource(R.string.moderatelyIncreasedRisk)

                    (allCkdScores.gfr >= 30 &&
                            (aStage == stringResource(R.string.a1))
                            )
                        -> stringResource(R.string.highRisk)

                    (allCkdScores.gfr < 30 &&
                            (aStage == stringResource(R.string.a1))
                            )
                        -> stringResource(R.string.veryHighRisk)

                    (allCkdScores.gfr >= 60 &&
                            (aStage == stringResource(R.string.a2))
                            )
                        -> stringResource(R.string.moderatelyIncreasedRisk)

                    (allCkdScores.gfr >= 45 &&
                            (aStage == stringResource(R.string.a2))
                            )
                        -> stringResource(R.string.highRisk)

                    (allCkdScores.gfr < 45 &&
                            (aStage == stringResource(R.string.a2))
                            )
                        -> stringResource(R.string.veryHighRisk)

                    (allCkdScores.gfr >= 60 &&
                            (aStage == stringResource(R.string.a3))
                            )
                        -> stringResource(R.string.highRisk)

                    (allCkdScores.gfr < 60 &&
                            (aStage == stringResource(R.string.a3))
                            )
                        -> stringResource(R.string.veryHighRisk)

                    else -> {
                        stringResource(R.string.na)
                    }
                }
                gStage = when (allCkdScores.gfr) {
                    in 90.0..Double.MAX_VALUE -> stringResource(R.string.g1)
                    in 60.0..<90.0 -> stringResource(R.string.g2)
                    in 45.0..<60.0 -> stringResource(R.string.g3a)
                    in 30.0..<45.0 -> stringResource(R.string.g3b)
                    in 15.0..<30.0 -> stringResource(R.string.g4)
                    in 0.0..<15.0 -> stringResource(R.string.g5)
                    else -> stringResource(R.string.na)
                }
                MedGuidelinesCard(
                    modifier = Modifier
                        .padding(Dimensions.cardPadding)
                ) {
                    Text(
                        text = stringResource(R.string.persistentAlbuminuriaCategories),
                        modifier = Modifier
                            .padding(Dimensions.textPadding)
                    )
                    GraphAndThreshold(
                        maxValue = 350F,
                        minValue = 0F,
                        firstThreshold = 30F,
                        secondThreshold = 300F,
                        firstLabel = stringResource(R.string.a1),
                        secondLabel = stringResource(R.string.a2),
                        thirdLabel = stringResource(R.string.a3),
                        score = allCkdScores.urineAlbuminCreatinineRatio
                    )
                }
                MedGuidelinesCard(
                    modifier = Modifier
                        .padding(Dimensions.cardPadding)
                ) {
                    Text(
                        text = stringResource(R.string.persistentProteinuriaCategories),
                        modifier = Modifier
                            .padding(Dimensions.textPadding)
                    )
                    GraphAndThreshold(
                        maxValue = 0.7F,
                        minValue = 0F,
                        firstThreshold = 0.15F,
                        secondThreshold = 0.5F,
                        firstLabel = stringResource(R.string.a1),
                        secondLabel = stringResource(R.string.a2),
                        thirdLabel = stringResource(R.string.a3),
                        score = allCkdScores.urineTotalProteinCreatinineRatio
                    )
                }
                MedGuidelinesCard(
                    modifier = Modifier
                        .padding(Dimensions.cardPadding)
                ) {
                    Text(
                        text = stringResource(R.string.gfrCategories),
                        modifier = Modifier
                            .padding(Dimensions.textPadding)
                    )
                    GraphAndThresholdSixSegments(
                        maxValue = 100F,
                        minValue = 0F,
                        firstThreshold = 15F,
                        secondThreshold = 30F,
                        thirdThreshold = 45F,
                        fourthThreshold = 60F,
                        fifthThreshold = 90F,
                        firstLabel = stringResource(R.string.g5),
                        secondLabel = stringResource(R.string.g4),
                        thirdLabel = stringResource(R.string.g3b),
                        fourthLabel = stringResource(R.string.g3a),
                        fifthLabel = stringResource(R.string.g2),
                        sixthLabel = stringResource(R.string.g1),
                        score = allCkdScores.gfr,
                        displayAsInt = true,
                        invertColors = true
                    )
                }
            }
        }
    }
}

fun calculateUrineAlbuminCreatinineRatio(
    urineAlbumin: Double,
    urineCreatinine: Double,
): Double {
    val score = (urineAlbumin / urineCreatinine) * 1000
    return score
}

fun calculateUrineTotalProteinCreatinineRatio(
    urineTotalProtein: Double,
    urineCreatinine: Double,
): Double {
    val score = (urineTotalProtein / urineCreatinine) * 1000 / 1000
    return score
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun inputAndCalculateCkd(
    urineAlbumin: MutableDoubleState,
    urineCreatinine: MutableDoubleState,
    urineTotalProtein: MutableDoubleState,
    gfr: MutableDoubleState,
    calculatedUrineAlbuminCreatinineRatio: Double,
    calculatedUrineTotalProteinCreatinineRatio: Double,
): CkdScores {
    var changedFactor1Unit by remember { mutableStateOf(true) }
    var changedFactor2Unit by remember { mutableStateOf(true) }
    var changedFactor3Unit by remember { mutableStateOf(true) }

    val urineAlbuminCreatinineRatioDisplay = remember { mutableDoubleStateOf(0.0) }
    val urineTotalProteinCreatinineRatioDisplay = remember { mutableDoubleStateOf(0.0) }
    var isUrineAlbuminCreatinineRatioDirectlySet by remember { mutableStateOf(false) }
    var isUrineTotalProteinCreatinineRatioDirectlySet by remember { mutableStateOf(false) }

    LaunchedEffect(urineAlbumin.doubleValue, urineCreatinine.doubleValue) {
        isUrineAlbuminCreatinineRatioDirectlySet = false
    }
    LaunchedEffect(urineTotalProtein.doubleValue, urineCreatinine.doubleValue) {
        isUrineTotalProteinCreatinineRatioDirectlySet = false
    }
    LaunchedEffect(isUrineAlbuminCreatinineRatioDirectlySet) {
        if (!isUrineAlbuminCreatinineRatioDirectlySet) {
            urineAlbuminCreatinineRatioDisplay.doubleValue =
                calculatedUrineAlbuminCreatinineRatio
        } else {
            urineAlbuminCreatinineRatioDisplay.doubleValue =
                urineAlbuminCreatinineRatioDisplay.doubleValue
        }
    }
    LaunchedEffect(isUrineTotalProteinCreatinineRatioDirectlySet) {
        if (!isUrineTotalProteinCreatinineRatioDirectlySet) {
            urineTotalProteinCreatinineRatioDisplay.doubleValue =
                calculatedUrineTotalProteinCreatinineRatio
        } else {
            urineTotalProteinCreatinineRatioDisplay.doubleValue =
                urineTotalProteinCreatinineRatioDisplay.doubleValue
        }
    }
    LaunchedEffect(calculatedUrineAlbuminCreatinineRatio) {
        if (!isUrineAlbuminCreatinineRatioDirectlySet) {
            urineAlbuminCreatinineRatioDisplay.doubleValue =
                calculatedUrineAlbuminCreatinineRatio
        }
    }
    LaunchedEffect(calculatedUrineTotalProteinCreatinineRatio) {
        if (!isUrineTotalProteinCreatinineRatioDirectlySet) {
            urineTotalProteinCreatinineRatioDisplay.doubleValue =
                calculatedUrineTotalProteinCreatinineRatio
        }
    }

    MedGuidelinesCard(
        modifier = Modifier
            .padding(
                Dimensions.cardPadding
            )
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .wrapContentHeight(
                    align = Alignment.Bottom
                ),
            itemVerticalAlignment = Alignment.Bottom,
        ) {
            InputValue(
                label = R.string.urineAlbumin, value = urineAlbumin,
                japaneseUnit = R.string.mgdl,
                isSetByOtherComponent = isUrineAlbuminCreatinineRatioDirectlySet
            )
            InputValue(
                label = R.string.urineCreatinine, value = urineCreatinine,
                japaneseUnit = R.string.mgdl,
                isJapaneseUnit = remember { mutableStateOf(changedFactor1Unit) }.also {
                    changedFactor1Unit = it.value
                },
                changedValueRate = 88.4 / 1000.0,
                changedUnit = R.string.mmolL,
                isSetByOtherComponent = isUrineAlbuminCreatinineRatioDirectlySet || isUrineTotalProteinCreatinineRatioDirectlySet
            )
            InputValue(
                label = R.string.urineAlbuminCreatinineRatio,
                value = urineAlbuminCreatinineRatioDisplay,
                japaneseUnit = R.string.mgg,
                isJapaneseUnit = remember { mutableStateOf(changedFactor2Unit) }.also {
                    changedFactor2Unit = it.value
                },
                changedValueRate = 0.113,
                changedUnit = R.string.mgmmol,
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        isUrineAlbuminCreatinineRatioDirectlySet = true
                    }
                },
                isSetByOtherComponent = !isUrineAlbuminCreatinineRatioDirectlySet
            )
            InputValue(
                label = R.string.urineTotalProtein, value = urineTotalProtein,
                japaneseUnit = R.string.mgdl, //changeUnit = changedFactor2Unit,
                isSetByOtherComponent = isUrineTotalProteinCreatinineRatioDirectlySet
            )
            InputValue(
                label = R.string.urineTotalProteinCreatinineRatio,
                value = urineTotalProteinCreatinineRatioDisplay,
                japaneseUnit = R.string.gg,
                isJapaneseUnit = remember { mutableStateOf(changedFactor3Unit) }.also {
                    changedFactor3Unit = it.value
                },
                changedValueRate = 0.113,
                changedUnit = R.string.mgmmol,
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        isUrineTotalProteinCreatinineRatioDirectlySet = true
                    }
                },
                isSetByOtherComponent = !isUrineTotalProteinCreatinineRatioDirectlySet
            )
            InputValue(
                label = R.string.gfr, value = gfr,
                japaneseUnit = R.string.mlmin173m2, //changeUnit = changedFactor3Unit
            )
        }
    }

    val urineAlbumin = urineAlbumin.doubleValue
    val urineCreatinine = urineCreatinine.doubleValue
    val urineAlbuminCreatinineRatio = urineAlbuminCreatinineRatioDisplay.doubleValue
    val urineTotalProtein = urineTotalProtein.doubleValue
    val urineTotalProteinCreatinineRatio = urineTotalProteinCreatinineRatioDisplay.doubleValue
    val gfr = gfr.doubleValue

    val allScores =
        CkdScores(
            urineAlbumin,
            urineCreatinine,
            urineAlbuminCreatinineRatio,
            urineTotalProtein,
            urineTotalProteinCreatinineRatio,
            gfr,
        )
    //allScores.roundToTwoDecimals()
    return allScores
}

@Preview
@Composable
fun CkdScreenPreview() {
    CkdScreen(navController = NavController(LocalContext.current))
}

