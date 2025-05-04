package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.GraphAndThreshold
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import kotlin.math.log10

@Composable
fun MALBIScreen(navController: NavController) {
    var grade by remember { mutableStateOf("") }
    var score by remember { mutableDoubleStateOf(0.0) }
    var scoreRound by remember { mutableDoubleStateOf(0.0) }
    val focusManager = LocalFocusManager.current
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.mALBITitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.mALBIRef, R.string.mALBIUrl)
                )
            )
        },
        bottomBar = {
            ResultBottomAppBar {

                Text(
                    buildAnnotatedString {
                        append("mALBI ")
                        withStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append(" $grade ")
                        }
                        append(" ($scoreRound)")
                    },
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
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
                score = mALBIInput()
                grade = when {
                    score <= -2.6 -> "1"
                    score < -2.27 -> "2a"
                    score <= -1.39 -> "2b"
                    else -> "3"
                }
                scoreRound = Math.round(score * 100.0) / 100.0

                Card(
                    modifier = Modifier.cardModifier()
                ) {
                    Text(
                        text = stringResource(R.string.mALBITitle),
                        modifier = Modifier.textModifier()
                    )
                    GraphAndThreshold(
                        maxValue = -1F,
                        minValue = -3.2F,
                        firstThreshold = -2.6F,
                        secondThreshold = -2.27F,
                        thirdThreshold = -1.39F,
                        firstLabel = stringResource(R.string.grade1),
                        secondLabel = stringResource(R.string.g2a),
                        thirdLabel = stringResource(R.string.g2b),
                        fourthLabel = stringResource(R.string.g3),
                        score = scoreRound
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun mALBIInput(): Double {
    val totalBilirubin = remember { mutableDoubleStateOf(1.0) }
    val albumin = remember { mutableDoubleStateOf(4.1) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp),
            itemVerticalAlignment = Alignment.Bottom,
        ) {
            InputValue(
                label = R.string.totalBilirubin, value = totalBilirubin, unit = R.string.mgdl,
                changedValueRate = 17.1, changedUnit = R.string.umolL
            )
            InputValue(
                label = R.string.albumin, value = albumin, unit = R.string.gdL,
                changedValueRate = 10.0, changedUnit = R.string.gL
            )
        }
    }
    val score =
        (log10(17.1 * totalBilirubin.doubleValue)) * 0.66 + (10 * albumin.doubleValue * (-0.085))
    return score
}

@Preview
@Composable
fun MALBIScreenPreview() {
    MALBIScreen(navController = NavController(LocalContext.current))
}