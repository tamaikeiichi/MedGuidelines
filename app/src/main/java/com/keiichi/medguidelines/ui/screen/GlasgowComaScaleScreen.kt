package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.viewModel.SofaViewModel

data class glasgowComaScale(
    val e: Int,
    val v: Int,
    val m: Int
)

@Composable
fun GlasgowComaScaleScreen(
    navController: NavController,
    viewModel: SofaViewModel
) {
    var score by remember { mutableStateOf(glasgowComaScale(0, 0, 0)) }
    // Call the ViewModel's update function whenever the score changes

    val sumOfScore = score.e + score.v + score.m

    LaunchedEffect(sumOfScore) {
        viewModel.updateGcsScore(sumOfScore.toDouble())
    }

//    // You can also add a button that pops the back stack
//    Button(onClick = { navController.popBackStack() }) {
//        Text("Done")
//    }

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
                            append(score.e.toString())
                        }
                        append("V")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(score.v.toString())
                        }
                        append("M")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(score.m.toString())
                        }
                        append(", Total ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append((score.e + score.v + score.m).toString())
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
            score = glasgowComaScaleScore()
        }

}
}

@Composable
fun glasgowComaScaleScore(): glasgowComaScale {
    val eye = invertNumberHorizontally(
        number = buttonAndScore(
            factor = eyeGrade,
            title = R.string.eye,
        ) + 1,
        list = eyeGrade
    )
    val verbal = invertNumberHorizontally(
        number =  buttonAndScore(
            factor = verbalGrade,
            title = R.string.verbal,
        ) + 1,
        list = verbalGrade
    )
    val motor = invertNumberHorizontally(
        number =  buttonAndScore(
            factor = motorGrade,
            title = R.string.motor,
        ) + 1,
        list = motorGrade
    )
    val score = glasgowComaScale(
        e = eye,
        v = verbal,
        m = motor
    )
    return score
}

private fun invertNumberHorizontally(
    number: Int,
    list: List<Int>,
): Int{
    val maxNumber = list.size.toDouble()
    val minNumber = 1.0
    val invertedNumber = ((number - ((maxNumber + minNumber)/2)) * (-1)) + ((maxNumber + minNumber)/2)
    return invertedNumber.toInt()
}

@Preview
@Composable
fun GlasgowComaScaleScreenPreview() {
    GlasgowComaScaleScreen(
        navController = NavController(LocalContext.current),
        viewModel = SofaViewModel()
    )
}