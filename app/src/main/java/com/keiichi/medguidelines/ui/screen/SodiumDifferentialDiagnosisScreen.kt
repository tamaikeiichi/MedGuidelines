package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun SodiumDifferentialDiagnosisScreen(navController: NavController){
    val listState = rememberLazyListState()
    val na = remember { mutableDoubleStateOf(120.0) }
    val serumOsmolality = remember { mutableDoubleStateOf(290.0) }

    MedGuidelinesScaffold (
        topBar = {
            TitleTopAppBar(
                title = R.string.disorderOfSerumSodium,
                navController = navController,
                references = listOf(
                    TextAndUrl(
                        R.string.disorderOfSerumSodiumReference,
                        R.string.disorderOfSerumSodiumReferenceUrl
                    )
                )
            )
        },
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(4.dp),
            state = listState
        ) {
            item {
                MedGuidelinesCard {
                    InputValue(
                        label = R.string.sodium,
                        value = na,
                        unit = R.string.mmoll,
                    )
                }
                when (na.doubleValue) {
                    in 0.0..< 135.0 -> {
                        MedGuidelinesCard {
                            Text(
                                text = parseStyledString(R.string.hyponatremia),
                                modifier = Modifier
                                    .padding(Dimensions.textPadding)
                            )
                        }
                        MedGuidelinesCard {
                            InputValue(
                                label = R.string.serumOsmolality,
                                value = serumOsmolality,
                                unit = R.string.mOsmKg,
                            )
                        }
                        when (serumOsmolality.doubleValue) {
                            in 0.0 ..< 280.0 -> {
                                MedGuidelinesCard {
                                    Text(
                                        text = parseStyledString(R.string.lowOsmolality),
                                        modifier = Modifier
                                            .padding(Dimensions.textPadding)
                                    )
                                }
                            }
                            in 280.0 ..< 295.0 -> {
                                MedGuidelinesCard {
                                    Text(
                                        text = parseStyledString(R.string.IsotonicHyponatraemia),
                                        modifier = Modifier
                                            .padding(Dimensions.textPadding)
                                    )
                                }
                                MedGuidelinesCard {
                                    Text(
                                        text = parseStyledString(R.string.HyperglycemiaMannitolAndGlycinByIrrigationFluids),
                                        modifier = Modifier
                                            .padding(Dimensions.textPadding)
                                    )
                                }
                            }
                            else -> {
                                MedGuidelinesCard {
                                    Text(
                                        text = parseStyledString(R.string.highOsmolality),
                                        modifier = Modifier
                                            .padding(Dimensions.textPadding)
                                    )
                                }
                            }
                        }
                    }
                    in 135.0 .. 145.0 -> {
                        MedGuidelinesCard {
                            Text(
                                text = parseStyledString(R.string.normonatremia),
                            )
                        }
                    }
                    else -> {
                        MedGuidelinesCard {
                            Text(
                                text = parseStyledString(R.string.hypernatremia),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SodiumDifferentialDiagnosisScreenPreview() {
    SodiumDifferentialDiagnosisScreen(
        navController = NavController(LocalContext.current)
    )
}