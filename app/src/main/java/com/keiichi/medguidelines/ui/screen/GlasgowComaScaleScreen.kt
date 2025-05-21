package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
import com.keiichi.medguidelines.ui.component.buttonAndScore

@Composable
fun GlasgowComaScaleScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }

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
                    text = "Glasgow Coma Scale ($totalScore)",
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
            totalScore = glasgowComaScaleTotalScore()
        }

}
}

@Composable
fun glasgowComaScaleTotalScore(): Int{
    val eye = buttonAndScore(
        factor = eyeGrade,
        title = R.string.eye,

    ) + 1
    val verbal = buttonAndScore(
        factor = verbalGrade,
        title = R.string.verbal,
    ) + 1
    val motor = buttonAndScore(
        factor = motorGrade,
        title = R.string.motor
    )+ 1
    val totalScore = eye + verbal + motor
    return totalScore
}

@Preview
@Composable
fun GlasgowComaScaleScreenPreview() {
    GlasgowComaScaleScreen(navController = NavController(LocalContext.current))
}