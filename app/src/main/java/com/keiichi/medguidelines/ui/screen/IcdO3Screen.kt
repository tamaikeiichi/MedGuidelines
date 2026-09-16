package com.keiichi.medguidelines.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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
import com.keiichi.medguidelines.data.IcdO3TopographyAlternateTerm
import com.keiichi.medguidelines.data.IcdO3TopographySearchResult
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.viewModel.IcdO3ViewModel
import java.util.Locale

private data class IcdO3GradeOption(val digit: String, val labelEn: String, val labelJa: String)

// どちらの検索窓がタップされているか（タップされた方の表示領域を最大化する）
private enum class IcdO3FocusedSection { TOPOGRAPHY, MORPHOLOGY }

// ICD-O-3の6桁目（グレード／分化度／細胞系列コード）の選択肢
// 「9」はグレード表と細胞系列表の両方に存在するため、"not applicable"の表記で1つに揃えている
private val icdO3GradeOptions = listOf(
    IcdO3GradeOption("1", "Grade I / Well differentiated / Differentiated, NOS", "異型度Ⅰ　高分化／分化，NOS"),
    IcdO3GradeOption(
        "2",
        "Grade II / Moderately differentiated / Moderately well differentiated / Intermediate differentiation",
        "異型度Ⅱ　中分化／中等度分化／中程度分化"
    ),
    IcdO3GradeOption("3", "Grade III / Poorly differentiated", "異型度Ⅲ　低分化"),
    IcdO3GradeOption("4", "Grade IV / Undifferentiated / Anaplastic", "異型度Ⅳ　未分化／退形成"),
    IcdO3GradeOption("5", "T-cell", "T細胞"),
    IcdO3GradeOption("6", "B-cell / Pre-B / B-precursor", "B細胞／前B／B前駆細胞"),
    IcdO3GradeOption("7", "Null cell / Non T-non B", "ヌル細胞／非T・非B"),
    IcdO3GradeOption("8", "NK cell / Natural killer cell", "NK（natural-killer）細胞"),
    IcdO3GradeOption("9", "Not determined, not stated or not applicable", "未決定，未記載又は適用外"),
)

// コードが4桁histology＋"/"＋1桁behaviorの形式（例："8000/3"）の場合のみ、6桁目に選択した数字を追加する
private fun applyGradeDigit(code: String, digit: String): String {
    if (digit.isBlank()) return code
    return if (code.length == 6 && code.getOrNull(4) == '/') code + digit else code
}

@Composable
fun IcdO3Screen(
    navController: NavHostController,
    viewModel: IcdO3ViewModel = viewModel(factory = IcdO3ViewModel.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedGradeDigit by viewModel.selectedGradeDigit.collectAsState()

    val topographySearchQuery by viewModel.topographySearchQuery.collectAsState()
    val topographySearchResults by viewModel.topographySearchResults.collectAsState()
    val isTopographyLoading by viewModel.isTopographyLoading.collectAsState()

    val configuration = LocalConfiguration.current
    val currentDeviceLocale = configuration.locales[0] ?: Locale.getDefault()
    val showJapanese = currentDeviceLocale.language == Locale.JAPANESE.language

    // タップされた検索窓の表示領域を最大化するための状態
    var focusedSection by remember { mutableStateOf<IcdO3FocusedSection?>(null) }

    val topographyCollapsed = focusedSection == IcdO3FocusedSection.MORPHOLOGY
    val morphologyCollapsed = focusedSection == IcdO3FocusedSection.TOPOGRAPHY

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.icdO3Title,
                navController = navController,
                references = listOf(TextAndUrl(R.string.icdo3, R.string.icdo3url))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 局在（Topography）- タップされていれば最大化、そうでなければ1/2
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (topographyCollapsed) Modifier.wrapContentHeight() else Modifier.weight(1f))
                    .animateContentSize(animationSpec = tween(durationMillis = 300))
            ) {
                MyCustomSearchBar(
                    searchQuery = topographySearchQuery,
                    onSearchQueryChange = { viewModel.onTopographyQueryChanged(it) },
                    onSearch = {},
                    isLoading = isTopographyLoading,
                    placeholderText = R.string.searchIcdO3Topography,
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            focusedSection = IcdO3FocusedSection.TOPOGRAPHY
                        }
                    }
                )
                if (!topographyCollapsed) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(topographySearchResults, key = { it.id }) { item ->
                            IcdO3TopographyResultCard(
                                item = item,
                                showJapanese = showJapanese,
                                onFavoriteClick = { viewModel.toggleTopographyFavorite(item) }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // 組織型（Morphology）- タップされていれば最大化、そうでなければ1/2
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (morphologyCollapsed) Modifier.wrapContentHeight() else Modifier.weight(1f))
                    .animateContentSize(animationSpec = tween(durationMillis = 300))
            ) {
                MyCustomSearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onQueryChanged(it) },
                    onSearch = {},
                    isLoading = isLoading,
                    placeholderText = R.string.searchIcdO3,
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            focusedSection = IcdO3FocusedSection.MORPHOLOGY
                        }
                    }
                )

                if (!morphologyCollapsed) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(searchResults, key = { it.id }) { item ->
                            IcdO3ResultCard(
                                item = item,
                                showJapanese = showJapanese,
                                gradeDigit = selectedGradeDigit,
                                onFavoriteClick = { viewModel.toggleFavorite(item) }
                            )
                        }
                    }
                }

                // 6桁目（グレード）セレクタは折りたたみ時も常に表示する
                IcdO3GradeDigitSelector(
                    selectedDigit = selectedGradeDigit,
                    showJapanese = showJapanese,
                    onDigitSelected = { viewModel.onGradeDigitChanged(it) }
                )
            }
        }
    }
}

private val gradeDigitSelectorShape = RoundedCornerShape(28.dp)
private val gradeDigitMenuShape = RoundedCornerShape(20.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IcdO3GradeDigitSelector(
    selectedDigit: String,
    showJapanese: Boolean,
    onDigitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val noneLabel = stringResource(R.string.icdO3GradeDigitNone)
    val selectedOption = icdO3GradeOptions.find { it.digit == selectedDigit }
    val selectedLabel = when {
        selectedOption == null -> noneLabel
        showJapanese -> "${selectedOption.digit}  ${selectedOption.labelJa}"
        else -> "${selectedOption.digit}  ${selectedOption.labelEn}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.icdO3GradeDigitLabel),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                shape = gradeDigitSelectorShape,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = gradeDigitMenuShape
            ) {
                DropdownMenuItem(
                    text = { Text(noneLabel) },
                    onClick = {
                        onDigitSelected("")
                        expanded = false
                    }
                )
                icdO3GradeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(text = "${option.digit}  ${option.labelEn}", fontSize = 14.sp)
                                if (showJapanese) {
                                    Text(
                                        text = option.labelJa,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        onClick = {
                            onDigitSelected(option.digit)
                            expanded = false
                        }
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
    gradeDigit: String,
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
                SelectionContainer(modifier = Modifier.weight(1f)) {
                    Column {
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
                SelectionContainer {
                    Text(text = applyGradeDigit(item.code, gradeDigit), fontSize = 16.sp)
                }
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
    SelectionContainer {
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
}

@Composable
private fun IcdO3TopographyResultCard(
    item: IcdO3TopographySearchResult,
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
                SelectionContainer(modifier = Modifier.weight(1f)) {
                    Column {
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
                SelectionContainer {
                    Text(text = item.code, fontSize = 16.sp)
                }
                if (item.needsReview) {
                    Text(
                        text = stringResource(R.string.icdO3NeedsReview),
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
                    TopographyAlternateTermRow(alt = alt, showJapanese = showJapanese)
                }
            }
        }
    }
}

@Composable
private fun TopographyAlternateTermRow(alt: IcdO3TopographyAlternateTerm, showJapanese: Boolean) {
    SelectionContainer {
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
}

@Preview(showBackground = true)
@Composable
fun PreviewIcdO3Screen() {
    val navController = rememberNavController()
    MaterialTheme {
        IcdO3Screen(navController = navController)
    }
}
