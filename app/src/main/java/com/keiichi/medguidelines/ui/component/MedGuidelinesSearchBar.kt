package com.keiichi.medguidelines.ui.component

import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
//import androidx.compose.material3.SearchView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class) // SearchBar is experimental in M3
@Composable
fun MyCustomSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit, // Callback for when search is triggered (e.g., keyboard action)
    placeholderText: Int = R.string.search, // Placeholder text
) {
    val focusManager = LocalFocusManager.current

    //SearchView(){}
    val onActiveChange = { /* Handle active state change if needed */ }
    // Content for the search results screen when 'active' is true
    // If you're not using the 'active' state for a separate results view,
    // this content might not be shown or relevant.
//    SearchBar(
//        inputField = {
//            SearchBarDefaults.InputField(
//                query = searchQuery,
//                onQueryChange = onSearchQueryChange, // Renamed for clarity with M3 SearchBar API
//                onSearch = {
//                    onSearch(searchQuery) // Execute search action
//                    focusManager.clearFocus() // Optionally clear focus on search execution
//                },
//                expanded = false, // Set to true if you want a separate search results screen
//                //onExpandedChange = onActiveChange,
//                //enabled = COMPILED_CODE,
//                placeholder = { Text(text = parseStyledString(placeholderText)) },
//                //leadingIcon = COMPILED_CODE,
//                trailingIcon = {
//                    if (searchQuery.isNotEmpty()) {
//                        IconButton(onClick = {
//                            onSearchQueryChange("") // Clear the search query
//                            focusManager.clearFocus()    // Remove focus and hide keyboard
//                        }) {
//                            Icon(
//                                imageVector = Icons.Default.Clear,
//                                contentDescription = "Clear search"
//                            )
//                        }
//                    }
//                },
//                //tonalElevation = 40.dp,
//                //shadowElevation = 4.dp
////                colors = COMPILED_CODE.inputFieldColors,
////                interactionSource = COMPILED_CODE,
//            )
//        },
//        expanded = false,
//        onExpandedChange = onActiveChange,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//                then(modifier),
////        shape = COMPILED_CODE,
////        colors = COMPILED_CODE,
////        tonalElevation = COMPILED_CODE,
////        shadowElevation = COMPILED_CODE,
////        windowInsets = COMPILED_CODE,
//        content = fun ColumnScope.() {
//            // Content for the search results screen when 'active' is true
//            // If you're not using the 'active' state for a separate results view,
//            // this content might not be shown or relevant.
//        },
//    )
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
                then(modifier)
        ,
        query = searchQuery,
        onQueryChange = onSearchQueryChange, // Renamed for clarity with M3 SearchBar API
        onSearch = {
            onSearch(searchQuery) // Execute search action
            focusManager.clearFocus() // Optionally clear focus on search execution
        },
        active = false, // Set to true if you want a separate search results screen
        onActiveChange = { /* Handle active state change if needed */ },

        placeholder = { Text(parseStyledString(placeholderText)) },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = {
                    onSearchQueryChange("") // Clear the search query
                    focusManager.clearFocus()    // Remove focus and hide keyboard
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        //tonalElevation = 40.dp,
        //shadowElevation = 4.dp
    ) {
        // Content for the search results screen when 'active' is true
        // If you're not using the 'active' state for a separate results view,
        // this content might not be shown or relevant.
    }
}

@Preview(showBackground = true)
@Composable
fun MyCustomSearchBarPreview() {
    var query by remember { mutableStateOf("Sample search") }
    MyCustomSearchBar(
        searchQuery = query,
        onSearchQueryChange = { query = it },
        onSearch = { /* Preview: handle search action */ }
    )
}

@Preview(showBackground = true)
@Composable
fun MyCustomSearchBarEmptyPreview() {
    var query by remember { mutableStateOf("") }
    MyCustomSearchBar(
        searchQuery = query,
        onSearchQueryChange = { query = it },
        onSearch = { /* Preview: handle search action */ }
    )
}