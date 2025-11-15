package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.eyeGrade
import com.keiichi.medguidelines.data.motorGrade
import com.keiichi.medguidelines.data.verbalGrade
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.viewModel.SofaViewModel
import com.keiichi.medguidelines.ui.viewModel.GcsComponents

@Composable
fun GlasgowComaScaleScreen(
    navController: NavController,
    viewModel: SofaViewModel
) {
    val gcsComponents by viewModel.gcsComponents.collectAsState()

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.glasgowComaScaleTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
        bottomBar = {
            ResultBottomAppBar {
                Text(
                    text = buildAnnotatedString {
                        append("E")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(gcsComponents.e.toString())
                        }
                        append("V")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(gcsComponents.v.toString())
                        }
                        append("M")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(gcsComponents.m.toString())
                        }
                        append(", Total ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            val total = gcsComponents.e + gcsComponents.v + gcsComponents.m
                            append(total.toString())
                        }
                    },
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
//            score = glasgowComaScaleScore()
            glasgowComaScaleScore(
                defaultScores = gcsComponents,
                onScoresChanged = { e, v, m ->
                    viewModel.updateGcsComponents(e, v, m)
                }
            )
        }

}
}

@Composable
fun glasgowComaScaleScore(
    defaultScores: GcsComponents,
    onScoresChanged: (e: Int, v: Int, m: Int) -> Unit
) {
    val defaultEyeIndex = eyeGrade.find { it.score == defaultScores.e }?.labelResId ?: eyeGrade[0].labelResId
    val defaultVerbalIndex = verbalGrade.find { it.score == defaultScores.v }?.labelResId ?: verbalGrade[0].labelResId
    val defaultMotorIndex = motorGrade.find { it.score == defaultScores.m }?.labelResId ?: motorGrade[0].labelResId


    val eye = buttonAndScoreWithScoreDisplayed(
            optionsWithScores = eyeGrade,
            title = R.string.eye,
            defaultSelectedOption = defaultEyeIndex // Use the calculated default
        )
    val verbal = buttonAndScoreWithScoreDisplayed(
            optionsWithScores = verbalGrade,
            title = R.string.verbal,
            defaultSelectedOption = defaultVerbalIndex // Use the calculated default
        )
    val motor = buttonAndScoreWithScoreDisplayed(
            optionsWithScores = motorGrade,
            title = R.string.motor,
            defaultSelectedOption = defaultMotorIndex // Use the calculated default
        )

    LaunchedEffect(eye, verbal, motor) {
        onScoresChanged(eye, verbal, motor)
    }
}

@Preview
@Composable
fun GlasgowComaScaleScreenPreview() {
    GlasgowComaScaleScreen(
        navController = NavController(LocalContext.current),
        viewModel = SofaViewModel()
    )
}