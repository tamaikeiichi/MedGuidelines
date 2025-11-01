package com.keiichi.medguidelines.ui.component

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em

@Composable
fun parseStyledString(@StringRes stringResId: Int): AnnotatedString {
    val fullString = stringResource(id = stringResId)
    return buildAnnotatedString {
        val tagRegexes = listOf(
            TagRegex("<b>", "</b>", SpanStyle(fontWeight = FontWeight.Bold)),
            TagRegex("<i>", "</i>", SpanStyle(fontStyle = FontStyle.Italic)),
            TagRegex(
                "<sub>",
                "</sub>",
                SpanStyle(fontSize = 0.8.em, baselineShift = BaselineShift(-0.2F))
            ),
            TagRegex(
                "<sup>",
                "</sup>",
                SpanStyle(fontSize = 0.8.em, baselineShift = BaselineShift(0.2F))
            ),
            TagRegex("<u>", "</u>", SpanStyle(textDecoration = TextDecoration.Underline)),
            TagRegex("<bold>", "</bold>", SpanStyle(fontWeight = FontWeight.Bold)),
            TagRegex("<small>", "</small>", SpanStyle(fontSize = 0.7.em)),
            TagRegex("\\*\\*", "\\*\\*", SpanStyle(fontWeight = FontWeight.Bold)),
            TagRegex("\\*", "\\*", SpanStyle(fontStyle = FontStyle.Italic))
        )

        val tagRanges = mutableListOf<TagRange>()
        val processedRanges = mutableListOf<IntRange>()

        // Find all tag ranges and their styles
        for (tagRegex in tagRegexes) {
            val regex =
                Regex(Regex.escape(tagRegex.openingTag) + "(.*?)" + Regex.escape(tagRegex.closingTag))
            regex.findAll(fullString).forEach { matchResult ->
                tagRanges.add(
                    TagRange(
                        matchResult.range,
                        tagRegex.openingTag.length,
                        tagRegex.closingTag.length,
                        tagRegex.style
                    )
                )
            }
        }

        // Sort tag ranges by their starting position
        tagRanges.sortBy { it.range.first }

        var currentIndex = 0
        for (tagRange in tagRanges) {
            // Append the text before the tag
            if (tagRange.range.first > currentIndex) {
                val textBeforeTag = fullString.substring(currentIndex, tagRange.range.first)
                append(textBeforeTag)
            }

            // Append the styled text
            val tagContent = fullString.substring(
                tagRange.range.first + tagRange.openingTagLength,
                tagRange.range.last - tagRange.closingTagLength + 1
            )
            withStyle(style = tagRange.style) {
                append(tagContent)
            }

            // Update the current index
            currentIndex = tagRange.range.last + 1
        }

        // Append the remaining text
        if (currentIndex < fullString.length) {
            append(fullString.substring(currentIndex))
        }
    }
}

data class TagRegex(
    val openingTag: String,
    val closingTag: String,
    val style: SpanStyle
)

data class TagRange(
    val range: IntRange,
    val openingTagLength: Int,
    val closingTagLength: Int,
    val style: SpanStyle
)