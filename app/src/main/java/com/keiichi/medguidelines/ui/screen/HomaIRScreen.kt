package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
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
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.textAndUrl

@Composable
fun HomaIRScreen(navController: NavController) {
    var grade by remember { mutableStateOf("") }
    var score by remember { mutableDoubleStateOf(0.0) }
    var scoreRound by remember { mutableDoubleStateOf(0.0) }
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.homairTitle,
                navController = navController,
                references = listOf(
                    textAndUrl(R.string.space, R.string.space)
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        buildAnnotatedString {
                            append(stringResource(R.string.insulinResistance))
                            append(" ")
                            withStyle(
                                style = SpanStyle(fontWeight = FontWeight.Bold)
                            ){
                                append(" $grade ")
                            }
                            append("HOMA-IR=$scoreRound")
                        },
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }
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
                score = homaIRInput()
                grade = when {
                    score <= 1.6 -> stringResource(R.string.absence)
                    score < 2.5 -> stringResource(R.string.boaderline)
                    else -> stringResource(R.string.presence)
                }
                scoreRound = Math.round(score * 100.0)/100.0
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()

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
                        score = scoreRound
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun homaIRInput(): Double {
    val insulin = remember { mutableDoubleStateOf(5.0) }
    val glucose = remember { mutableDoubleStateOf(100.0) }
    var changedFactor1Unit by remember { mutableStateOf(true) }
    var changedFactor2Unit by remember { mutableStateOf(true) }


    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()

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
                unit = R.string.uUml,
                changeUnit = remember { mutableStateOf(changedFactor1Unit) }.also { changedFactor1Unit = it.value }
            )
            InputValue(
                label = R.string.fastingGlucose, value = glucose,
                unit = R.string.mgdl,
                changeUnit = remember { mutableStateOf(changedFactor2Unit) }.also { changedFactor2Unit = it.value },
                changedValueRate = 0.05551,
                changedUnit = R.string.mmoll
            )
        }

    }
    val score = insulin.doubleValue * glucose.doubleValue / 405
    return score
}

@Preview
@Composable
fun HomaIRScreenPreview(){
    HomaIRScreen(navController = NavController(LocalContext.current))
}