package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.PairedTextItem
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.ShinryokoiMasterScreenItemCard
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.viewModel.ShikaShinryokoiMasterViewModel

@Composable
fun ShikaShinryokoiMasterScreen(
    navController: NavHostController,
    viewModel: ShikaShinryokoiMasterViewModel = viewModel(factory = ShikaShinryokoiMasterViewModel.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val displayedItems by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lazyListState = remember(calculation = { LazyListState() })

    // お気に入りに追加した項目は先頭へ移動するので、リスト更新後に先頭までスクロールする
    var scrollToTopOnNextUpdate by remember { mutableStateOf(false) }
    LaunchedEffect(displayedItems) {
        if (scrollToTopOnNextUpdate) {
            scrollToTopOnNextUpdate = false
            lazyListState.animateScrollToItem(index = 0)
        }
    }

    MedGuidelinesScaffold (
        topBar = {
            TitleTopAppBar(
                title = R.string.ShikaShinryokoiMaster,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.ShikaShinryokoiMaster, R.string.ShikaShinryokoiMasterUrl)
                )
            )
        },
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),

            ) {
            MyCustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    viewModel.onQueryChanged(newQuery)
                },
                onSearch = { query ->
                    println("Search submitted: $query")
                },
                isLoading = isLoading
            )
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(2.dp),
            ) {
                items(
                    items = displayedItems,
                    key = { pairedItem ->
                        pairedItem.originalIndex // Use the original row index as a stable key
                    }
                ) { pairedItem ->
                    ShinryokoiMasterScreenItemCard(
                        pairedItem = pairedItem,
                        isFavorite = pairedItem.isFavorite,
                        onFavoriteClick = { clickedPairedItem: PairedTextItem ->
                            if (!clickedPairedItem.isFavorite) {
                                scrollToTopOnNextUpdate = true
                            }
                            viewModel.toggleFavorite(clickedPairedItem)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ShikaShinryokoiMasterScreenPreview() {
    ShikaShinryokoiMasterScreen(navController = NavHostController(LocalContext.current))
}
