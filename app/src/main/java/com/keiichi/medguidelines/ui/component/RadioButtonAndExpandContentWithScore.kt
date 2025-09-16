package com.keiichi.medguidelines.ui.component

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
fun RadioButtonAndExpandContentWithScore(
    options: List<LabelAndScore>,
    selectedOption: LabelAndScore,
    onOptionSelected: (selectedOption: LabelAndScore) -> Unit,
    title: Int,
    // Change titleNote to be a composable lambda
    expandedContent: @Composable (() -> Unit)? = null, // Renamed for clarity
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    appendixLabel: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    // Determine if there's content to expand
    val hasExpandableContent = (expandedContent != null)

    val cardModifier = Modifier
        .padding(Dimensions.cardPadding)
        .then(
            if (hasExpandableContent) {
                Modifier.clickable { expanded = !expanded }
            } else {
                Modifier
            }
        )

    Card(
        modifier = cardModifier,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column {
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
                            modifier = Modifier.padding(bottom = if (expanded && hasExpandableContent) 4.dp else 0.dp)
                        )
                    }
                    // Invoke the provided composable for expanded content
                    if (expanded && hasExpandableContent) {
                        expandedContent?.invoke() // Call the composable lambda
                    }
                }
                if (hasExpandableContent) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            FlowRow(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                options.forEach { option ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { onOptionSelected(option) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null
                        )
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
