package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.CockcroftGaultResult
import com.keiichi.medguidelines.data.DataStoreKeys
import com.keiichi.medguidelines.data.WeightBasis
import com.keiichi.medguidelines.data.calculateCockcroftGault
import com.keiichi.medguidelines.data.cockcroftGaultEthnicity
import com.keiichi.medguidelines.data.cockcroftGaultSex
import com.keiichi.medguidelines.data.dataStore
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesFlowRow
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.PopupClickable
import com.keiichi.medguidelines.ui.component.parseStyledString
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayedSelectable
import java.text.DecimalFormat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun CockcroftGaultScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var result by remember {
        mutableStateOf(
            CockcroftGaultResult(
                actualBodyWeight = 0.0,
                crClActual = 0.0,
                gfr = 0.0,
                gfrCys = 0.0,
                bmi = null,
                idealBodyWeight = null,
                adjustedBodyWeight = null,
                crClIdeal = null,
                crClAdjusted = null,
                recommendedBasis = WeightBasis.ACTUAL,
                recommendedWeight = 0.0,
                recommendedCrCl = 0.0
            )
        )
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.cockcroftGaultTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.cockcroftGaultRef, R.string.cockcroftGaultUrl),
                    TextAndUrl(R.string.idealAndAdjustedBodyWeightRef, R.string.idealAndAdjustedBodyWeightUrl),
                    TextAndUrl(R.string.gfrRefTitle, R.string.gfrUrl),
                    //TextAndUrl(R.string.gfrCysTitle, R.string.gfrCysUrl)
                    TextAndUrl(R.string.ckdGuideline2023Ref, R.string.ckdGuideline2023Url)
                )
            )
        },
        bottomBar = {
            val mlMin = stringResource(R.string.mlMin)
            val gfrUnit = parseStyledString(R.string.mlmin173m2)
            ScoreBottomAppBarVariable(displayText = buildAnnotatedString {
                appendGfrSummaryRow(
                    unit = gfrUnit,
                    cre = result.gfr,
                    cys = result.gfrCys
                )
                append("\n")
                appendCCrSummaryRow(
                    mlMin = mlMin,
                    actual = result.crClActual,
                    actualLabel = stringResource(R.string.cockcroftGaultActualBW),
                    ideal = result.crClIdeal,
                    idealLabel = stringResource(R.string.cockcroftGaultIdealBW),
                    adjusted = result.crClAdjusted,
                    adjustedLabel = stringResource(R.string.cockcroftGaultAdjustedBW)
                )
            }
            )
        },
        modifier = Modifier
            .pointerInput(Unit) { // Use pointerInput with detectTapGestures
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus() // Clear focus on tap outside
                    }
                )
            },
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            val inputs = cockcroftGaultInput()
            val isJapanese = cockcroftGaultEthnicitySelector()
            result = calculateCockcroftGault(
                age = inputs.age,
                weight = inputs.weight,
                height = inputs.height,
                creatinine = inputs.creatinine,
                cystatinC = inputs.cystatinC,
                isFemale = inputs.isFemale,
                isJapanese = isJapanese
            )
            cockcroftGaultDetail(result)
            cockcroftGaultFormulaCard(isJapanese)
        }
    }
}

private data class CockcroftGaultInputs(
    val age: Double,
    val weight: Double,
    val height: Double,
    val creatinine: Double,
    val cystatinC: Double,
    val isFemale: Boolean
)

@Composable
private fun cockcroftGaultInput(): CockcroftGaultInputs {
    val age = remember { mutableDoubleStateOf(70.0) }
    val weight = remember { mutableDoubleStateOf(60.0) }
    val height = remember { mutableDoubleStateOf(165.0) }
    val creatinine = remember { mutableDoubleStateOf(1.0) }
    val cystatinC = remember { mutableDoubleStateOf(0.8) }

    val sex = buttonAndScoreWithScoreDisplayed(
        optionsWithScores = cockcroftGaultSex,
        title = R.string.space,
        defaultSelectedOption = R.string.male,
        isNumberDisplayed = false
    )
    MedGuidelinesCard {
        MedGuidelinesFlowRow {
            InputValue(
                label = R.string.age,
                value = age,
                japaneseUnit = R.string.years
            )
            InputValue(
                label = R.string.bodyWeight,
                value = weight,
                japaneseUnit = R.string.kg
            )
            InputValue(
                label = R.string.bodyHeight,
                value = height,
                japaneseUnit = R.string.cm
            )
            InputValue(
                label = R.string.creatinine,
                value = creatinine,
                japaneseUnit = R.string.mgdl,
                changedValueRate = 88.4,
                changedUnit = R.string.umolL
            )
            InputValue(
                label = R.string.cystatinC,
                value = cystatinC,
                japaneseUnit = R.string.mgL
            )
        }
    }

    return CockcroftGaultInputs(
        age = age.doubleValue,
        weight = weight.doubleValue,
        height = height.doubleValue,
        creatinine = creatinine.doubleValue,
        cystatinC = cystatinC.doubleValue,
        isFemale = sex == 1
    )
}

/** 日本人を選択すると，JSN eGFRcr（194の式）を使用する。選択は永続化される。 */
@Composable
private fun cockcroftGaultEthnicitySelector(): Boolean {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isJapanese by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        context.dataStore.data
            .map { preferences -> preferences[DataStoreKeys.COCKCROFT_GAULT_IS_JAPANESE_KEY] ?: true }
            .collect { isJapanese = it }
    }

    buttonAndScoreWithScoreDisplayedSelectable(
        optionsWithScores = cockcroftGaultEthnicity,
        title = R.string.space,
        defaultSelectedOption = if (isJapanese) R.string.japanese else R.string.ethnicityOther,
        onOptionSelected = { selectedLabel ->
            val newIsJapanese = selectedLabel == R.string.japanese
            isJapanese = newIsJapanese
            scope.launch {
                context.dataStore.edit { preferences ->
                    preferences[DataStoreKeys.COCKCROFT_GAULT_IS_JAPANESE_KEY] = newIsJapanese
                }
            }
        },
        isNumberDisplayed = false
    )

    return isJapanese
}

@Composable
private fun cockcroftGaultDetail(result: CockcroftGaultResult) {
    var showIdealBodyWeightFormula by remember { mutableStateOf(false) }
    var showAdjustedBodyWeightFormula by remember { mutableStateOf(false) }

    MedGuidelinesCard {
        Column {
//            Text(
//                text = stringResource(R.string.cockcroftGaultBodyWeightCard),
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.textModifier()
//            )
            val bmi = result.bmi
            if (bmi == null) {
                Text(
                    text = stringResource(R.string.cockcroftGaultHeightNote),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.textModifier()
                )
            } else {
                Text(
                    text = stringResource(R.string.cockcroftGaultBmiRow, bmi),
                    fontSize = 18.sp,
                    modifier = Modifier.textModifier()
                )
            }
            Text(
                text = stringResource(
                    R.string.cockcroftGaultCrClRow,
                    stringResource(R.string.cockcroftGaultActualBodyWeight),
                    result.actualBodyWeight,
                    result.crClActual
                ),
                fontSize = 18.sp,
                modifier = Modifier.textModifier()
            )
            val ibw = result.idealBodyWeight
            val crClIdeal = result.crClIdeal
            if (ibw != null && crClIdeal != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.cockcroftGaultCrClRow,
                            stringResource(R.string.cockcroftGaultIdealBodyWeight),
                            ibw,
                            crClIdeal
                        ),
                        fontSize = 18.sp,
                        modifier = Modifier.textModifier()
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = "help",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp)
                            .clickable { showIdealBodyWeightFormula = true },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val adjustedBw = result.adjustedBodyWeight
            val crClAdjusted = result.crClAdjusted
            if (adjustedBw != null && crClAdjusted != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.cockcroftGaultCrClRow,
                            stringResource(R.string.cockcroftGaultAdjustedBodyWeight),
                            adjustedBw,
                            crClAdjusted
                        ),
                        fontSize = 18.sp,
                        modifier = Modifier.textModifier()
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = "help",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp)
                            .clickable { showAdjustedBodyWeightFormula = true },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (showIdealBodyWeightFormula) {
                Popup(onDismissRequest = { showIdealBodyWeightFormula = false }) {
                    PopupClickable(
                        text = stringResource(R.string.cockcroftGaultIdealBodyWeightFormula),
                        onClick = { showIdealBodyWeightFormula = false }
                    )
                }
            }
            if (showAdjustedBodyWeightFormula) {
                Popup(onDismissRequest = { showAdjustedBodyWeightFormula = false }) {
                    PopupClickable(
                        text = stringResource(R.string.cockcroftGaultAdjustedBodyWeightFormula),
                        onClick = { showAdjustedBodyWeightFormula = false }
                    )
                }
            }
//            if (bmi != null) {
//                Text(
//                    text = stringResource(
//                        R.string.cockcroftGaultRecommendedRow,
//                        weightBasisLabel(result.recommendedBasis)
//                    ),
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.textModifier()
//                )
//            }
//            Text(
//                text = stringResource(R.string.cockcroftGaultNote),
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.textModifier()
//            )
        }
    }
}

@Composable
private fun cockcroftGaultFormulaCard(isJapanese: Boolean) {
    MedGuidelinesCard {
        Column {
            Text(
                text = stringResource(R.string.cockcroftGaultFormulaCardTitle),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.textModifier()
            )
            Text(
                text = stringResource(R.string.cockcroftGaultCCrFormula),
                modifier = Modifier.textModifier()
            )

            Text(
                text = stringResource(
                    if (isJapanese) R.string.cockcroftGaultGfrCysFormula else R.string.cockcroftGaultCkdEpiGfrCysFormula
                ),
                modifier = Modifier.textModifier()
            )
            Text(
                text = stringResource(
                    if (isJapanese) R.string.cockcroftGaultJsnGfrFormula else R.string.cockcroftGaultGfrFormula
                ),
                modifier = Modifier.textModifier()
            )
        }
    }
}

/** "GFR (ml/min/1.73m2) cre <値>, cys <値>" の形式で表示する */
private fun androidx.compose.ui.text.AnnotatedString.Builder.appendGfrSummaryRow(
    unit: androidx.compose.ui.text.AnnotatedString,
    cre: Double,
    cys: Double
) {
    append("GFR")
    withStyle(style = SpanStyle(fontSize = 0.6.em)) {
        append(" (")
        append(unit)
        append(")")
    }
    append("\n")
    append("cre ")
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(formatCrCl(cre))
    }
    append(", cys ")
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(formatCrCl(cys))
    }
}

/** "CCr (mL/min) <実体重値> (実体重), <理想体重値>（理想体重）, <補正体重値>（補正体重）" の形式で表示する */
private fun androidx.compose.ui.text.AnnotatedString.Builder.appendCCrSummaryRow(
    mlMin: String,
    actual: Double,
    actualLabel: String,
    ideal: Double?,
    idealLabel: String,
    adjusted: Double?,
    adjustedLabel: String
) {
    append("CCr")
    withStyle(style = SpanStyle(fontSize = 0.6.em)) {
        append(" (")
        append(mlMin)
        append(")")
    }
    append("\n")
    appendCCrValue(actual, actualLabel)
    if (ideal != null) {
        append(", ")
        appendCCrValue(ideal, idealLabel)
    }
    if (adjusted != null) {
        append(", ")
        appendCCrValue(adjusted, adjustedLabel)
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendCCrValue(
    value: Double,
    label: String
) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(formatCrCl(value))
    }
    append(" (")
    append(label)
    append(")")
}

private val crClFormatter = DecimalFormat("0.0")

private fun formatCrCl(crCl: Double): String = crClFormatter.format(crCl)

@Preview
@Composable
fun CockcroftGaultScreenPreview() {
    CockcroftGaultScreen(navController = NavController(LocalContext.current))
}
