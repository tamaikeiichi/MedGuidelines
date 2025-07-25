package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.FactorAlerts
import com.keiichi.medguidelines.ui.component.GraphAndThreshold
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.cardModifier
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

val references = listOf(
    TextAndUrl(R.string.fib4, R.string.fib4Url),
    TextAndUrl(R.string.apri, R.string.apriUrl),
    TextAndUrl(R.string.shearWaveElastography, R.string.shearWaveElastographyUrl),
    TextAndUrl(R.string.nafldFibrosisScore, R.string.nafldFibrosisScoreUrl),
    TextAndUrl(R.string.elfScore, R.string.elfScoreUrl),
    TextAndUrl(R.string.m2bpgi, R.string.m2bpgiUrl),
    TextAndUrl(R.string.caIndex, R.string.caIndexUrl),
)

data class Scores(
    var fib4score: Double,
    var apri: Double,
    var swe: Double,
    var nfs: Double,
    var elfScore: Double,
    var m2bpgi: Double,
    var caIndex: Double
) {
    fun roundToTwoDecimals() {
        fib4score = roundDouble(fib4score)
        apri = roundDouble(apri)
        swe = roundDouble(swe)
        nfs = roundDouble(nfs)
        elfScore = roundDouble(elfScore)
    }
    private fun roundDouble(value: Double): Double {
        return Math.round(value * 100.0) / 100.0
    }
}

fun Modifier.textModifier(): Modifier =
    this.padding(5.dp)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiverFibrosisScoreSystemScreen(
    navController: NavController,
) {
    val focusManager = LocalFocusManager.current
    val age = remember { mutableDoubleStateOf(0.00) }
    val ast = remember { mutableDoubleStateOf(0.0) }
    val platelet = remember { mutableDoubleStateOf(0.0) }
    val alt = remember { mutableDoubleStateOf(0.0) }
    val swe = remember { mutableDoubleStateOf(0.0) }
    val bodyHeight = remember { mutableDoubleStateOf(0.0) }
    val bodyWeight = remember { mutableDoubleStateOf(0.0) }
    val dmPresence = remember { mutableIntStateOf(0) }
    val albumin = remember { mutableDoubleStateOf(0.0) }
    val hyaluronicAcid = remember { mutableDoubleStateOf(0.0) }
    val piiinp = remember { mutableDoubleStateOf(0.0) }
    val timp1 = remember { mutableDoubleStateOf(0.0) }
    val elfScore = remember { mutableDoubleStateOf(0.0) }
    val m2bpgi = remember { mutableDoubleStateOf(0.0) }
    val t4c7score = remember { mutableDoubleStateOf(0.0) }
    var allScores by remember {
        mutableStateOf(
            Scores(
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
            )
        )
    }
    val calculatedElfScore by remember {
        derivedStateOf {
            if ((hyaluronicAcid.doubleValue != 0.0) &&
                (piiinp.doubleValue != 0.0) &&
                (timp1.doubleValue != 0.0)
            ) {
                (((
                        calculateElfScore(
                            hyaluronicAcid.doubleValue,
                            piiinp.doubleValue,
                            timp1.doubleValue
                        ) * 100.0
                        ).roundToInt()
                        ) / 100.0
                        )
            } else {
                0.0
            }
        }
    }
    LaunchedEffect(key1 = calculatedElfScore) {
        if (calculatedElfScore != elfScore.doubleValue)
            elfScore.doubleValue = calculatedElfScore
    }
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.liverFibrosisScoreSystemTitle,
                navController = navController,
                references = references,
            )
        },
        modifier = Modifier
            .pointerInput(Unit) { // Use pointerInput with detectTapGestures
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus() // Clear focus on tap outside
                    }
                )
            },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(Dimensions.lazyColumnPadding),
            state = rememberLazyListState()
        ) {
            item {
                allScores = inputAndCalculate(
                    age = age,
                    ast = ast,
                    platelet = platelet,
                    alt = alt,
                    swe = swe,
                    bodyHeight = bodyHeight,
                    bodyWeight = bodyWeight,
                    dmPresence = dmPresence,
                    albumin = albumin,
                    hyaluronicAcid = hyaluronicAcid,
                    piiinp = piiinp,
                    timp1 = timp1,
                    elfScore = elfScore,
                    m2bpgi = m2bpgi,
                    t4c7score = t4c7score,
                    //calculatedElfScore = calculatedElfScore
                )
                allScores.roundToTwoDecimals()
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.fib4),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 5F,
                        minValue = 0.1F,
                        firstThreshold = 1.3F,
                        secondThreshold = 2.67F,
                        firstLabel = stringResource(R.string.lowRisk),
                        secondLabel = stringResource(R.string.intermediateRisk),
                        thirdLabel = stringResource(R.string.highRisk),
                        score = allScores.fib4score
                    )
                    Row() {
                        FactorAlerts(
                            text = R.string.age,
                            value = age.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.ast,
                            value = ast.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.alt,
                            value = alt.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.platelet,
                            value = platelet.doubleValue
                        )
                    }
                }
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.nafldFibrosisScore),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 2F,
                        minValue = -4F,
                        firstThreshold = -1.44F,
                        secondThreshold = 0.672F,
                        firstLabel = stringResource(R.string.unlikely),
                        thirdLabel = stringResource(R.string.likely),
                        score = allScores.nfs
                    )
                    FlowRow {
                        FactorAlerts(
                            text = R.string.age,
                            value = age.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.bodyHeight,
                            value = bodyHeight.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.bodyWeight,
                            value = bodyWeight.doubleValue
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer, // Set the background color to white
                            shadowElevation = 2.dp,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(4.dp) // Optional: Add padding around the text
                        ) {
                            Text(
                                text = stringResource(R.string.diabetesMellitus),
                                color = Color.Black,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                        FactorAlerts(
                            text = R.string.ast,
                            value = ast.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.alt,
                            value = alt.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.platelet,
                            value = platelet.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.albumin,
                            value = albumin.doubleValue
                        )
                    }
                }
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.elfScore),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 14F,
                        minValue = 6F,
                        firstThreshold = 9.8F,
                        secondThreshold = 10.5F,
                        firstLabel = "",
                        secondLabel = stringResource(R.string.f2),
                        thirdLabel = stringResource(R.string.f3),
                        score = allScores.elfScore
                    )
                    FlowRow {
                        FactorAlerts(
                            text = R.string.hyaluronicAcid,
                            value = hyaluronicAcid.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.piiip,
                            value = piiinp.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.timp1,
                            value = timp1.doubleValue
                        )
                    }
                }
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.astToPlateletRatioIndex),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 2.5F,
                        minValue = 0.01F,
                        firstThreshold = 1.34F,
                        secondThreshold = 0F,
                        firstLabel = stringResource(R.string.nashCrnFibrosisStage02),
                        secondLabel = stringResource(R.string.stage34),
                        score = allScores.apri
                    )
                    FlowRow {
                        FactorAlerts(
                            text = R.string.ast,
                            value = ast.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.alt,
                            value = alt.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.platelet,
                            value = platelet.doubleValue
                        )
                    }
                }
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.m2bpgi),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 3F,
                        minValue = 0.01F,
                        firstThreshold = 0.94F,
                        secondThreshold = 1.46F,
                        firstLabel = stringResource(R.string.bruntStaging),
                        secondLabel = stringResource(R.string.stage3),
                        thirdLabel = stringResource(R.string.stage4),
                        score = allScores.m2bpgi
                    )
                    FactorAlerts(
                        text = R.string.m2bpgi,
                        value = m2bpgi.doubleValue
                    )
                }
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.caIndex),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 20F,
                        minValue = 0.01F,
                        firstThreshold = 8.723F,
                        //secondThreshold = 2.1F,
                        firstLabel = stringResource(R.string.normal),
                        secondLabel = stringResource(R.string.fibrosisStage1),
                        score = allScores.caIndex
                    )
                    FlowRow {
                        FactorAlerts(
                            text = R.string.type4collagen7S,
                            value = t4c7score.doubleValue
                        )
                        FactorAlerts(
                            text = R.string.ast,
                            value = ast.doubleValue
                        )
                    }
                }
                MedGuidelinesCard(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.shearWaveElastography),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 3F,
                        minValue = 0.01F,
                        firstThreshold = 1.3F,
                        secondThreshold = 2.1F,
                        firstLabel = stringResource(R.string.normal),
                        thirdLabel = stringResource(R.string.cACLD),
                        thirdLabelInDetail = stringResource(
                            R.string.compensatedAdvancedChronicLiverDisease
                        ),
                        score = allScores.swe
                    )
                    FactorAlerts(
                        text = R.string.shearWaveElastography,
                        value = swe.doubleValue
                    )
                }
            }
        }
    }
}

fun calculateElfScore(
    hyaluronicAcid: Double,
    piiinp: Double,
    timp1: Double
): Double {
    val score = 2.278 +
            0.815 * ln(hyaluronicAcid) +
            0.751 * ln(piiinp) +
            0.394 * ln(timp1)
    return score
}

@Composable
fun PopupClickable(text: String, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer, // Set the background color to white
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp) // Optional: Add padding around the text
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp) // Optional: Add padding inside the surface
        )
    }
}

fun DrawScope.drawThresholdLine(
    height: Float,
    xPosition: Float
) {
    drawLine(
        color = Color.Black,
        start = Offset(x = xPosition, y = height),
        end = Offset(x = xPosition, y = height * 2),
        strokeWidth = 3F
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun inputAndCalculate(
    age: MutableDoubleState,
    ast: MutableDoubleState,
    platelet: MutableDoubleState,
    alt: MutableDoubleState,
    swe: MutableDoubleState,
    bodyHeight: MutableDoubleState,
    bodyWeight: MutableDoubleState,
    dmPresence: MutableIntState,
    albumin: MutableDoubleState,
    hyaluronicAcid: MutableDoubleState,
    piiinp: MutableDoubleState,
    timp1: MutableDoubleState,
    elfScore: MutableDoubleState,
    m2bpgi: MutableDoubleState,
    t4c7score: MutableDoubleState,
    //calculatedElfScore: Double
): Scores {
    MedGuidelinesCard(
        modifier = Modifier.cardModifier()
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
                label = R.string.age, value = age,
                japaneseUnit = R.string.years, //changeUnit = changedFactor1Unit
            )
            InputValue(
                label = R.string.bodyHeight, value = bodyHeight,
                japaneseUnit = R.string.cm, //changeUnit = false
            )
            InputValue(
                label = R.string.bodyWeight, value = bodyWeight,
                japaneseUnit = R.string.kg, //changeUnit = false
            )
            InputValue(
                label = R.string.ast, value = ast,
                japaneseUnit = R.string.iul, //changeUnit = changedFactor2Unit
            )
            InputValue(
                label = R.string.alt, value = alt,
                japaneseUnit = R.string.iul, //changeUnit = changedFactor4Unit
            )
            InputValue(
                label = R.string.plateletCount, value = platelet,
                japaneseUnit = R.string.unit109L, //changeUnit = changedFactor3Unit
            )
            InputValue(
                label = R.string.albumin, value = albumin,
                japaneseUnit = R.string.gdL, //changeUnit = false
            )
            InputValue(
                label = R.string.shearWaveElastography, value = swe,
                japaneseUnit = R.string.ms, //changeUnit = changeFactor5Unit
            )
            InputValue(
                label = R.string.elfScore, value = elfScore,
                japaneseUnit = R.string.space, //changeUnit = false
            )
            InputValue(
                label = R.string.m2bpgi, value = m2bpgi,
                japaneseUnit = R.string.coi, //changeUnit = false
            )
            InputValue(
                label = R.string.type4collagen7S, value = t4c7score,
                japaneseUnit = R.string.ngml, //changeUnit = false
            )
            dmPresence.intValue = buttonAndScore(
                factor = noYes,
                title = R.string.dmPresence,
                titleNote = R.string.dmPresenceNote,
                cardColor = MaterialTheme.colorScheme.onSecondary,
            )
            InputValue(
                label = R.string.hyaluronicAcid, value = hyaluronicAcid,
                japaneseUnit = R.string.ngml, //changeUnit = false
            )
            InputValue(
                label = R.string.piiip, value = piiinp,
                japaneseUnit = R.string.ngml, //changeUnit = false
            )
            InputValue(
                label = R.string.timp1, value = timp1,
                japaneseUnit = R.string.ngml, //changeUnit = false
            )
        }
    }

    val fib4score = (age.doubleValue * ast.doubleValue) /
            (platelet.doubleValue * sqrt(alt.doubleValue))
    val apri = ((ast.doubleValue / 30) / platelet.doubleValue) * 100
    val nfs = -1.675 +
            (0.037 * age.doubleValue) +
            0.094 * (bodyWeight.doubleValue / ((bodyHeight.doubleValue / 100).pow(2.0))) +
            1.13 * dmPresence.intValue +
            0.99 * ast.doubleValue / alt.doubleValue -
            0.013 * platelet.doubleValue -
            0.66 * albumin.doubleValue
    val caIndex = 1.5 * t4c7score.doubleValue + 0.0264 * ast.doubleValue
    val allScores =
        Scores(
            fib4score,
            apri,
            swe.doubleValue,
            nfs,
            elfScore.doubleValue,
            m2bpgi.doubleValue,
            caIndex)
    //allScores.roundToTwoDecimals()
    return allScores
}


@Preview
@Composable
fun LiverFibrosisScoreSystemScreenPreview() {
    LiverFibrosisScoreSystemScreen(navController = NavController(LocalContext.current))
}

@Preview
@Composable
fun LiverFibrosisScoreSystemScreenPreview2() {
    GraphAndThreshold(
        maxValue = 2F,
        minValue = -4F,
        firstThreshold = -1.44F,
        secondThreshold = 0.672F,
        firstLabel = stringResource(R.string.unlikely),
        thirdLabel = stringResource(R.string.likely),
        score = 0.0
    )
}
