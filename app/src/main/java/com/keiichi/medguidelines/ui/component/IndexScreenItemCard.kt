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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keiichi.compose.DarkYellow
import com.keiichi.compose.Yellow
import com.keiichi.medguidelines.R
import androidx.compose.foundation.layout.offset


@Composable
fun IndexScreenItemCard(
    @StringRes name: Int,
    currentAlpha: Float =1f,
    isFavorite: Boolean,
    isLoading: MutableState<Boolean>,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit, // Callback for star click
    modifier: Modifier = Modifier
) {
    Column {
        Card (
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    Dimensions.cardPadding
                )
                .clickable(onClick = onItemClick),
//            elevation = CardDefaults.cardElevation(
//                defaultElevation = Dimensions.cardElevation
//            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // To push star to the end
            ) {
                Text(
                    text = parseStyledString(name),
                    fontSize = 25.sp,
                    lineHeight = 29.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .weight(1f)
                )
                // Conditional icon: Loading indicator or Favorite star
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(30.dp)
                            .offset(x = (-8).dp), // Match star size
                        color = MaterialTheme.colorScheme.primary, // Or your desired color
                        strokeWidth = 3.dp
                    )
                } else {
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
                            tint = if (isFavorite) starColor else LocalContentColor.current,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
//        Spacer(
//            modifier = Modifier.height(4.dp)
//        )
    }
}

@Preview
@Composable
fun IndexScreenItemCardPreview(){
    Column {
        IndexScreenItemCard(
            name = R.string.childPughTitle,
            currentAlpha = 1f,
            isFavorite = false,
            isLoading = remember { mutableStateOf(false) }, // Preview not loading
            onItemClick = {},
            onFavoriteClick = {}
        )
        Spacer(Modifier.height(8.dp))
        IndexScreenItemCard(
            name = R.string.ikashiRinryokuMasterKensaku, // Example of the item that might load
            currentAlpha = 1f,
            isFavorite = true,
            isLoading = remember { mutableStateOf(true) }, // Preview loading
            onItemClick = {},
            onFavoriteClick = {}
        )
    }
}