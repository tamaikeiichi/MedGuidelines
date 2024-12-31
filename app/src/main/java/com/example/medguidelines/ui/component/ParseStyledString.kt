package com.example.medguidelines.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle


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