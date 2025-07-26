package com.keiichi.medguidelines.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun ScoreBottomAppBar(
    displayText: AnnotatedString,
    modifier: Modifier = Modifier,
    barHeight: Dp = 100.dp,
    fontSize: TextUnit = 30.sp
) {
    ResultBottomAppBar(
        modifier = modifier,
        barHeight = barHeight
    ) {
        Text(
            text = displayText,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            lineHeight = 1.2.em
        )
    }
}

@Preview
@Composable
fun ScoreBottomAppBarPreview() {
    ScoreBottomAppBar(
        displayText = AnnotatedString("Mild")
    )
}