package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.noYes
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.buttonAndScore
import com.keiichi.medguidelines.ui.component.parseStyledString

@Composable
fun SodiumDifferentialDiagnosisScreen(navController: NavController) {
    val listState = rememberLazyListState()
    val na = remember { mutableDoubleStateOf(145.0) }
    val k = remember { mutableDoubleStateOf(4.5) }
    val serumGlucose = remember { mutableDoubleStateOf(90.0) }
    val serumOsmolality = remember { mutableDoubleStateOf(290.0) }
    val urineOsmolality = remember { mutableDoubleStateOf(100.0) }
    val urineSodiumConc = remember { mutableDoubleStateOf(30.0) }
    var acuteOrSevere = remember { mutableIntStateOf(0) }
    var diureticsOrKidneyDisease = remember { mutableIntStateOf(0) }

    MedGuidelinesScaffold(
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(4.dp),
            state = listState
        ) {
            item {
                MedGuidelinesCard {
                    FlowRow {
                        InputValue(
                            label = R.string.sodium,
                            value = na,
                            unit = R.string.mmoll,
                        )
//                        InputValue(
//                            label = R.string.k,
//                            value = k,
//                            unit = R.string.mmoll,
//                        )
                        InputValue(
                            label = R.string.serumGlucose,
                            value = serumGlucose,
                            unit = R.string.mgdl,
                            changedValueRate = 1 / 18.0,
                            changedUnit = R.string.mmoll
                        )
                    }

                }
                when (na.doubleValue) {
                    in 0.0..<135.0 -> {
                        MedGuidelinesCard {
                            MedGuidelinesText(
                                text = parseStyledString(R.string.hyponatremia),
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
                            in 0.0..<280.0 -> {
                                MedGuidelinesCard {
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.hypotonicHyponatremia),
                                    )
                                }
                                MedGuidelinesCard {
                                    acuteOrSevere.intValue = buttonAndScore(
                                        factor = noYes,
                                        title = R.string.acuteOrSevereSymptoms,
                                    )
                                }
                                if (acuteOrSevere.intValue == 1) {
                                    MedGuidelinesCard {
                                        MedGuidelinesText(
                                            text = parseStyledString(R.string.considerImmediateTreatmentWithHypertonicSaline),
                                        )
                                    }
                                } else {
                                    MedGuidelinesCard {
                                        InputValue(
                                            label = R.string.urineOsmolality,
                                            value = urineOsmolality,
                                            unit = R.string.mOsmKg,
                                        )
                                    }
                                    if (urineOsmolality.doubleValue <= 100.0) {
                                        MedGuidelinesCard {
                                            Column {
                                                MedGuidelinesText(
                                                    text = parseStyledString(R.string.primaryPolydipsia),
                                                )
                                                MedGuidelinesText(
                                                    text = parseStyledString(R.string.lowSoluteIntake),
                                                )
                                                MedGuidelinesText(
                                                    text = parseStyledString(R.string.beerPotomania),
                                                )
                                            }
                                        }
                                    } else {
                                        MedGuidelinesCard {
                                            InputValue(
                                                label = R.string.urineSodiumConcentration,
                                                value = urineSodiumConc,
                                                unit = R.string.mOsmKg,
                                            )
                                        }
                                            if (urineSodiumConc.doubleValue <= 30.0) {
                                                MedGuidelinesCard {
                                                        MedGuidelinesText(
                                                            text = parseStyledString(R.string.lowEffectiveArterialBloodVolume),
                                                        )
                                                }
                                                MedGuidelinesCard {
                                                    MedGuidelinesText(
                                                        text = buildAnnotatedString {
                                                            withStyle(
                                                                style = SpanStyle(
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            ) {
                                                                append(parseStyledString(R.string.ifEcfExpadedConsider))
                                                            }
                                                            append("\n")
                                                            append(parseStyledString(R.string.hearFailure))
                                                            append("\n")
                                                            append(parseStyledString(R.string.liverCirrhosis))
                                                            append("\n")
                                                            append(parseStyledString(R.string.nephroticSyndrome))
                                                            append("\n")
                                                            withStyle(
                                                                style = SpanStyle(
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            ) {
                                                                append(parseStyledString(R.string.ifEcfReducedConsider))
                                                            }
                                                            append("\n")
                                                            append(parseStyledString(R.string.diarrheaAndVomiting))
                                                            append("\n")
                                                            append(parseStyledString(R.string.thirdSpacing))
                                                            append("\n")
                                                            append(parseStyledString(R.string.remoteDiuretics))
                                                        }
                                                    )
                                                }
                                            } else {
                                                diureticsOrKidneyDisease.intValue = buttonAndScore(
                                                    factor = noYes,
                                                    title = R.string.diureticsOrKidneyDisease,
                                                )
                                                if (diureticsOrKidneyDisease.intValue == 1) {
                                                    MedGuidelinesCard {
                                                        MedGuidelinesText(
                                                            text = buildAnnotatedString {
                                                                withStyle(
                                                                    style = SpanStyle(
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                ) {
                                                                    append(parseStyledString(R.string.consider))
                                                                }
                                                                append("\n")
                                                                append(parseStyledString(R.string.diuretics))
                                                                append("\n")
                                                                append(parseStyledString(R.string.kidneyDisease))
                                                                append("\n")
                                                                append(parseStyledString(R.string.allOtherCauses))
                                                            }
                                                        )
                                                    }
                                                } else {
                                                    MedGuidelinesCard {
                                                        Column {
                                                            MedGuidelinesText(
                                                                text = buildAnnotatedString {
                                                                    withStyle(
                                                                        style = SpanStyle(
                                                                            fontWeight = FontWeight.Bold
                                                                        )
                                                                    ) {
                                                                        append(parseStyledString(R.string.ifEcfReducedConsider))
                                                                    }
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.vomiting))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.primaryAdrenalInsufficiency))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.renalSaltWasting))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.cerebralSaltWasting))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.occultDiuretics))
                                                                    append("\n")
                                                                    withStyle(
                                                                        style = SpanStyle(
                                                                            fontWeight = FontWeight.Bold
                                                                        )
                                                                    ) {
                                                                        append(parseStyledString(R.string.ifEcfNormalConsider))
                                                                    }
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.siad))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.secondaryAdrenalInsufficiency))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.hypothyroidism))
                                                                    append("\n")
                                                                    append(parseStyledString(R.string.occultDiuretics))
                                                                }
                                                            )
                                                        }
                                                    }
                                                }

                                            }

                                    }
                                }
                            }

                            in 280.0..<295.0 -> {
                                MedGuidelinesCard {
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.IsotonicHyponatraemia),
                                    )
                                }
                                MedGuidelinesCard {
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.HyperglycemiaMannitolAndGlycinByIrrigationFluids),
                                    )
                                }
                            }

                            else -> {
                                MedGuidelinesCard {
                                    Column {
                                        MedGuidelinesText(
                                            text = parseStyledString(R.string.hypertonicHyponatremia),
                                        )
                                        MedGuidelinesText(
                                            text = parseStyledString(R.string.HyperglycemiaMayBePresent),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    in 135.0..145.0 -> {
                        MedGuidelinesCard {
                            MedGuidelinesText(
                                text = parseStyledString(R.string.normonatremia),
                            )
                        }
                    }

                    else -> {
                        MedGuidelinesCard {
                            MedGuidelinesText(
                                text = parseStyledString(R.string.hypernatremia),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedGuidelinesText(text: AnnotatedString) {
    Text(
        text = text,
        modifier = Modifier
            .padding(Dimensions.textPadding)
    )
}

@Preview
@Composable
fun SodiumDifferentialDiagnosisScreenPreview() {
    SodiumDifferentialDiagnosisScreen(
        navController = NavController(LocalContext.current)
    )
}