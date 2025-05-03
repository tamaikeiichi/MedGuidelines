package com.keiichi.medguidelines.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun buttonAndScore(
    factor: List<Int>,
    title: Int,
    titleNote: Int
): Int {
    val radioOptions: List<Int> = factor
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    RadioButtonAndExpand(factor, selectedOption, onOptionSelected, title, titleNote)
    val score: Int =
        radioOptions.indexOf(selectedOption).coerceAtLeast(0)
    return score
}