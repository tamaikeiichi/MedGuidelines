package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.cardModifier(): Modifier =
    this
        .padding(4.dp)
        .fillMaxWidth()