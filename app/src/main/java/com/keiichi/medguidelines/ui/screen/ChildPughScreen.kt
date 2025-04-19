package com.keiichi.medguidelines.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
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
import com.keiichi.medguidelines.data.ptGrade
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.textAndUrl

@Composable
fun ChildPughScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var childPughScoreABC by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TitleTopAppBar(title =  R.string.childPughTitle,
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
                        text = "Child-Pugh $childPughScoreABC ($totalScore)",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = childPughTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            childPughScoreABC = when (totalScore) {
                in 5..6 -> "A"
                in 7..9 -> "B"
                else -> "C"
            }
        }
    }
}

@Composable
fun childPughTotalScore(): Int {
    val bilirubinScore = buttonAndScore(
        bilirubinGrade,
        R.string.bilirubinTitle,
        R.string.space
    ) + 1
    val albuminScore =
        buttonAndScore(
            albuminGrade,
            R.string.albuminTitle,
            R.string.space
        ) + 1
    val ptScore = buttonAndScore(ptGrade,
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
    return  totalScore
}

@Preview
@Composable
fun ChildPughScreenPreview(){
    ChildPughScreen(navController = NavController(LocalContext.current))
}