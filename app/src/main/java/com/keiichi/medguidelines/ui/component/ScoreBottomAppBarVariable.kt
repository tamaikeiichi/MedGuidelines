package com.keiichi.medguidelines.ui.component

// Inside your ScoreBottomAppBar.kt (or wherever it's defined)

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding // Ensure this is imported
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScoreBottomAppBarVariable(
    displayText: AnnotatedString,
    fontSize: TextUnit = 30.sp,
    modifier: Modifier = Modifier,
    // Other parameters like backgroundColor, contentColor, etc.
    paddingValues: Dp = 16.dp // Default padding around the text
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // Define the style for measurement (should match your Text composable's style)
    val textStyle = TextStyle(
        fontSize = fontSize,
        // Add other style attributes if they affect height:
        // fontFamily = ..., fontWeight = ..., lineHeight = ...
    )

    // Measure the text. You need to provide constraints.
    // For width, you might use the available width of the BottomAppBar.
    // For simplicity, let's assume it can take as much width as it needs for now,
    // or you can pass a max width constraint.
//    val textLayoutResult: TextLayoutResult = remember(displayText, textStyle, density) {
//        textMeasurer.measure(
//            text = displayText,
//            style = textStyle,
//            constraints = Constraints(maxWidth = Constraints.Infinity) // Adjust if you have a fixed width
//        )
//    }
    // Use BoxWithConstraints to get the available width for text measurement
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) { // The modifier here sets up the context for maxWidth
        val availableWidthPx = constraints.maxWidth // maxWidth from BoxWithConstraints in pixels

        val textLayoutResult: TextLayoutResult =
            remember(displayText, textStyle, density, availableWidthPx) {
                textMeasurer.measure(
                    text = displayText,
                    style = textStyle,
                    constraints = Constraints(maxWidth = availableWidthPx) // Use the actual available width
                )
            }

        val textHeight = with(density) { textLayoutResult.size.height.toDp() }
        val barHeight = textHeight + (paddingValues * 2) // Add padding above and below text

        ResultBottomAppBar(
            modifier = modifier.height(barHeight), // Set the dynamic height
            // contentColor = ...,
            // containerColor = ...
        ) {
            Text(
                text = displayText,
                fontSize = fontSize,
                style = textStyle, // Apply the same style
                textAlign = TextAlign.Center,
                lineHeight = 1.2.em,
                modifier = Modifier.padding(horizontal = paddingValues) // Horizontal padding for text
            )
        }
    }
}

@Preview
@Composable
fun ScoreBottomAppBarVariablePreview() {
    ScoreBottomAppBarVariable(
        displayText = AnnotatedString("Mild")
    )
}