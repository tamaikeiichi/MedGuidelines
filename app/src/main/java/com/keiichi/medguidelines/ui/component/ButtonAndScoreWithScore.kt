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
fun buttonAndScoreWithScore(
    optionsWithScores: List<LabelAndScore>, // Changed from factor: List<Int>
    title: Int, // R.string.xxx for the title
    titleNote: Int = R.string.space,
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
    RadioButtonAndExpandWithScore( // You'll need to modify RadioButtonAndExpand
        options = optionsWithScores,
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        title = title,
        titleNote = titleNote,
        cardColor = cardColor,
        appendixLabel = appendixLabel
    )

    // The score is now directly from the selected option's score property
    val score: Int = selectedOption.score
    return score
}

//@Preview
//@Composable
//fun ButtonAndScorePreview() {
//    // Example usage for the preview
//    val exampleOptions = listOf(
//        ScoreOption(labelResId = R.string.option_label_1, score = 3), // Example string resource
//        ScoreOption(labelResId = R.string.option_label_2, score = 7), // Example string resource
//        ScoreOption(labelResId = R.string.option_label_3, score = 0)  // Example string resource
//    )
//    buttonAndScore(
//        optionsWithScores = exampleOptions,
//        title = R.string.congestiveHearFaiLureHistoryTitle, // Example title
//        titleNote = R.string.space
//    )
//}

// Assume you have these dummy string resources in your strings.xml for the preview to work:
// <string name="option_label_1">Option 1 (Score 3)</string>
// <string name="option_label_2">Option 2 (Score 7)</string>
// <string name="option_label_3">Option 3 (Score 0)</string>
