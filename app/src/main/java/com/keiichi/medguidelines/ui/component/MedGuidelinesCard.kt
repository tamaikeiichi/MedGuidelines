package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MedGuidelinesCard (
    modifier: Modifier = Modifier,
        //.cardModifier(),
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit,
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.cardPadding),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ){
            content()

    }
}