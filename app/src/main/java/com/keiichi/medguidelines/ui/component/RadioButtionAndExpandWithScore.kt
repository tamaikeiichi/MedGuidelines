package com.keiichi.medguidelines.ui.component

import androidx.annotation.StringRes // Added for @StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
// import androidx.compose.runtime.saveable.rememberSaveable // Not used in this version for expanded state
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.LabelAndScore


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RadioButtonAndExpandWithScore(
    options: List<LabelAndScore>, // Changed from radioOptions: List<Int>
    selectedOption: LabelAndScore, // Changed from selectedOption: Int
    onOptionSelected: (selectedOption: LabelAndScore) -> Unit, // Changed callback type
    title: Int, // R.string resource for the title
    titleNote: Int, // R.string resource for the note, R.string.space if no note
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    // Using R.string.space as a sentinel for "no note" is a bit fragile.
    // Consider using a nullable Int (Int?) for titleNote and checking for null.
    val hasTitleNote = (titleNote != R.string.space)

    val cardModifier = Modifier
        .padding(Dimensions.cardPadding) // Ensure Dimensions.cardPadding is defined
        .then(
            if (hasTitleNote) {
                Modifier.clickable { expanded = !expanded }
            } else {
                Modifier // No clickable modifier if no note to expand/collapse
            }
        )

    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(containerColor = cardColor) // Use containerColor for M3
    ) {
        Column { // Removed unnecessary extra Column
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Make row take full width for better alignment
                    .padding(vertical = 2.dp, horizontal = 8.dp) // Adjusted padding
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically // Center title and icon
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // Add some padding before the icon
                ) {
                    // Ensure parseStyledString is available and works as expected
                    if (title != R.string.space) { // Assuming R.string.space means "no title"
                        Text(
                            text = parseStyledString(title), // Using stringResource directly for simplicity
                            // Replace with parseStyledString(title) if styling is needed
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = if (expanded && hasTitleNote) 4.dp else 0.dp)
                        )
                    }
                    if (expanded && hasTitleNote) {
                        Text(
                            text = parseStyledString(titleNote), // Using stringResource directly
                            // Replace with parseStyledString(titleNote) if styling is needed
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                if (hasTitleNote) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            // Radio buttons section
            FlowRow(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp), // Added padding
                horizontalArrangement = Arrangement.Start
            ) {
                options.forEach { option -> // Iterate over List<ScoreOption>
                    Row(
                        Modifier
                            .selectable(
                                selected = (option == selectedOption), // Compare ScoreOption objects
                                onClick = { onOptionSelected(option) }, // Pass the selected ScoreOption
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 4.dp, vertical = 8.dp), // Increased vertical padding
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null // Recommended: onClick for RadioButton itself is null when Row is selectable
                        )
                        Text(
                            text = parseStyledString(option.labelResId), // Use labelResId from ScoreOption
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp), // Increased start padding
                            softWrap = true,
                        )
                    }
                }
            }
            appendixLabel?.invoke()
        }
    }
}

