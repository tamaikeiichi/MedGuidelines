package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.MfactorOropharyngealCancer
import com.keiichi.medguidelines.data.NfactorHypopharyngealCancer
import com.keiichi.medguidelines.data.NfactorOropharyngealHpvAssociated
import com.keiichi.medguidelines.data.TfactorOropharyngealHpvAssociated
import com.keiichi.medguidelines.data.TfactorOropharyngealHpvIndependent
import com.keiichi.medguidelines.data.hypopharyngealCancerTNM
import com.keiichi.medguidelines.data.oropharyngealHpvAssociatedCancerTNM
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.TextAndUrl

// タイトルのプルダウンの並び順
private const val HPV_ASSOCIATED = 0
private const val HPV_INDEPENDENT = 1

@Composable
fun OropharyngealTNMScreen(navController: NavController) {
    var hpvStatus by rememberSaveable { mutableIntStateOf(HPV_ASSOCIATED) }
    var score by remember { mutableStateOf(listOf(0, 0)) }
    var literalScore by remember { mutableStateOf("") }

    // Build the display string here
    val displayString = buildAnnotatedString {
        append("Stage ")
        append(literalScore)
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.oropharyngealTNMScreenTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                ),
                titleOptions = listOf(
                    R.string.oropharyngealHpvAssociatedTitle,
                    R.string.oropharyngealHpvIndependentTitle
                ),
                selectedTitleOption = hpvStatus,
                onTitleOptionSelected = { hpvStatus = it }
            )
        },
        bottomBar = {
            ScoreBottomAppBar(displayText = displayString)
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            // HPV関連／非関連を切り替えたら選択をリセットする
            key(hpvStatus) {
                if (hpvStatus == HPV_ASSOCIATED) {
                    score = oropharyngealHpvAssociatedTNMScore()
                    literalScore = oropharyngealHpvAssociatedCancerTNM[score.last()][score.first()]
                } else {
                    score = oropharyngealHpvIndependentTNMScore()
                    // HPV非関連中咽頭癌の病期は下咽頭癌と共通
                    literalScore = hypopharyngealCancerTNM[score.last()][score.first()]
                }
            }
        }
    }
}

@Composable
fun oropharyngealHpvAssociatedTNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorOropharyngealHpvAssociated,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteOropharyngealHpvAssociated
    )
    val scoreB = buttonAndScore(
        NfactorOropharyngealHpvAssociated,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteOropharyngealHpvAssociated
    )
    val scoreC = buttonAndScore(
        MfactorOropharyngealCancer,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteHypopharyngealCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    } else {
        listOf(scoreA, scoreC + NfactorOropharyngealHpvAssociated.size - 1)
    }
    return score
}

@Composable
fun oropharyngealHpvIndependentTNMScore(): List<Int> {
    val scoreA = buttonAndScore(
        TfactorOropharyngealHpvIndependent,
        R.string.TFactorTitle,
        R.string.TfactorTitleNoteOropharyngealHpvIndependent
    )
    // N分類は下咽頭癌と共通
    val scoreB = buttonAndScore(
        NfactorHypopharyngealCancer,
        R.string.NFactorTitle,
        R.string.NfactorTitleNoteHypopharyngealCancer
    )
    val scoreC = buttonAndScore(
        MfactorOropharyngealCancer,
        R.string.MFactorTitle,
        R.string.MfactorTitleNoteHypopharyngealCancer
    )
    val score = if (scoreC == 0) {
        listOf(scoreA, scoreB)
    } else {
        listOf(scoreA, scoreC + NfactorHypopharyngealCancer.size - 1)
    }
    return score
}

@Preview
@Composable
fun OropharyngealTNMScreenPreview() {
    OropharyngealTNMScreen(navController = NavController(LocalContext.current))
}
