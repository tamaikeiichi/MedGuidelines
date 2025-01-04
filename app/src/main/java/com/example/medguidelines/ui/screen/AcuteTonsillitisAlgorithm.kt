package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medguidelines.R
import com.example.medguidelines.data.RadioButtonName
import com.example.medguidelines.data.absencePresence
import com.example.medguidelines.data.mclsaacAge
import com.example.medguidelines.data.tonsillitisRedFlag
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar


@Composable
fun AcuteTonsillitisAlgorithmScreen(navController: NavController) {
    var redFlagScore by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = stringResource(id = R.string.acuteTonsillitisAlgorithmTitle),
                navController = navController
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            state = listState
        ) {
            item{
                redFlagScore = acuteTonsillitisRedFlagScore()
                if (redFlagScore > 0) {
                    LaunchedEffect(key1 = redFlagScore) {
                        listState.animateScrollToItem(0)
                    }
                    AcuteTonsillitisSpecialistExamination()
                    } else {
                        score = acuteTonsillitisMclsaacScore()
                        when (score) {
                            0,1 -> AcuteTonsillitisFollowupScore()
                            2,3 -> {
                                LaunchedEffect(key1 = score) {
                                    listState.animateScrollToItem(1)
                                }
                                AcuteTonsillitisAntigenTest()
                                AcuteTonsillitisAntibioticsAMPC()
                            }
                            4,5,6,7 -> {
                                LaunchedEffect(key1 = score) {
                                    listState.animateScrollToItem(1)
                                }
                                AcuteTonsillitisAntigenTest()
                                AcuteTonsillitisAntibioticsAMPC()
                                AcuteTonsillitisHospitalCare()
                            }
                        }
                }
                }
            }
        }
    }

@Composable
fun AcuteTonsillitisHospitalCare() {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Text(text = stringResource(id = R.string.hospitalCare),
            modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun AcuteTonsillitisAntigenTest() {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Text(text = stringResource(id = R.string.antigenTest),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun AcuteTonsillitisAntibioticsAMPC() {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Text(text = stringResource(id = R.string.antibioticsAMPC),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun AcuteTonsillitisSpecialistExamination() {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Text(text = stringResource(id = R.string.needSpecialistExamination),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun acuteTonsillitisRedFlagScore():Int {
    var score by remember { mutableIntStateOf(0) }
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Column (
            modifier = Modifier.padding(8.dp)
        ) {
            score = acuteTonsillitisAlgorithmButtonAndScore(
                tonsillitisRedFlag,
                R.string.tonsillitisRedFlagTitle,
                R.string.space
            )
        }
    }
    return score
}

@Composable
fun AcuteTonsillitisFollowupScore() {
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ){
        Text(text = stringResource(id = R.string.followup),
            modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun acuteTonsillitisMclsaacScore(): Int {
    val scores = remember { List(5) { mutableIntStateOf(0) } }
    var scoreA by scores[0]
    var scoreB by scores[1]
    var scoreC by scores[2]
    var scoreD by scores[3]
    var scoreE by scores[4]
    var totalScore by remember { mutableIntStateOf(0) }
    Card(
        modifier = Modifier.padding(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            scoreA = acuteTonsillitisAlgorithmButtonAndScore(
                absencePresence,
                R.string.temperatureTitle,
                R.string.space
            )
            scoreB = acuteTonsillitisAlgorithmButtonAndScore(
                absencePresence,
                R.string.coughTitle,
                R.string.space
            )
            scoreC = acuteTonsillitisAlgorithmButtonAndScore(
                absencePresence,
                R.string.cervicalNodesTitle,
                R.string.space
            )
            scoreD = acuteTonsillitisAlgorithmButtonAndScore(
                absencePresence,
                R.string.tonsillarTitle,
                R.string.space
            )
            scoreE = acuteTonsillitisAlgorithmButtonAndScore(
                mclsaacAge,
                R.string.mclsaacAgeTitle,
                R.string.space
            )
            totalScore =
                    scoreA + scoreB + scoreC + scoreD + (scoreE * (-1) + 1)

        }
    }
    return totalScore
}

@Composable
fun acuteTonsillitisAlgorithmButtonAndScore(
    factor : List<RadioButtonName>,
    title : Int,
    titleNote : Int
): Int
{
    val radioOptions : List<RadioButtonName> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)

    return radioOptions.indexOf(selectedOption).coerceAtLeast(0)
}

//@Preview
//@Composable
//fun AcuteTonsillitisAlgorithmScreenPreview() {
//    AcuteTonsillitisAlgorithmScreen()
//}