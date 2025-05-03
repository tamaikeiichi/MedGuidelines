package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ResultBottomAppBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    BottomAppBar(
        modifier = modifier,
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