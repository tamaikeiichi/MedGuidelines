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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RadioButtonAndExpand(
    radioOptions: List<Int>,
    selectedOption: Int,
    onOptionSelected: (selectedOption: Int) -> Unit,
    title: Int,
    titleNote: Int,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
) {
    //var selectedOption by rememberSaveable { mutableStateOf(radioOptions.first()) }
    Column() {
        var expanded by remember { mutableStateOf(false) }
        val cardModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .then(
                if (titleNote != R.string.space) {
                    Modifier.clickable { expanded = !expanded }
                } else {
                    Modifier
                }
            )
        Card(
            modifier = cardModifier,
            colors = CardDefaults.cardColors(cardColor)
        ) {
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(3.dp)
                ) {
                    Text(
                        text = parseStyledString(title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(4.dp)
                    )
                    if (expanded) {
                        Text(
                            text = parseStyledString(titleNote),
                            Modifier
                                .padding(2.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (titleNote != R.string.space) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }

            }
        }

    }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    FlowRow(
        Modifier
            .selectableGroup()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        radioOptions.forEach { text ->
            Row(
                Modifier//.fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = stringResource(id = text),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    softWrap = true,
                )
            }
        }
    }
}

