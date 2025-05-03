package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MedGuidelinesCard (
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable () -> Unit,
){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(color)
    ){
            content()

    }
}