package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp


// --- 通常の Column (ScrollState) 用 ---
@Composable
fun VerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .width(12.dp)
            .padding(vertical = 2.dp)
    ) {
        val viewHeight = constraints.maxHeight.toFloat()
        // 全体の高さ = 現在の最大スクロール可能量 + 表示領域の高さ
        val fullHeight = scrollState.maxValue.toFloat() + viewHeight

        if (fullHeight <= viewHeight) return@BoxWithConstraints

        // つまみの高さ
        val thumbHeight = ((viewHeight / fullHeight) * viewHeight)
            .coerceAtLeast(with(LocalDensity.current) { 32.dp.toPx() })

        // つまみの位置
        val thumbOffset = (scrollState.value.toFloat() / scrollState.maxValue) * (viewHeight - thumbHeight)

        Box(
            modifier = Modifier
                .offset { IntOffset(0, thumbOffset.toInt()) }
                .size(width = 10.dp, height = with(LocalDensity.current) { thumbHeight.toDp() })
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
    }
}