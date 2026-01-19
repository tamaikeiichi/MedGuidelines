package com.keiichi.medguidelines.ui.component

import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ScrollState // 追加

@Composable
fun VerticalLazyScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    // 画面に表示されているアイテムがある場合のみ計算
    val layoutInfo = listState.layoutInfo
    val firstVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull() ?: return

    val totalItemsCount = layoutInfo.totalItemsCount
    val viewportHeight = layoutInfo.viewportSize.height.toFloat()

    // 全体の推定高さを計算
    val estimatedFullHeight = if (totalItemsCount > 0) {
        val averageItemHeight = layoutInfo.visibleItemsInfo.map { it.size }.average().toFloat()
        averageItemHeight * totalItemsCount
    } else 0f

    if (estimatedFullHeight <= viewportHeight) return // コンテンツが画面内なら表示しない

    BoxWithConstraints(
        modifier = modifier
            .width(12.dp)
            .padding(vertical = 2.dp)
    ) {
        val viewHeight = constraints.maxHeight.toFloat()

        // つまみの高さ
        val thumbHeight = (viewportHeight / estimatedFullHeight * viewHeight)
            .coerceAtLeast(with(LocalDensity.current) { 32.dp.toPx() })

        // つまみの位置
        val scrollOffset =
            listState.firstVisibleItemIndex * (estimatedFullHeight / totalItemsCount) +
                    listState.firstVisibleItemScrollOffset
        val thumbOffset = (scrollOffset / estimatedFullHeight) * viewHeight

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




@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewVerticalLazyScrollbar() {
    MaterialTheme {
        Surface(color = Color.White) {
            val listState = rememberLazyListState()
            // ダミーデータ 50件
            val dummyItems = (1..50).map { "Item $it" }

            Box(modifier = Modifier.fillMaxSize()) {
                // リスト表示
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(dummyItems) { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }

                // スクロールバーの確認
                VerticalLazyScrollbar(
                    listState = listState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}