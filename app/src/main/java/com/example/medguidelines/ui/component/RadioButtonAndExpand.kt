package com.example.medguidelines.ui.component

import android.text.Html
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.example.medguidelines.data.labDataNames

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RadioButtonAndExpand(
    radioOptions: List<labDataNames>,
    selectedOption: labDataNames,
    onOptionSelected: (selectedOption: labDataNames) -> Unit,
    title: String,
    titleNote: Int,
) {
    Column() {
        Card(
//            colors = CardDefaults.cardColors(
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            //        )

            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp),


        ) {
            var expanded by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(5.dp)
                ) {
                    Text(
                        text = AnnotatedString(
                            Html.fromHtml(title, FROM_HTML_MODE_LEGACY).toString()
                        ),
                        Modifier
                            .padding(10.dp)
                    )
                    if (expanded) {
                        Text(
                            text = parseStyledString(titleNote),
                            Modifier
                                .padding(2.dp)
                        )
                    }
                }
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
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = stringResource(id = text.stringid),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp),
                    softWrap = true,
                )
            }
        }
    }
}

