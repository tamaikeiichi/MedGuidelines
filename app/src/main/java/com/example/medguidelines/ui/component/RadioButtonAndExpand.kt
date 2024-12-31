package com.example.medguidelines.ui.component

import android.text.Html
import android.text.Spanned
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.example.medguidelines.data.labDataNames
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle

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
            //        ),
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
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
//                            text = AnnotatedString(
//                                Html.fromHtml(titleNote, FROM_HTML_MODE_LEGACY).toString()
 //                           ),
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


@Composable
fun parseStyledString(stringResId: Int): AnnotatedString {
    val fullString = stringResource(id = stringResId)
    return buildAnnotatedString {
        val boldRegex = Regex("<b>(.*?)</b>")
        val underlineRegex = Regex("<u>(.*?)</u>")
        val customBoldRegex = Regex("<bold>(.*?)</bold>")
        val customBoldRegex2 = Regex("\\*\\*(.*?)\\*\\*")
        val customItalicRegex = Regex("\\*(.*?)\\*")

        val tagRanges = mutableListOf<Pair<IntRange, SpanStyle>>()
        val processedRanges = mutableListOf<IntRange>()

        // Find all tag ranges and their styles
        boldRegex.findAll(fullString).forEach { matchResult ->
            tagRanges.add(matchResult.range to SpanStyle(fontWeight = FontWeight.Bold))
        }
        underlineRegex.findAll(fullString).forEach { matchResult ->
            tagRanges.add(matchResult.range to SpanStyle(textDecoration = TextDecoration.Underline))
        }
        customBoldRegex.findAll(fullString).forEach { matchResult ->
            tagRanges.add(matchResult.range to SpanStyle(fontWeight = FontWeight.Bold))
        }
        customBoldRegex2.findAll(fullString).forEach { matchResult ->
            tagRanges.add(matchResult.range to SpanStyle(fontWeight = FontWeight.Bold))
        }
        customItalicRegex.findAll(fullString).forEach { matchResult ->
            tagRanges.add(matchResult.range to SpanStyle(fontStyle = FontStyle.Italic))
        }

        // Sort tag ranges by their starting position
        tagRanges.sortBy { it.first.first }

        var currentIndex = 0
        for ((range, style) in tagRanges) {
            // Append the text before the tag
            if (range.first > currentIndex) {
                val textBeforeTag = fullString.substring(currentIndex, range.first)
                var isProcessed = false
                for (processedRange in processedRanges) {
                    if (processedRange.contains(range.first)) {
                        isProcessed = true
                        break
                    }
                }
                if (!isProcessed) {
                    append(textBeforeTag)
                }
            }

            // Append the styled text
            val tagContent = fullString.substring(range.first + 3, range.last - 3)
            var isProcessed = false
            for (processedRange in processedRanges) {
                if (processedRange.contains(range.first)) {
                    isProcessed = true
                    break
                }
            }
            if (!isProcessed) {
                withStyle(style = style) {
                    append(tagContent)
                }
            }

            // Update the current index
            currentIndex = range.last + 1
            processedRanges.add(range)
        }

        // Append the remaining text
        if (currentIndex < fullString.length) {
            append(fullString.substring(currentIndex))
        }
    }
}