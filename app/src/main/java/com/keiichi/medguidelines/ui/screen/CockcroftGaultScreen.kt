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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.CockcroftGaultResult
import com.keiichi.medguidelines.data.WeightBasis
import com.keiichi.medguidelines.data.calculateCockcroftGault
import com.keiichi.medguidelines.data.cockcroftGaultSex
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesFlowRow
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.PopupClickable
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayed
import java.text.DecimalFormat

@Composable
fun CockcroftGaultScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var result by remember {
        mutableStateOf(
            CockcroftGaultResult(
                actualBodyWeight = 0.0,
                crClActual = 0.0,
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
                    TextAndUrl(R.string.idealAndAdjustedBodyWeightRef, R.string.idealAndAdjustedBodyWeightUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(displayText = buildAnnotatedString {
                append(stringResource(R.string.cockcroftGaultActualBodyWeight))
                append(": ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(formatCrCl(result.crClActual))
                }
                append(" ")
                withStyle(style = SpanStyle(fontSize = 0.8.em)) {
                    append(stringResource(R.string.mlMin))
                }
                if (result.recommendedBasis != WeightBasis.ACTUAL) {
                    append("\n")
                    append(weightBasisLabel(result.recommendedBasis))
                    append(": ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(formatCrCl(result.recommendedCrCl))
                    }
                    append(" ")
                    withStyle(style = SpanStyle(fontSize = 0.8.em)) {
                        append(stringResource(R.string.mlMin))
                    }
                }
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
            result = cockcroftGaultInput()
            cockcroftGaultDetail(result)
        }
    }
}

@Composable
private fun cockcroftGaultInput(): CockcroftGaultResult {
    val age = remember { mutableDoubleStateOf(70.0) }
    val weight = remember { mutableDoubleStateOf(60.0) }
    val height = remember { mutableDoubleStateOf(165.0) }
    val creatinine = remember { mutableDoubleStateOf(1.0) }

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
        }
    }

    return calculateCockcroftGault(
        age = age.doubleValue,
        weight = weight.doubleValue,
        height = height.doubleValue,
        creatinine = creatinine.doubleValue,
        isFemale = sex == 1
    )
}

@Composable
private fun cockcroftGaultDetail(result: CockcroftGaultResult) {
    var showBodyWeightFormula by remember { mutableStateOf(false) }

    MedGuidelinesCard {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.textModifier()
            ) {
                Text(
                    text = stringResource(R.string.cockcroftGaultBodyWeightCard),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showBodyWeightFormula = !showBodyWeightFormula }
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = "help",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(18.dp)
                        .clickable { showBodyWeightFormula = !showBodyWeightFormula },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (showBodyWeightFormula) {
                Popup {
                    PopupClickable(
                        text = stringResource(R.string.cockcroftGaultBodyWeightFormula),
                        onClick = { showBodyWeightFormula = false }
                    )
                }
            }
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
                modifier = Modifier.textModifier()
            )
            val ibw = result.idealBodyWeight
            val crClIdeal = result.crClIdeal
            if (ibw != null && crClIdeal != null) {
                Text(
                    text = stringResource(
                        R.string.cockcroftGaultCrClRow,
                        stringResource(R.string.cockcroftGaultIdealBodyWeight),
                        ibw,
                        crClIdeal
                    ),
                    modifier = Modifier.textModifier()
                )
            }
            val adjustedBw = result.adjustedBodyWeight
            val crClAdjusted = result.crClAdjusted
            if (adjustedBw != null && crClAdjusted != null) {
                Text(
                    text = stringResource(
                        R.string.cockcroftGaultCrClRow,
                        stringResource(R.string.cockcroftGaultAdjustedBodyWeight),
                        adjustedBw,
                        crClAdjusted
                    ),
                    modifier = Modifier.textModifier()
                )
            }
            if (bmi != null) {
                Text(
                    text = stringResource(
                        R.string.cockcroftGaultRecommendedRow,
                        weightBasisLabel(result.recommendedBasis)
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.textModifier()
                )
            }
//            Text(
//                text = stringResource(R.string.cockcroftGaultNote),
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.textModifier()
//            )
        }
    }
}

@Composable
private fun weightBasisLabel(basis: WeightBasis): String = stringResource(
    when (basis) {
        WeightBasis.ACTUAL -> R.string.cockcroftGaultActualBodyWeight
        WeightBasis.IDEAL -> R.string.cockcroftGaultIdealBodyWeight
        WeightBasis.ADJUSTED -> R.string.cockcroftGaultAdjustedBodyWeight
    }
)

private val crClFormatter = DecimalFormat("0.0")

private fun formatCrCl(crCl: Double): String = crClFormatter.format(crCl)

@Preview
@Composable
fun CockcroftGaultScreenPreview() {
    CockcroftGaultScreen(navController = NavController(LocalContext.current))
}
