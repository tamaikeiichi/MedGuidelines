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
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
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

// 低ナトリウム血症の鑑別は Spasovski G, et al. Eur J Endocrinol. 2014;170(3):G1-G47 の
// 推奨 6.2.1，Table 10，Figure 6 に準拠
// 測定した血漿浸透圧が <275 mOsm/kg なら常に低張性（6.2.1.2）
private const val HYPOTONIC_SERUM_OSMOLALITY = 275.0

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
    val nonHypotonicCause = remember { mutableIntStateOf(0) }

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
                            japaneseUnit = R.string.mmoll,
                        )
//                        InputValue(
//                            label = R.string.k,
//                            value = k,
//                            unit = R.string.mmoll,
//                        )
//                        InputValue(
//                            label = R.string.serumGlucose,
//                            value = serumGlucose,
//                            unit = R.string.mgdl,
//                            changedValueRate = 1 / 18.0,
//                            changedUnit = R.string.mmoll
//                        )
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
                                japaneseUnit = R.string.mOsmKg,
                            )
                        }
                        if (serumOsmolality.doubleValue < HYPOTONIC_SERUM_OSMOLALITY) {
                            MedGuidelinesCard {
                                MedGuidelinesText(
                                    text = parseStyledString(R.string.hypotonicHyponatremia),
                                )
                            }
                            HypotonicHyponatremiaWorkup(
                                acuteOrSevere = acuteOrSevere,
                                urineOsmolality = urineOsmolality,
                                urineSodiumConc = urineSodiumConc,
                                diureticsOrKidneyDisease = diureticsOrKidneyDisease,
                            )
                        } else {
                            // ≥275 mOsm/kg では Table 10 の非低張性の原因を除外する
                            MedGuidelinesCard {
                                Column {
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.excludeNonHypotonicHyponatremia),
                                    )
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.nonHypotonicEffectiveOsmoles),
                                    )
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.pseudohyponatremia),
                                    )
                                }
                            }
                            MedGuidelinesCard {
                                nonHypotonicCause.intValue = buttonAndScore(
                                    factor = noYes,
                                    title = R.string.nonHypotonicCausePresent,
                                )
                            }
                            if (nonHypotonicCause.intValue == 1) {
                                MedGuidelinesCard {
                                    MedGuidelinesText(
                                        text = parseStyledString(R.string.nonHypotonicHyponatremia),
                                    )
                                }
                            } else {
                                // 非低張性の原因がなければ低張性として扱う（6.2.1.3）
                                MedGuidelinesCard {
                                    Column {
                                        MedGuidelinesText(
                                            text = parseStyledString(R.string.acceptAsHypotonicHyponatremia),
                                        )
                                        MedGuidelinesText(
                                            text = parseStyledString(R.string.hypotonicHyponatremia),
                                        )
                                    }
                                }
                                HypotonicHyponatremiaWorkup(
                                    acuteOrSevere = acuteOrSevere,
                                    urineOsmolality = urineOsmolality,
                                    urineSodiumConc = urineSodiumConc,
                                    diureticsOrKidneyDisease = diureticsOrKidneyDisease,
                                )
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

/**
 * 低張性低ナトリウム血症の鑑別（Figure 6）
 */
@Composable
private fun HypotonicHyponatremiaWorkup(
    acuteOrSevere: MutableIntState,
    urineOsmolality: MutableDoubleState,
    urineSodiumConc: MutableDoubleState,
    diureticsOrKidneyDisease: MutableIntState,
) {
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
        return
    }
    MedGuidelinesCard {
        InputValue(
            label = R.string.urineOsmolality,
            value = urineOsmolality,
            japaneseUnit = R.string.mOsmKg,
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
        return
    }
    MedGuidelinesCard {
        InputValue(
            label = R.string.urineSodiumConcentration,
            value = urineSodiumConc,
            japaneseUnit = R.string.mmoll,
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
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(parseStyledString(R.string.ifEcfExpadedConsider))
                    }
                    append("\n")
                    append(parseStyledString(R.string.hearFailure))
                    append("\n")
                    append(parseStyledString(R.string.liverCirrhosis))
                    append("\n")
                    append(parseStyledString(R.string.nephroticSyndrome))
                    append("\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
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
        return
    }
    diureticsOrKidneyDisease.intValue = buttonAndScore(
        factor = noYes,
        title = R.string.diureticsOrKidneyDisease,
    )
    if (diureticsOrKidneyDisease.intValue == 1) {
        MedGuidelinesCard {
            MedGuidelinesText(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
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
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
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
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
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
