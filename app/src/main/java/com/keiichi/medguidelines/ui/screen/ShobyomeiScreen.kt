package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.viewModel.ShobyomeiViewModel

@Composable
fun ShobyomeiScreen(
    navController: NavHostController,
    viewModel: ShobyomeiViewModel = viewModel(factory = ShobyomeiViewModel.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(title = R.string.shobyomeiTitle, navController = navController,
                references = listOf(
                    TextAndUrl(R.string.shobyomeiRefTitle, R.string.ShobyomeiMasterUrl)
                ))
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MyCustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.onQueryChanged(it) },
                onSearch = {},
                isLoading = isLoading,
                placeholderText = R.string.searchShobyomei
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                // 検索結果またはお気に入りがある場合に凡例（ヘッダー）を表示
                if (searchResults.isNotEmpty()) {
                    item {
                        MedGuidelinesCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.cardPadding),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Column(modifier = Modifier.padding(Dimensions.cardPadding)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
//                                    Text(
//                                        text = if (searchQuery.isBlank()) "お気に入り" else "検索結果",
//                                        fontSize = 14.sp,
//                                        color = MaterialTheme.colorScheme.secondary
//                                    )
                                }
                                Text(text = "傷病名", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(text = "コード", fontSize = 18.sp, fontWeight = FontWeight.Normal)
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                    ) {
                                        Text(text = "ICD10 (基礎疾患)", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                                        Text(text = "ICD10 (症状発現)", fontSize = 16.sp, fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                }

                items(searchResults, key = { it.code }) { item ->
                    MedGuidelinesCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(Dimensions.cardPadding)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { viewModel.toggleFavorite(item) }) {
                                    Icon(
                                        imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Favorite",
                                        tint = if (item.isFavorite) Color(0xFFFFD700) else LocalContentColor.current
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(text = item.code, fontSize = 18.sp)
                                Column(horizontalAlignment = Alignment.End) {
                                    if (item.icd_10_1.isNotBlank()) {
                                        Text(text = item.icd_10_1, fontSize = 16.sp)
                                    } else {
                                    Text(text = "なし", fontSize = 16.sp)
                                }
                                    if (item.icd_10_2.isNotBlank()) {
                                        Text(text = item.icd_10_2, fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    } else {
                                        Text(text = "なし", fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShobyomeiScreen() {
    val navController = rememberNavController()
    MaterialTheme {
        ShobyomeiScreen(navController = navController)
    }
}
