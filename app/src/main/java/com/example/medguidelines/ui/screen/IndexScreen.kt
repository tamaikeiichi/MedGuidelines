package com.example.medguidelines.ui.screen

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.medguidelines.R
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListItemData(val nameResId: Int, val onClick: () -> Unit) : Parcelable


@Composable
fun IndexScreen(
    navigateToChildPugh: () -> Unit,
    navigateToAdrop: () -> Unit,
) {
    val items = rememberSaveable {
            mutableListOf(
                ListItemData(R.string.childPughTitle, navigateToChildPugh),
                ListItemData(R.string.aDropTitle, navigateToAdrop)
        )
    }

    Column(){
        SearchBar()
        LazyColumn(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
        ) {
            items(items){ itemData ->
                ListItem(
                    name = stringResource(id = itemData.nameResId),
                    onClick = {
                        // Move clicked item to the top
                        items.remove(itemData)
                        items.add(0, itemData)

                        // Execute the original onClick action
                        itemData.onClick()
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    TextField(
        value = "",
        onValueChange = {}
    ,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.indexScreen_searchbar))
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Preview
@Composable
fun IndexScreenPreview(){
    IndexScreen(navigateToChildPugh = {}, navigateToAdrop = {})
}

