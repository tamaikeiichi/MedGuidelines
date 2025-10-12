package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
    val gfr = remember { mutableDoubleStateOf(0.0) }
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

//    LaunchedEffect(key1 = calculatedUrineAlbuminCreatinineRatio) {
//        if (calculatedUrineAlbuminCreatinineRatio != urineAlbuminCreatinineRatio.doubleValue)
//            urineAlbuminCreatinineRatio.doubleValue =
//                calculatedUrineAlbuminCreatinineRatio.toDouble()
//    }

    LaunchedEffect(key1 = calculatedUrineTotalProteinCreatinineRatio) {
        if (calculatedUrineTotalProteinCreatinineRatio != urineTotalProteinCreatinineRatio.doubleValue)
            urineTotalProteinCreatinineRatio.doubleValue =
                calculatedUrineTotalProteinCreatinineRatio
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                displayText = buildAnnotatedString {
                    append(stringResource(R.string.ckdGuidelineTitle))
                },
                navController = navController,
                references = references,
            )
        },
        bottomBar = {
            val displayText =
                buildAnnotatedString {
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
                    urineAlbuminCreatinineRatio = calculatedUrineAlbuminCreatinineRatio,
                    urineTotalProtein = urineTotalProtein,
                    urineTotalProteinCreatinineRatio = urineTotalProteinCreatinineRatio,
                    gfr = gfr
                )
                allCkdScores.roundToTwoDecimals()
                prognosis = when {
                    (allCkdScores.gfr >= 60.0 &&
                            allCkdScores.urineAlbuminCreatinineRatio < 30.0)
                        -> stringResource(R.string.lowRisk)

                    (allCkdScores.gfr >= 45 &&
                            allCkdScores.urineAlbuminCreatinineRatio < 30)
                        -> stringResource(R.string.moderatelyIncreasedRisk)

                    (allCkdScores.gfr >= 30 &&
                            allCkdScores.urineAlbuminCreatinineRatio < 30)
                        -> stringResource(R.string.highRisk)

                    (allCkdScores.gfr < 30 &&
                            allCkdScores.urineAlbuminCreatinineRatio < 30)
                        -> stringResource(R.string.veryHighRisk)

                    (allCkdScores.gfr >= 60 &&
                            allCkdScores.urineAlbuminCreatinineRatio <= 300)
                        -> stringResource(R.string.moderatelyIncreasedRisk)

                    (allCkdScores.gfr >= 45 &&
                            allCkdScores.urineAlbuminCreatinineRatio <= 300)
                        -> stringResource(R.string.highRisk)

                    (allCkdScores.gfr < 45 &&
                            allCkdScores.urineAlbuminCreatinineRatio <= 300)
                        -> stringResource(R.string.veryHighRisk)

                    (allCkdScores.gfr >= 60 &&
                            allCkdScores.urineAlbuminCreatinineRatio > 300)
                        -> stringResource(R.string.highRisk)

                    (allCkdScores.gfr < 60 &&
                            allCkdScores.urineAlbuminCreatinineRatio > 300)
                        -> stringResource(R.string.veryHighRisk)

                    else -> {
                        stringResource(R.string.na)
                    }
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
    urineAlbuminCreatinineRatio: Double,
    urineTotalProtein: MutableDoubleState,
    urineTotalProteinCreatinineRatio: MutableDoubleState,
    gfr: MutableDoubleState,
): CkdScores {
    var changedFactor1Unit by remember { mutableStateOf(true) }
    var changedFactor2Unit by remember { mutableStateOf(true) }
    var changedFactor3Unit by remember { mutableStateOf(true) }

    val ratioDisplayState = remember(urineAlbuminCreatinineRatio) {
        mutableDoubleStateOf(urineAlbuminCreatinineRatio)
    }

    // 2. State to track if the user has manually edited the ratio field.
    var isRatioManuallySet by remember { mutableStateOf(false) }

    // --- Logic to decide what to display ---
    // If NOT manually set, always show the latest calculated value.
    if (!isRatioManuallySet) {
        ratioDisplayState.doubleValue = urineAlbuminCreatinineRatio
    }

    LaunchedEffect(urineAlbumin.doubleValue, urineCreatinine.doubleValue) {
        isRatioManuallySet = false
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
            )
            InputValue(
                label = R.string.urineCreatinine, value = urineCreatinine,
                japaneseUnit = R.string.mgdl,
                isJapaneseUnit = remember { mutableStateOf(changedFactor1Unit) }.also {
                    changedFactor1Unit = it.value
                },
                changedValueRate = 88.4,
                changedUnit = R.string.umolL
            )
            // Create a temporary state that holds the calculated value

            // Update it whenever the calculated value changes
            ratioDisplayState.doubleValue = urineAlbuminCreatinineRatio

            InputValue(
                label = R.string.urineAlbuminCreatinineRatio,
                value = ratioDisplayState,
                japaneseUnit = R.string.mgg,
                isJapaneseUnit = remember { mutableStateOf(changedFactor2Unit) }.also {
                    changedFactor2Unit = it.value
                },
                changedValueRate = 0.113,
                changedUnit = R.string.mgmmol,
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        isRatioManuallySet = true
                    }
                }
            )
            InputValue(
                label = R.string.urineTotalProtein, value = urineTotalProtein,
                japaneseUnit = R.string.mgdl, //changeUnit = changedFactor2Unit
            )
            InputValue(
                label = R.string.urineTotalProteinCreatinineRatio,
                value = urineTotalProteinCreatinineRatio,
                japaneseUnit = R.string.gg,
                isJapaneseUnit = remember { mutableStateOf(changedFactor3Unit) }.also {
                    changedFactor3Unit = it.value
                },
                changedValueRate = 0.113,
                changedUnit = R.string.mgmmol
            )
            InputValue(
                label = R.string.gfr, value = gfr,
                japaneseUnit = R.string.mlmin173m2, //changeUnit = changedFactor3Unit
            )
        }
    }

    val urineAlbumin = urineAlbumin.doubleValue
    val urineCreatinine = urineCreatinine.doubleValue
    val urineAlbuminCreatinineRatio = ratioDisplayState.doubleValue
    val urineTotalProtein = urineTotalProtein.doubleValue
    val urineTotalProteinCreatinineRatio = urineTotalProteinCreatinineRatio.doubleValue
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

