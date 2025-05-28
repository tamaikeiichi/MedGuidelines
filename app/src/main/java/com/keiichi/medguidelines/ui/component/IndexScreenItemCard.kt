package com.keiichi.medguidelines.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.CardDefaults
import com.keiichi.compose.DarkYellow
import com.keiichi.compose.Yellow
import com.keiichi.medguidelines.R

@Composable
fun IndexScreenItemCard(
//    name: Int,
//    onClick: () -> Unit,
//    currentAlpha: Float
    @StringRes name: Int,
    currentAlpha: Float,
    isFavorite: Boolean,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit, // Callback for star click
    modifier: Modifier = Modifier
) {
    Column {
        Card (
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { onClick() }
//                .alpha(currentAlpha)
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    Dimensions.cardPadding
                    //vertical = 2.dp
                )
                .alpha(currentAlpha)
                .clickable(onClick = onItemClick), // Keep the main item clickable
            //elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            val context = LocalContext.current
//            Text(
//                text = parseStyledString(name),
//                fontSize = 25.sp,
//                modifier = Modifier
//                    .padding(8.dp)
//                    .fillMaxWidth()
//            )
            Row(
                modifier = Modifier
                    //.padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // To push star to the end
            ) {
                Text(
                    text = parseStyledString(name),
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .weight(1f)
                    //style = MaterialTheme.typography.titleMedium,
                    //modifier = Modifier.weight(1f) // Text takes available space
                )
                IconButton(onClick = onFavoriteClick) { // Star icon
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
                        tint = if (isFavorite) starColor else LocalContentColor.current
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}

@Preview
@Composable
fun IndexScreenItemCardPreview(){
    IndexScreenItemCard(
        name = R.string.childPughTitle,
        currentAlpha = 1f,
        isFavorite = false,
        onItemClick = {},
        onFavoriteClick = {}
    )
}