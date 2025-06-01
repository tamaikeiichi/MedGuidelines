package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MedGuidelinesFlowRow (
    modifier: Modifier = Modifier,
    content: @Composable FlowRowScope.() -> Unit
) {
    FlowRow(
        modifier = Modifier
        .padding(4.dp)
        then(modifier),
        itemVerticalAlignment = Alignment.Bottom,
        content = content
    )
}