package com.keiichi.medguidelines.ui.component

// Import Checkbox and other necessary components
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.medguidelines.data.LabelAndScore
import com.keiichi.medguidelines.R

@Composable
fun NumberedCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    score: Int,
    modifier: Modifier = Modifier,
    isNumberDisplayed: Boolean = false
) {
    val checkboxSize = 24.dp
    Box(
        modifier = modifier.size(checkboxSize),
        contentAlignment = Alignment.Center
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                // Make the checkmark color visible on the primary background
                checkedColor = MaterialTheme.colorScheme.primary,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        // Overlay the score number
        if (isNumberDisplayed) {
            Text(
                text = "$score",
                color = if (checked) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                style = TextStyle(
                    fontSize = 12.sp, // A bit smaller to fit better in a checkbox
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckboxesAndExpandWithScore( // Renamed the function
    optionsWithScores: List<LabelAndScore>,
    defaultSelectedOption: List<LabelAndScore>,
    onOptionSelected: (selectedOptions: List<LabelAndScore>) -> Unit,
    title: Int,
    titleNote: Int = R.string.space,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable (() -> Unit)? = null,
    isNumberDisplayed: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val hasTitleNote = (titleNote != R.string.space)
    val cardModifier = Modifier
        .padding(Dimensions.cardPadding)
        .then(
            if (hasTitleNote) Modifier.clickable {
                expanded = !expanded
            } else Modifier
        )

    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column {
            // ... (The top part with Title and Expand Icon remains the same)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 8.dp)
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    if (title != R.string.space) {
                        Text(
                            text = parseStyledString(title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = if (expanded && hasTitleNote) 4.dp else 0.dp)
                        )
                    }
                    if (expanded && hasTitleNote) {
                        Text(
                            text = parseStyledString(titleNote),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                if (hasTitleNote) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }


            // --- Checkboxes section ---
            FlowRow(
                Modifier
                    // Removed .selectableGroup() as it's more for radio buttons
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                optionsWithScores.forEach { option ->
                    // A checkbox is selected if the option is in our list of selected options
                    val isSelected = defaultSelectedOption.contains(option)

                    Row(
                        Modifier
                            .clickable(
                                // Use clickable instead of selectable for Checkboxes
                                onClick = {
                                    // 3. CLICK HANDLER CHANGE: Add or remove from the list
                                    val newSelection = defaultSelectedOption.toMutableList().apply {
                                        if (isSelected) {
                                            remove(option)
                                        } else {
                                            add(option)
                                        }
                                    }
                                    onOptionSelected(newSelection)
                                },
                                role = Role.Checkbox, // 4. SEMANTICS CHANGE: Role is now Checkbox
                            )
                            .padding(
                                horizontal = 4.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- UI Component Change ---
                        // Use the new NumberedCheckbox instead of ThinRadioButton
                        NumberedCheckbox(
                            checked = isSelected,
                            onCheckedChange = null, // onClick is handled by the parent Row
                            score = option.score,
                            isNumberDisplayed = isNumberDisplayed
                        )
                        // --- End of UI Component Change ---

                        Text(
                            text = parseStyledString(option.labelResId),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                            softWrap = true,
                        )
                    }
                }
            }
            appendixLabel?.invoke()
        }
    }
}


@Preview(showBackground = true, name = "Interactive Checkboxes")
@Composable
fun CheckboxesAndExpandWithScorePreview() {
    val previewOptions = listOf(
        LabelAndScore(R.string.fever, 2),
        LabelAndScore(R.string.leukopenia, 3),
        LabelAndScore(R.string.thrombocytopenia, 4),
    )
    var selectedOptions by remember {
        mutableStateOf(listOf(previewOptions[0], previewOptions[1]))
    }
    MedGuidelinesTheme {
        CheckboxesAndExpandWithScore(
            optionsWithScores = previewOptions,
            defaultSelectedOption = selectedOptions,
            onOptionSelected = { newSelection ->
                selectedOptions = newSelection
            },
            title = R.string.hematologic, // Example: <string name="hematologic">Hematologic</string>
            titleNote = R.string.hematologicNote, // Example: <string name="hematologic_note">Select all that apply.</string>
            isNumberDisplayed = false,
        )
    }
}

