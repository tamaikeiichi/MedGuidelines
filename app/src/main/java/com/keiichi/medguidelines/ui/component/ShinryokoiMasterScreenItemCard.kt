package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.keiichi.compose.DarkYellow
import com.keiichi.compose.Yellow
import com.keiichi.medguidelines.data.PairedTextItem

@Composable
fun ShinryokoiMasterScreenItemCard(
    pairedItem: PairedTextItem,
    isFavorite: Boolean,
    onFavoriteClick: (item: PairedTextItem) -> Unit, // Callback for star click
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimensions.cardPadding),
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // To push star to the end
            ) {
                Text(
                    text = pairedItem.kanjiMeisho?.toString() ?: "N/A",
                    modifier = Modifier
                        .weight(4f)
                        .padding(Dimensions.textPadding),
                )
                val displayTensu = pairedItem.tensu.removeSuffix(".00")
                Text(
                    text = "${displayTensu}" +
                            if (pairedItem.tensuShikibetsu == "1") {
                                "円"
                            } else if (pairedItem.tensuShikibetsu == "4") {
                                "（購入価格）"
                            }
                            else if (pairedItem.tensuShikibetsu == "5") {
                                "％加算"
                            } else if (pairedItem.tensuShikibetsu == "6") {
                                "％減算"
                            } else if (pairedItem.tensuShikibetsu == "7") {
                                "（減点診療）"
                            }
                            else {
                                "点"
                            },
                    modifier = Modifier
                        .weight(1.3f)
                        .padding(Dimensions.textPadding),
                    textAlign = TextAlign.End
                )
                IconButton(onClick = { onFavoriteClick(pairedItem) }) { // Star icon
                    val starColor = if (isFavorite) {
                        if (isSystemInDarkTheme()) {
                            DarkYellow // Use your defined dark yellow for dark theme
                        } else {
                            Yellow // Standard yellow for light theme
                        }
                    } else {
                        LocalContentColor.current // Or MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                    }
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (isFavorite) "Unmark as favorite" else "Mark as favorite",
                        tint = if (isFavorite) starColor else LocalContentColor.current,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}