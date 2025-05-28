package com.keiichi.medguidelines.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppDimensions(
    val lazyColumnPadding: Dp = 4.dp,
    val cardPadding: Dp = 2.dp,
    val textPadding: Dp = 4.dp
)

// Provide a CompositionLocal to access AppDimensions throughout your app
val LocalAppDimensions = staticCompositionLocalOf { AppDimensions() }

// Example of how to use it in a Composable
val Dimensions: AppDimensions
    @Composable
    get() = LocalAppDimensions.current