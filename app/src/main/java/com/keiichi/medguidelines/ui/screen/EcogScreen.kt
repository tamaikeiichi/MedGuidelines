package com.keiichi.medguidelines.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.ecog
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesFlowRow
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.buttonAndScore

@Composable
fun EcogScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var score: Int by remember { mutableIntStateOf(0) }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.ecogPerformanceStatus,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.ecog, R.string.ecogUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBar(displayText = buildAnnotatedString {
                append("Score ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(score.toString())
                }

            }
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
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),

            ) {
            score = score()

        }

    }}

@Composable
private fun score(): Int {
    var score = remember { mutableIntStateOf(0) }

    MedGuidelinesCard {
        MedGuidelinesFlowRow {
            score.intValue = buttonAndScore(
                factor = ecog,
                title = R.string.space,
            )
        }
    }
    return score.intValue
}

@Preview
@Composable
fun EcogScreenPreview(){
    EcogScreen(navController = NavController(LocalContext.current))
}