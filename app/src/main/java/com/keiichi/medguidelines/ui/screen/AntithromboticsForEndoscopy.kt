package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.parseStyledString
import kotlin.text.contains

@Composable
fun AntithromboticsForEndoscopy(navController: NavController) {
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.manegementAntithromboticsForEndoscopy,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.BsgAndESGE, R.string.antithromboticsUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBar(
                displayText = parseStyledString(R.string.space)
            )
        }
    ) { innerPadding ->
        val itemList = listOf("Apple", "Banana", "Cherry", "Date", "Grape", "Lemon", "Mango", "Orange", "Strawberry", "Watermelon")
        // State to hold the selected item in the preview
        var selectedItem by remember { mutableStateOf(itemList[0]) }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            SearchableExposedDropdownMenu(
                items = itemList,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                label = "Select a Fruit"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Currently Selected: $selectedItem")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableExposedDropdownMenu(
    items: List<String>, // The full list of items to choose from
    selectedItem: String, // The currently selected item
    onItemSelected: (String) -> Unit, // Callback for when an item is selected
    label: String = "Select an item" // Optional label for the text field
) {
    // State to control the expanded/collapsed state of the dropdown
    var expanded by remember { mutableStateOf(false) }

    // State to hold the text in the text field. Initialize with the selected item.
    var textFieldValue by remember { mutableStateOf(TextFieldValue(selectedItem)) }

    // Filter the items based on the current text in the text field.
    // remember is used to avoid recalculating the filtered list on every recomposition
    // unless the items list or the text field value changes.
    val filteredItems = remember(items, textFieldValue.text) {
        if (textFieldValue.text.isBlank()) {
            // If the text field is empty, show all items
            items
        } else {
            // Filter items that contain the text field's value (case-insensitive)
            items.filter { it.contains(textFieldValue.text, ignoreCase = true) }
        }
    }

    // The main container for the exposed dropdown menu
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded // Toggle the expanded state
        }
    ) {
        // The text field that serves as the anchor for the dropdown
        OutlinedTextField(
            // menuAnchor() modifier is essential for linking the text field to the menu box
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue // Update the text field value
                expanded = true // Expand the menu when the user starts typing
            },
            label = { Text(label) },
            // Trailing icon for the dropdown arrow
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            // Default colors for the exposed dropdown text field
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true // Typically a single line for dropdowns
        )

        // The dropdown menu itself, appearing below the text field when expanded
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false // Close the menu when dismissed (e.g., by tapping outside)
            }
        ) {
            // Display the filtered items as menu items
            filteredItems.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        // When an item is clicked:
                        textFieldValue = TextFieldValue(selectionOption) // Update text field
                        onItemSelected(selectionOption) // Notify parent composable
                        expanded = false // Close the menu
                    },
                    // Use default content padding for menu items
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}


@Preview
@Composable
fun AntithromboticsForEndoscopyPreview() {
    AntithromboticsForEndoscopy(navController = NavController(LocalContext.current))
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchableExposedDropdownMenu() {
    val itemList = listOf("Apple", "Banana", "Cherry", "Date", "Grape", "Lemon", "Mango", "Orange", "Strawberry", "Watermelon")
    // State to hold the selected item in the preview
    var selectedItem by remember { mutableStateOf(itemList[0]) }

    Column(modifier = Modifier.padding(16.dp)) {
        SearchableExposedDropdownMenu(
            items = itemList,
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it },
            label = "Select a Fruit"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Currently Selected: $selectedItem")
    }
}