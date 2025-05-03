package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FlowchartScreenListItem(singleFlowchartPanel: () -> Unit) {
    Column {
        Card {
            singleFlowchartPanel()
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}