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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
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
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

private object PreferencesKeys {
    val SHOW_COMPLETED = stringPreferencesKey("show_completed")
}

//fun updateShowCompleted(showCompleted: String) {
//    context.dataStore.edit { preferences ->
//        preferences[PreferencesKeys.SHOW_COMPLETED] = showCompleted
//    }
//}

@Composable
fun IndexScreen(
    viewModel: IndexScreenViewModel,
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

                        val LAYOUT_PREFERENCES_NAME = "layout_preferences"
                        val json = Json.encodeToString(items)
                        val jsonPreferencesKey  = stringPreferencesKey(json)



                        // Save the updated list to SharedPreferences


                        saveLayoutToPreferencesStore(json, requireContext())

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

