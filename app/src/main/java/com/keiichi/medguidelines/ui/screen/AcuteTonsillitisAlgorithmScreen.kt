package com.keiichi.medguidelines.ui.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.absencePresence
import com.keiichi.medguidelines.data.mclsaacAge
import com.keiichi.medguidelines.data.tonsillitisRedFlag
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl


@Composable
fun AcuteTonsillitisAlgorithmScreen(navController: NavController) {
    var redFlagScore by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.acuteTonsillitisAlgorithmTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(
                        R.string.acuteTonsillitisAlgorithmReference,
                        R.string.acuteTonsillitisAlgorithmReferenceUrl
                    )
                )
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
            item {
                redFlagScore = acuteTonsillitisRedFlagScore()
                if (redFlagScore > 0) {
                    LaunchedEffect(key1 = redFlagScore) {
                        listState.animateScrollToItem(0)
                    }
                    AcuteTonsillitisCard(title = R.string.needSpecialistExamination)
                } else {
                    score = acuteTonsillitisMclsaacScore()
                    when (score) {
                        -1, 0, 1 -> AcuteTonsillitisFollowupScore()
                        2, 3 -> {
                            LaunchedEffect(key1 = score) {
                                listState.animateScrollToItem(1)
                            }
                            AcuteTonsillitisCard(R.string.antigenTest)
                            AcuteTonsillitisCard(R.string.antibioticsAMPC)
                        }

                        4, 5, 6, 7 -> {
                            LaunchedEffect(key1 = score) {
                                listState.animateScrollToItem(1)
                            }
                            AcuteTonsillitisCard(R.string.antigenTest)
                            AcuteTonsillitisCard(R.string.antibioticsAMPC)
                            AcuteTonsillitisCard(R.string.hospitalCare)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AcuteTonsillitisCard(
    title: Int,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    ) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(color)
    ) {
        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun acuteTonsillitisRedFlagScore(): Int {
    var score by rememberSaveable { mutableIntStateOf(0) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            score = buttonAndScore(
                tonsillitisRedFlag,
                R.string.tonsillitisRedFlagTitle,
                R.string.space
            )
        }
    }
    return score
}

@Composable
fun AcuteTonsillitisFollowupScore(
    color: Color = MaterialTheme.colorScheme.onPrimary
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(color)
    ) {
        Text(
            text = stringResource(id = R.string.followup),
            modifier = Modifier.padding(8.dp)
        )
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
            scoreA = buttonAndScore(
                absencePresence,
                R.string.temperatureTitle,
                R.string.space
            )
            scoreB = buttonAndScore(
                absencePresence,
                R.string.coughTitle,
                R.string.space
            )
            scoreC = buttonAndScore(
                absencePresence,
                R.string.cervicalNodesTitle,
                R.string.space
            )
            scoreD = buttonAndScore(
                absencePresence,
                R.string.tonsillarTitle,
                R.string.space
            )
            scoreE = buttonAndScore(
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

//@Preview
//@Composable
//fun AcuteTonsillitisAlgorithmScreenPreview() {
//    AcuteTonsillitisAlgorithmScreen()
//}