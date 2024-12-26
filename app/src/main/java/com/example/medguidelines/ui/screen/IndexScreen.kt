package com.example.medguidelines.ui.screen

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.medguidelines.data.LAYOUT_PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.internal.NopCollector.emit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

//@Parcelize
//data class ListItemData(val nameResId: Int, val onClick: () -> Unit) : Parcelable

@Serializable
data class ListItemData(
    @SerialName("nameResId")
    val nameResId: Int,
    @SerialName("onClick")
    val onClick: () -> Unit
)


//var dataStore : DataStore<Preferences> by preferencesDataStore(
//    name = USER_PREFERENCES_NAME
//)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val INDEX_SEQUENCE = stringPreferencesKey("index_sequence")


suspend fun saveIsFirstDataStore(context: Context, itemSequence: String) {
    context.dataStore.edit { settings ->
        settings[INDEX_SEQUENCE] = itemSequence
    }
}



@Composable
fun IndexScreen(
    viewModel: IndexScreenViewModel,
    navigateToChildPugh: () -> Unit,
    navigateToAdrop: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val items = rememberSaveable {
            mutableListOf(
                ListItemData(R.string.childPughTitle, navigateToChildPugh),
                ListItemData(R.string.aDropTitle, navigateToAdrop)
        )
    }

    var json = Json.encodeToString(items)

    LaunchedEffect(Unit) {
        context.dataStore.data
            .map { preferences ->
                preferences[INDEX_SEQUENCE] ?: json
            }
            .collect {
                json = it
            }
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
                        json = context.dataStore.map { preferences ->
                            preferences[INDEX_SEQUENCE] ?: json
                        }
                        var items = Json.decodeFromString<ListItemData>(json)

                        items.remove(itemData)
                        items.add(0, itemData)

                        json = Json.encodeToString(items)

                        scope.launch {
                            saveIsFirstDataStore(context, json)
                        }


                        // Save the updated list to SharedPreferences

                        // Execute the original onClick action
                        itemData.onClick()
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

