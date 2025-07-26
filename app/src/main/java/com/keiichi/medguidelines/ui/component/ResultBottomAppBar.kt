package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ResultBottomAppBar(
    modifier: Modifier = Modifier,
    barHeight: Dp = 100.dp,
    content: @Composable () -> Unit = {}
) {
    BottomAppBar(
        modifier = modifier.height(barHeight),
        contentColor = MaterialTheme.colorScheme.primaryContainer,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun ResultBottomAppBarPreview() {
    ResultBottomAppBar()
}