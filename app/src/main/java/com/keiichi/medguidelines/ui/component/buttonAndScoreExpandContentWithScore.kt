package com.keiichi.medguidelines.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.keiichi.medguidelines.R // Assuming your R file is here
import com.keiichi.medguidelines.data.LabelAndScore
import com.keiichi.medguidelines.data.yesNoUnknown

// Data class to hold label and score for each option


@Composable
fun buttonAndScoreExpandContentWithScore(
    optionsWithScores: List<LabelAndScore>, // Changed from factor: List<Int>
    title: Int, // R.string.xxx for the title
    expandedContent: @Composable (() -> Unit)? = null,
    defaultSelectedOption: Int? = null,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable () -> Unit = {}
): Int {
    // Ensure optionsWithScores is not empty to avoid crashing on optionsWithScores[0]
    if (optionsWithScores.isEmpty()) {
        // Handle empty options list: return a default score, throw error, or show placeholder
        return 0 // Or some other sensible default
    }
// Determine the initial selected option
    val initialOption = optionsWithScores.find{it.labelResId == defaultSelectedOption}?.takeIf { optionsWithScores.contains(it) }
        ?: optionsWithScores[0] // Fallback to first if default is null or not in the list

    // The state now holds the selected ScoreOption object
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(initialOption) }

    // RadioButtonAndExpand will need to be adapted to use ScoreOption
    // It will pass the full ScoreOption object back on selection.
    RadioButtonAndExpandContentWithScore( // You'll need to modify RadioButtonAndExpand
        options = optionsWithScores,
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        title = title,
        expandedContent = expandedContent,
        cardColor = cardColor,
        appendixLabel = appendixLabel
    )

    // The score is now directly from the selected option's score property
    val score: Int = selectedOption.score
    return score
}

