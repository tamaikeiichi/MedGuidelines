package com.keiichi.medguidelines.ui.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.albuminGrade
import com.keiichi.medguidelines.data.ascitesGrade
import com.keiichi.medguidelines.data.bilirubinGrade
import com.keiichi.medguidelines.data.encephalopathyGrade
import com.keiichi.medguidelines.data.kayserFleischerRings
import com.keiichi.medguidelines.data.ptGrade
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ResultBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl

@Composable
fun WilsonScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var diagnosis by remember { mutableStateOf("") }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.wilsonTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
        bottomBar = {
            ResultBottomAppBar {
                Text(
                    text = "Total Score: $totalScore ($diagnosis)",
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
            totalScore = wilsonTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            diagnosis = when (totalScore) {
                in 4..20 -> "established"
                in 3..3 -> "possible"
                else -> "very unlikely"
            }
        }
    }
}

@Composable
fun wilsonTotalScore(): Int {
    val  KayserFleischerRingsScore = buttonAndScore(
        factor = kayserFleischerRings,
        title = R.string.KFrings,
        titleNote = R.string.space
    ) * (-1)
    val albuminScore =
        buttonAndScore(
            albuminGrade,
            R.string.albuminTitle,
            R.string.space
        ) + 1
    val ptScore = buttonAndScore(
        ptGrade,
        R.string.ptTitle,
        R.string.ptTitleNote
    ) + 1
    val ascitesScore =
        buttonAndScore(
            ascitesGrade,
            R.string.ascitesTitle,
            R.string.space
        ) + 1
    val encephalopathyScore = buttonAndScore(
        encephalopathyGrade,
        R.string.encephalopaphyTitle,
        R.string.encephalopaphyTitleNote
    ) + 1

    val totalScore =
        bilirubinScore + albuminScore + ptScore + ascitesScore + encephalopathyScore
    return totalScore
}

@Preview
@Composable
fun ChildPughScreenPreview() {
    ChildPughScreen(navController = NavController(LocalContext.current))
}