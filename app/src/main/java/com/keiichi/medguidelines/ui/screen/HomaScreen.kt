package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.GraphAndThreshold
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl

data class HomaScore(
    val ir: Double,
    val beta: Double
)

data class HomaGrade(
    var ir: String,
    var beta: String
)

@Composable
fun HomaIRScreen(navController: NavController) {
    var grade by remember { mutableStateOf(
        HomaGrade(
            ir = "",
            beta = ""
        )
    ) }
    var score by remember { mutableStateOf(
        HomaScore(ir = 0.0, beta = 0.0)
    )
    }
    var scoreRound by remember { mutableStateOf(
        HomaScore(ir = 0.0, beta = 0.0)
    )}
    val focusManager = LocalFocusManager.current
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.homaIrHomaBetaTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
        bottomBar = {
            ResultBottomAppBar {
                Text(
                    buildAnnotatedString {
                        append(stringResource(R.string.insulinResistance))
                        append(" ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(" ${grade.ir} ")
                        }
                        append("\n")
                        append(stringResource(R.string.bCellDysfunction))
                        append(" ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(" ${grade.beta} ")
                        }
                        //append("HOMA-IR=$scoreRound")
                    },
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.2.em
                )

            }
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
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                score = homaInput()
                grade.ir = when {
                    score.ir <= 1.6 -> stringResource(R.string.absence)
                    score.ir < 2.5 -> stringResource(R.string.boaderline)
                    else -> stringResource(R.string.presence)
                }
                grade.beta = when {
                    score.beta < 30 -> stringResource(R.string.presence)
                    else -> stringResource(R.string.absence)
                }
                scoreRound = HomaScore(
                    ir = Math.round(score.ir * 100.0) / 100.0,
                    beta = Math.round(score.beta * 100.0) / 100.0
                )
                MedGuidelinesCard (
                ) {
                    Text(
                        text = stringResource(R.string.homairTitle),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 4F,
                        minValue = 0.1F,
                        firstThreshold = 1.6F,
                        secondThreshold = 2.5F,
                        firstLabel = stringResource(R.string.normal),
                        secondLabel = stringResource(R.string.space),
                        thirdLabel = stringResource(R.string.insulinResistance),
                        score = scoreRound.ir
                    )
                }
                MedGuidelinesCard {
                    Text(
                        text = stringResource(R.string.homab),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = 100F,
                        minValue = -10F,
                        firstThreshold = 30F,
                        //secondThreshold = -10F,
                        firstLabel = stringResource(R.string.bCellDysfunction),
                        secondLabel = stringResource(R.string.normal),
                        //thirdLabel = stringResource(R.string.space),
                        score = scoreRound.beta
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun homaInput(): HomaScore {
    val insulin = remember { mutableDoubleStateOf(5.0) }
    val glucose = remember { mutableDoubleStateOf(90.0) }
    var changedFactor1Unit by remember { mutableStateOf(true) }
    var changedFactor2Unit by remember { mutableStateOf(true) }

    MedGuidelinesCard (
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .wrapContentHeight(
                    align = Alignment.Bottom
                ),
            itemVerticalAlignment = Alignment.Bottom
        ) {
            InputValue(
                label = R.string.fastingInsulin, value = insulin,
                japaneseUnit = R.string.uUml,
                isJapaneseUnit = remember { mutableStateOf(changedFactor1Unit) }.also {
                    changedFactor1Unit = it.value
                }
            )
            InputValue(
                label = R.string.fastingGlucose, value = glucose,
                japaneseUnit = R.string.mgdl,
                isJapaneseUnit = remember { mutableStateOf(changedFactor2Unit) }.also {
                    changedFactor2Unit = it.value
                },
                changedValueRate = 0.05551,
                changedUnit = R.string.mmoll
            )
        }
    }
    val score = HomaScore(
        ir = insulin.doubleValue * glucose.doubleValue / 405,
        beta = 360 * insulin.doubleValue / (glucose.doubleValue - 63)
    )
    return score
}

@Preview
@Composable
fun HomaIRScreenPreview() {
    HomaIRScreen(navController = NavController(LocalContext.current))
}