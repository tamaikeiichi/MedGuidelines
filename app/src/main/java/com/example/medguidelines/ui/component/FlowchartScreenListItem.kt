package com.example.medguidelines.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlowchartScreenListItem (singleFlowchartPanel: () -> Unit) {
    Column {
        Card {
            singleFlowchartPanel()
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}