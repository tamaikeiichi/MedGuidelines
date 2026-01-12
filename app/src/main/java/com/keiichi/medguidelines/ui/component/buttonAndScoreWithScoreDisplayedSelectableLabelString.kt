package com.keiichi.medguidelines.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.keiichi.medguidelines.R // Assuming your R file is here
import com.keiichi.medguidelines.ui.screen.LabelStringAndScore

@Composable
fun buttonAndScoreWithScoreDisplayedSelectableLabelString(
    optionsWithScores: List<LabelStringAndScore>,
    @StringRes title: Int,
    titleNote: Int = R.string.space,
    defaultSelectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable () -> Unit = {},
    isNumberDisplayed: Boolean = true,
    onTitleClick: (() -> Unit)? = null,
    expandedContent: @Composable (() -> Unit)? = null,
): Int {
    if (optionsWithScores.isEmpty()) {
        return 0 // Or some other sensible default
    }

    // 1. REMOVED: The conflicting internal state `remember { mutableStateOf(...) }` is gone.
    //    This is the most important part of the fix.

    // 2. Find the currently selected option OBJECT based on the `defaultSelectedOption` Int passed from the parent.
    //    This is needed to pass the object to the child and to calculate the score.
    val currentSelectedOptionObject = optionsWithScores.find { it.labelResId == defaultSelectedOption }
        ?: optionsWithScores[0] // Fallback to the first option if no match is found.

    RadioButtonAndExpandWithScoreDisplayedLabelString(
        options = optionsWithScores,
        // 3. Pass the controlled state object down to the next level.
        selectedOption = currentSelectedOptionObject,
        // When the child reports a selection, convert it back to an Int (labelResId) and send it up to the original caller.
        onOptionSelected = { newSelectedLabelAndScore ->
            onOptionSelected(newSelectedLabelAndScore.labelResId)
        },
        title = title,
        titleNote = titleNote,
        cardColor = cardColor,
        appendixLabel = appendixLabel,
        isNumberDisplayed = isNumberDisplayed,
        onTitleClick = onTitleClick,
        expandedContent = expandedContent,
    )

    // 4. The score is now derived directly from the controlled state object, which is always in sync.
    return currentSelectedOptionObject.code
}

@Composable
fun buttonAndScoreWithScoreDisplayedSelectableLabelString(
    optionsWithScores: List<LabelStringAndScore>,
    title: String,
    titleNote: Int = R.string.space,
    defaultSelectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable () -> Unit = {},
    isNumberDisplayed: Boolean = true,
    onTitleClick: (() -> Unit)? = null,
    expandedContent: @Composable (() -> Unit)? = null,
): Int {
    if (optionsWithScores.isEmpty()) {
        return 0 // Or some other sensible default
    }

    // 1. REMOVED: The conflicting internal state `remember { mutableStateOf(...) }` is gone.
    //    This is the most important part of the fix.

    // 2. Find the currently selected option OBJECT based on the `defaultSelectedOption` Int passed from the parent.
    //    This is needed to pass the object to the child and to calculate the score.
    val currentSelectedOptionObject = optionsWithScores.find { it.labelResId == defaultSelectedOption }
        ?: optionsWithScores[0] // Fallback to the first option if no match is found.

    RadioButtonAndExpandWithScoreDisplayedLabelString(
        options = optionsWithScores,
        // 3. Pass the controlled state object down to the next level.
        selectedOption = currentSelectedOptionObject,
        // When the child reports a selection, convert it back to an Int (labelResId) and send it up to the original caller.
        onOptionSelected = { newSelectedLabelAndScore ->
            onOptionSelected(newSelectedLabelAndScore.labelResId)
        },
        title = title,
        titleNote = titleNote,
        cardColor = cardColor,
        appendixLabel = appendixLabel,
        isNumberDisplayed = isNumberDisplayed,
        onTitleClick = onTitleClick,
        expandedContent = expandedContent,
    )

    // 4. The score is now derived directly from the controlled state object, which is always in sync.
    return currentSelectedOptionObject.code
}
