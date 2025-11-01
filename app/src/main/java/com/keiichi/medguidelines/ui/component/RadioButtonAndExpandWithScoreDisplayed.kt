package com.keiichi.medguidelines.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.minDimension
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.sp
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.LabelAndScore


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RadioButtonAndExpandWithScoreDisplayed(
    options: List<LabelAndScore>, // Changed from radioOptions: List<Int>
    selectedOption: LabelAndScore, // Changed from selectedOption: Int
    onOptionSelected: (selectedOption: LabelAndScore) -> Unit, // Changed callback type
    title: Int, // R.string resource for the title
    titleNote: Int, // R.string resource for the note, R.string.space if no note
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable (() -> Unit)? = null,
    isNumberDisplayed: Boolean = true
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
                    val isSelected = (option == selectedOption)
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
// --- Start of Custom Overlay RadioButton ---
                        val radioButtonSize = 24.dp
                        Box(
                            modifier = Modifier.size(radioButtonSize), // Standard RadioButton touch target size
                            //contentAlignment = Alignment.TopCenter
                        ) {
                            // Layer 1: The RadioButton itself
                            ThinRadioButton(
                                selected = isSelected,
                                strokeWidth = 1.dp,
                                size = radioButtonSize
                            )
                            // Layer 2: The overlayed Text, shown only if the score is not 0
                           if (isNumberDisplayed) {
                               Box(
                                   modifier = Modifier
                                       .fillMaxSize()
                                       .padding(bottom = 2.dp)


                                   ,
                                   contentAlignment = Alignment.TopCenter
                               ) {
                                   Text(
                                       modifier = Modifier
                                           //.padding(bottom = 1.dp)
                                        ,
                                           //.wrapContentSize(),
                                       //.offset(y = (-1).dp),
                                       text = "${option.score}",
                                       color = if (isSelected) {
                                           // Change text color to be visible on the selected radio button color
                                           MaterialTheme.colorScheme.onPrimary
                                       } else {
                                           // Use the default text color when not selected
                                           MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                       },
                                       fontSize = 14.sp, // Small font size to fit inside
                                       //textAlign = TextAlign.Center,
                                       //lineHeight = TextUnit.Unspecified
                                   )
                              }
                            }
                        }
                        // --- End of Custom Overlay RadioButton ---

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

@Composable
fun ThinRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp, // The overall size of the radio button
    strokeWidth: Dp = 1.dp, // The thickness of the outer circle
    dotRadiusRatio: Float = 1.0f // Ratio of the dot's radius to the outer circle's radius
) {
    val radioColor: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Canvas(modifier = modifier.size(size)) {
        val radius = (size.toPx() / 2) //- (strokeWidth.toPx() / 1)

        // Draw the outer circle (the border)
        drawCircle(
            color = radioColor,
            radius = radius - (strokeWidth.toPx() / 2),
            style = Stroke(width = strokeWidth.toPx())
        )

        // Draw the inner dot if selected
        if (selected) {
            drawCircle(
                color = radioColor,
                radius = radius * dotRadiusRatio
            )
        }
    }
}

@Preview
@Composable
fun RadioButtonAndExpandWithScoreDisplayedPreview(){
    val previewOptions = listOf(
        LabelAndScore(R.string.none, 0),
        LabelAndScore(R.string.mild, 1),
        LabelAndScore(R.string.severe, -3)
    )
    var selected by remember { mutableStateOf(previewOptions[1]) }

    RadioButtonAndExpandWithScoreDisplayed(
        options = previewOptions,
        selectedOption = selected,
        onOptionSelected = { selected = it },
        title = R.string.wilsonTitle, // Replace with your actual string resource
        titleNote = R.string.wilsonTitle, // Replace with your actual string resource
    )
}