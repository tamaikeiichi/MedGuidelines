package com.keiichi.medguidelines.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.keiichi.medguidelines.R

@Composable
fun buttonAndScore(
    factor: List<Int>,
    title: Int,
    titleNote: Int,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable () -> Unit = {}
): Int {
    val radioOptions: List<Int> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(
        factor,
        selectedOption,
        onOptionSelected, title, titleNote, cardColor,
        appendixLabel= appendixLabel
        )
    val score: Int =
        radioOptions.indexOf(selectedOption).coerceAtLeast(0)
    return score
}

@Preview
@Composable
fun ButtonAndScorePreview(
    factor: List<Int> = listOf(1, 2, 3),
    title: Int = R.string.congestiveHearFaiLureHistoryTitle,
    titleNote: Int = R.string.space
) {}