package com.keiichi.medguidelines.ui.screen

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.IcdO3AlternateTerm
import com.keiichi.medguidelines.data.IcdO3SearchResult
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.viewModel.IcdO3ViewModel
import java.util.Locale

@Composable
fun IcdO3Screen(
    navController: NavHostController,
    viewModel: IcdO3ViewModel = viewModel(factory = IcdO3ViewModel.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val configuration = LocalConfiguration.current
    val currentDeviceLocale = configuration.locales[0] ?: Locale.getDefault()
    val showJapanese = currentDeviceLocale.language == Locale.JAPANESE.language

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.icdO3Title,
                navController = navController,
                references = listOf(TextAndUrl(R.string.icdo3, R.string.icdo3url))
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            MyCustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.onQueryChanged(it) },
                onSearch = {},
                isLoading = isLoading,
                placeholderText = R.string.searchIcdO3
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(searchResults, key = { it.id }) { item ->
                    IcdO3ResultCard(
                        item = item,
                        showJapanese = showJapanese,
                        onFavoriteClick = { viewModel.toggleFavorite(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IcdO3ResultCard(
    item: IcdO3SearchResult,
    showJapanese: Boolean,
    onFavoriteClick: () -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.termEn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (showJapanese && item.termJa.isNotBlank()) {
                        Text(
                            text = item.termJa,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onFavoriteClick) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.code, fontSize = 16.sp)
                if (item.isObsolete) {
                    Text(
                        text = stringResource(R.string.icdO3Obsolete),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (item.alternateTerms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.icdO3AlsoKnownAs),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                item.alternateTerms.forEach { alt ->
                    AlternateTermRow(alt = alt, showJapanese = showJapanese)
                }
            }
        }
    }
}

@Composable
private fun AlternateTermRow(alt: IcdO3AlternateTerm, showJapanese: Boolean) {
    Column(modifier = Modifier.padding(top = 2.dp)) {
        Text(
            text = alt.termEn,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (showJapanese && alt.termJa.isNotBlank()) {
            Text(
                text = alt.termJa,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIcdO3Screen() {
    val navController = rememberNavController()
    MaterialTheme {
        IcdO3Screen(navController = navController)
    }
}
