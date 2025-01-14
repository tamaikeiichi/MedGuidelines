package com.example.medguidelines.ui.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.TitleTopAppBar
import com.example.medguidelines.ui.component.parseStyledString

@Composable
fun BloodGasAnalysisScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = stringResource(id = R.string.bloodGasAnalysisTitle),
                navController = navController,
                referenceText = R.string.bloodGasAnalysisRef,
                referenceUrl = R.string.bloodGasAnalysisUrl
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                BloodGasAnalysisInput()
            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodGasAnalysisInput() {
    val ph = remember { mutableDoubleStateOf(7.40) }
    val paO2 = remember { mutableDoubleStateOf(100.0) }
    val paCo2 = remember { mutableDoubleStateOf(40.0) }
    val hco3 = remember { mutableDoubleStateOf(24.0) }
    val na = remember { mutableDoubleStateOf(145.0) }
    val k = remember { mutableDoubleStateOf(4.5) }
    val cl = remember { mutableDoubleStateOf(98.0) }
    val albumin = remember { mutableDoubleStateOf(3.9) }

    val normalHco3: Double = 23.0
    val normalPaCo2: Double = 40.0
    val normalPaO2: Double = 100.0

    BloodGasAnalysisCard(
        bloodGasAnalysisContent = {
            FlowRow(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                BloodGasAnalysisTextField(
                    label = R.string.ph, value = ph, width = 100,

                    )
                BloodGasAnalysisTextField(
                    label = R.string.po2, value = paO2, width = 110,

                    )
                BloodGasAnalysisTextField(
                    label = R.string.pco2, value = paCo2, width = 120,

                    )
                BloodGasAnalysisTextField(
                    label = R.string.hco3, value = hco3, width = 120,

                    )
                BloodGasAnalysisTextField(
                    label = R.string.na, value = na, width = 110,


                    )
                BloodGasAnalysisTextField(
                    label = R.string.k, value = k, width = 100,


                    )
                BloodGasAnalysisTextField(
                    label = R.string.cl, value = cl, width = 110,


                    )
                BloodGasAnalysisTextField(
                    label = R.string.albumin, value = albumin, width = 120,

                    )

            }

        }
    )
    if (ph.doubleValue < 7.38) {
        BloodGasAnalysisCard(
            bloodGasAnalysisContent = {
                BloodGasAnalysisText(R.string.acidemia)
            }
        )
        if (hco3.doubleValue < 22 && paCo2.doubleValue < 42) {
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisText(R.string.metabolicAcidosis)
                }
            )
            val calculateExpectedPaco2 = 1.5 * hco3.doubleValue + 8
            if (paO2.doubleValue > calculateExpectedPaco2) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalRespiratoryAcidosis)
                    }
                )
            } else if (paO2.doubleValue < calculateExpectedPaco2) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalRespiratoryAlkalosis)
                    }
                )
            }

            val anionGap = na.doubleValue - cl.doubleValue - hco3.doubleValue
            var correctedAnionGap = anionGap
            if (albumin.doubleValue < 3.9) {
                correctedAnionGap = anionGap + 2.5 * (3.9 - albumin.doubleValue)
            }
            if (correctedAnionGap in 8.0..12.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.normalAnionGap)
                    }
                )
                val urineNa = remember { mutableDoubleStateOf(20.0) }
                val urineK = remember { mutableDoubleStateOf(4.0) }
                val urineCl = remember { mutableDoubleStateOf(90.0) }
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        FlowRow(
                            modifier = Modifier
                                .padding(4.dp)
                        ) {
                            BloodGasAnalysisTextField(
                                label = R.string.urineNa, value = urineNa, width = 120,


                                )
                            BloodGasAnalysisTextField(
                                label = R.string.urineK, value = urineK, width = 120,


                                )
                            BloodGasAnalysisTextField(
                                label = R.string.urineCl, value = urineCl, width = 120,


                                )
                        }
                    }
                )
                val urineAnionGap = urineNa.doubleValue - urineCl.doubleValue + urineK.doubleValue
                if (urineAnionGap >= 0) {
                    BloodGasAnalysisCard(
                        bloodGasAnalysisContent = {
                            BloodGasAnalysisText(R.string.rta)
                        }
                    )
                } else {
                    BloodGasAnalysisCard(
                        bloodGasAnalysisContent = {
                            BloodGasAnalysisText(R.string.urinaryAnionGapNegativeDDx)
                        }
                    )
                }
            } else if (correctedAnionGap >= 12) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        Column() {
                            BloodGasAnalysisText(R.string.highAnionGap)
                            BloodGasAnalysisText(R.string.highAnionGapDDx)
                        }
                    }
                )
            }
        } else if (paCo2.doubleValue > 42 && hco3.doubleValue > 22) {
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisText(R.string.respiratoryAcidosis)
                }
            )
            val hco3change = (hco3.doubleValue - 23) / ((paCo2.doubleValue - 40)/10)
            if (0.0 < hco3change && hco3change < 1.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalMetabolicAcidosis)
                    }
                )
            } else if (1.0 <= hco3change && hco3change < 4.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.acuteRespiratoryAcidosis)
                    }
                )
            } else if (hco3change in 4.0..5.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.chronicRespiratoryAcidosis)
                    }
                )
            } else if (hco3change > 5.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalMetabolicAlkalosis)
                    }
                )
            }
            val age = remember { mutableDoubleStateOf(65.0) }
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisTextField(
                        label = R.string.age, value = age, width = 120,
                        )
                }
            )
            val aaDifference = 150 - paO2.doubleValue - 1.25 * paCo2.doubleValue
            var correctedAaDifference = aaDifference
            if (age.doubleValue >= 65) {
                correctedAaDifference = aaDifference / 2
            }
            if (correctedAaDifference <= 10) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.HypoventilationWithoutIntrinsicLungDisease)
                    }
                )
            } else if (correctedAaDifference > 10) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.HypoventilationWithIntrinsicLungDiseaseVentilationPerfusionMismatchOrBoth)
                    }
                )
            }
        }
    } else if (ph.doubleValue > 7.42) {
        BloodGasAnalysisCard (
            bloodGasAnalysisContent = {
                BloodGasAnalysisText(R.string.alkalemia)
            }
        )
        if (hco3.doubleValue > 26 && paCo2.doubleValue >= 38) {
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisText(R.string.metabolicAlkalosis)
                }
            )
            val calculateExpectedPaco2 = 0.7 * (hco3.doubleValue - 24) + 40
            if (paCo2.doubleValue < calculateExpectedPaco2) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalRespiratoryAlkalosis)
                    }
                )
            } else if (paCo2.doubleValue > calculateExpectedPaco2) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalRespiratoryAcidosis)
                    }
                )
            }
        } else if (hco3.doubleValue < 26 && paCo2.doubleValue < 38) {
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisText(R.string.respiratoryAlkalosis)
                }
            )
            val hco3Change = (hco3.doubleValue - normalHco3) / (paCo2.doubleValue - normalPaCo2)/10
            if (0.0 < hco3Change && hco3Change < 2.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalMetabolicAlkalosis)
                    }
                )
            } else if (2.0 <= hco3Change && hco3Change < 4.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.acuteRespiratoryAlkalosis)
                    }
                )
            } else if (hco3Change in 4.0..5.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.chronicRespiratoryAlkalosis)
                    }
                )
            } else if (hco3Change > 5.0) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.additionalMetabolicAcidosis)
                    }
                )
            }
            val age = remember { mutableDoubleStateOf(65.0) }
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    FlowRow (
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        BloodGasAnalysisTextField(
                            label = R.string.age, value = age, width = 120,
                        )
                    }

                }
            )
            val aaDifference = 150 - paO2.doubleValue - 1.25 * paCo2.doubleValue
            var correctedAaDifference = aaDifference
            if (age.doubleValue >= 65) {
                correctedAaDifference = aaDifference / 2
            }
            if (correctedAaDifference <= 10) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.HypoventilationWithoutIntrinsicLungDisease)
                    }
                )
            } else if (correctedAaDifference > 10) {
                BloodGasAnalysisCard(
                    bloodGasAnalysisContent = {
                        BloodGasAnalysisText(R.string.HypoventilationWithIntrinsicLungDiseaseVentilationPerfusionMismatchOrBoth)
                    }
                )
            }
        }
    } else {
        if (hco3.doubleValue > (normalHco3 + 3) && paCo2.doubleValue > (normalPaCo2 + 5)) {
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisText(R.string.metabolicAlkalosisAndRespiratoryAcidosis)
                }
            )
        } else if (hco3.doubleValue < (normalHco3 - 3) && paCo2.doubleValue < (normalPaCo2 - 5)) {
            BloodGasAnalysisCard(
                bloodGasAnalysisContent = {
                    BloodGasAnalysisText(R.string.metabolicAcidosisAndRespiratoryAlkalosis)
                }
            )
        }
    }
}

@Composable
fun BloodGasAnalysisText(text: Int) {
    Text(
        text = parseStyledString(text),
        modifier = Modifier
            .padding(10.dp)
    )
}

@Composable
fun BloodGasAnalysisCard(bloodGasAnalysisContent: @Composable ()-> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        bloodGasAnalysisContent()

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGasAnalysisTextField(label: Int, value: MutableDoubleState, width: Int,
    //keyboardOptions: KeyboardOptions
) {
    var text by remember { mutableStateOf(value.doubleValue.toString()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) {
            text = ""
        }
    }
    val fontSize = calculateFontSize(text)

    TextField(
        label = { Text(parseStyledString(label)) },
        value = text,
        onValueChange = {newText ->
            if (newText.matches(Regex("[0-9]*\\.?[0-9]*")) || newText.isEmpty()) {
                text = newText
                value.doubleValue = newText.toDoubleOrNull() ?: 0.0
            }},
        modifier = Modifier
            .padding(5.dp)
            .width(width.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        textStyle = TextStyle.Default.copy(
            fontSize = fontSize,
            textAlign = TextAlign
                .Right,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Bottom,
                trim = LineHeightStyle.Trim.Both
            )
        ),
        maxLines = 1,
        interactionSource = interactionSource
        //TextFieldColors =
    )
}

fun calculateFontSize(text: String): TextUnit {
    val baseSize = 28.sp
    val minSize = 12.sp
    val maxLength = 5

    return when {
        text.length <= maxLength / 2 -> baseSize
        text.length <= maxLength -> (baseSize.value - (text.length - maxLength / 2) * 2).sp
        else -> minSize
    }
}

@Preview
@Composable
fun BloodGasAnalysisScreenPreview() {
    BloodGasAnalysisScreen(navController = NavController(LocalContext.current))
}
