package com.example.medguidelines.ui.screen

import android.content.Context
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class ActionType {
    NAVIGATE_TO_CHILD_PUGH,
    NAVIGATE_TO_ADROP
}

@Parcelize
@Serializable
data class ListItemData(
    val nameResId: Int,
    val actionType: ActionType
) : Parcelable

val itemsList = listOf(
    ListItemData(R.string.childPughTitle, ActionType.NAVIGATE_TO_CHILD_PUGH),
    ListItemData(R.string.aDropTitle, ActionType.NAVIGATE_TO_ADROP)
)

// Usage with DataStore:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val LIST_ITEM_DATA_KEY = stringPreferencesKey("list_item_data")

suspend fun saveListItemData(context: Context, item: MutableList<ListItemData>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[LIST_ITEM_DATA_KEY] = jsonString
    }
}
//
fun loadListItemData(context: Context): Flow<ListItemData?> {
    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_ITEM_DATA_KEY]
        if (jsonString != null) {
            Json.decodeFromString<ListItemData>(jsonString)
        } else {
            null
        }
    }
}

@Composable
fun IndexScreen(
    //viewModel: IndexScreenViewModel,
    navigateToChildPugh: () -> Unit,
    navigateToAdrop: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val items = rememberSaveable {
        itemsList.toMutableList()
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
                        scope.launch {
                            loadListItemData(context)
                        }

                        items.remove(itemData)
                        items.add(0, itemData)

                        scope.launch {
                            saveListItemData(context, items)
                        }
                        when (itemData.actionType) {
                            ActionType.NAVIGATE_TO_CHILD_PUGH -> navigateToChildPugh()
                            ActionType.NAVIGATE_TO_ADROP -> navigateToAdrop()
                        }
                    }
                )
            }
        }
    }
}


//suspend fun saveLayoutToPreferencesStore(isLinearLayoutManager: String, context: Context) {
//    context.dataStore.edit { preferences ->
//        preferences[LAYOUT_PREFERENCES_NAME] = isLinearLayoutManager
//    }
//}

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

