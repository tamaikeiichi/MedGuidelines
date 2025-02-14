package com.example.medguidelines.ui.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.medguidelines.data.ActionType
import com.example.medguidelines.ui.component.IndexScreenListItem
import com.example.medguidelines.data.ListItemData
import com.example.medguidelines.data.loadListItemData
import com.example.medguidelines.data.saveListItemData
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.text.contains
import kotlin.text.isBlank

val itemsList = listOf(
    ListItemData(R.string.childPughTitle, ActionType.NAVIGATE_TO_CHILD_PUGH),
    ListItemData(R.string.aDropTitle, ActionType.NAVIGATE_TO_ADROP),
    ListItemData(R.string.colorectalTNMTitle, ActionType.NAVIGATE_TO_COLORECTAL_TNM),
    ListItemData(R.string.acuteTonsillitisAlgorithmTitle, ActionType.NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM),
    ListItemData(R.string.bloodGasAnalysisTitle, ActionType.NAVIGATE_TO_BLOOD_GAS_ANALYSIS),
    ListItemData(R.string.acutePancreatitisTitle, ActionType.NAVIGATE_TO_ACUTE_PANCREATITIS),
    ListItemData(R.string.netakiridoTitle, ActionType.NAVIGATE_TO_NETAKIRIDO),
    ListItemData(R.string.pancreaticTNMTitle, ActionType.NAVIGATE_TO_PANCREATITIS_TNM),
    ListItemData(R.string.esophagealTNMTitle, ActionType.NAVIGATE_TO_ESOPAGEAL_TNM),
    ListItemData(R.string.mALBITitle, ActionType.NAVIGATE_TO_MALBI),
)

@Composable
fun IndexScreen(
    navigateToChildPugh: () -> Unit,
    navigateToAdrop: () -> Unit,
    navigateToColorectalTNM: () -> Unit,
    navigateToAcuteTonsillitisAlgorithm: () -> Unit,
    navigateToBloodGasAnalysis: () -> Unit,
    navigateToAcutePancreatitis: () -> Unit,
    navigateToNetakirido: () -> Unit,
    navigateToPancreaticTNM: () -> Unit,
    navigateToEsophagealTNM: () -> Unit,
    navigateToMALBI: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    val originalItems = rememberSaveable(saver = listSaver(
        save = { it.map { item -> Json.encodeToString(item) } },
        restore = { it.map { item -> Json.decodeFromString<ListItemData>(item) }.toMutableStateList() }
    )) {
        mutableStateListOf<ListItemData>()
    }
    val filteredItems = remember(searchQuery, originalItems) {
        if (searchQuery.isBlank()) {
            originalItems
        } else {
            originalItems.toList().filter {
                itemData ->
                val name = context.getString(itemData.nameResId)
                name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        loadListItemData(context).collect { loadedItems ->
            originalItems.clear()
            originalItems.addAll(loadedItems)
        }
    }

    Column{
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { newQuery ->
                searchQuery = newQuery
            }
        )
        LazyColumn(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
        ) {
            items(filteredItems//items
                    ){ itemData ->
                IndexScreenListItem(
                    name = itemData.nameResId,
                    onClick = {
                        val updatedItems = originalItems.toMutableList()//items.toMutableList()
                        updatedItems.remove(itemData)
                        updatedItems.add(0, itemData)
                        scope.launch {
                            saveListItemData(context, updatedItems)
                        }
                        when (itemData.actionType) {
                            ActionType.NAVIGATE_TO_CHILD_PUGH -> navigateToChildPugh()
                            ActionType.NAVIGATE_TO_ADROP -> navigateToAdrop()
                            ActionType.NAVIGATE_TO_COLORECTAL_TNM -> navigateToColorectalTNM()
                            ActionType.NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM -> navigateToAcuteTonsillitisAlgorithm()
                            ActionType.NAVIGATE_TO_BLOOD_GAS_ANALYSIS -> navigateToBloodGasAnalysis()
                            ActionType.NAVIGATE_TO_ACUTE_PANCREATITIS -> navigateToAcutePancreatitis()
                            ActionType.NAVIGATE_TO_NETAKIRIDO -> navigateToNetakirido()
                            ActionType.NAVIGATE_TO_PANCREATITIS_TNM -> navigateToPancreaticTNM()
                            ActionType.NAVIGATE_TO_ESOPAGEAL_TNM -> navigateToEsophagealTNM()
                            ActionType.NAVIGATE_TO_MALBI -> navigateToMALBI()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
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
    IndexScreen(
        navigateToChildPugh = {},
        navigateToAdrop = {},
        navigateToColorectalTNM = {},
        navigateToAcuteTonsillitisAlgorithm = {},
        navigateToBloodGasAnalysis = {},
        navigateToAcutePancreatitis = {},
        navigateToNetakirido = {},
        navigateToPancreaticTNM = {},
        navigateToEsophagealTNM = {},
        navigateToMALBI = {},
    )
}
