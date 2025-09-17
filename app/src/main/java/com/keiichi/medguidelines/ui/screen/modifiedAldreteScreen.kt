package com.keiichi.medguidelines.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.aldreteCirculation
import com.keiichi.medguidelines.data.aldreteConsciousness
import com.keiichi.medguidelines.data.aldreteExtremeties
import com.keiichi.medguidelines.data.aldreteRespiration
import com.keiichi.medguidelines.data.aldreteSaturation
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.buttonAndScoreExpandContentWithScore
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScore
import com.keiichi.medguidelines.ui.component.calculateFontSize
import com.keiichi.medguidelines.ui.component.parseStyledString
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun AldreteScreen(navController: NavController) {
    var totalScore by remember { mutableIntStateOf(0) }
    var diagnosis by remember { mutableStateOf("") }

    val displayString = buildAnnotatedString{
        append("Score: $totalScore ($diagnosis)")
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.aldreteTitle,
                navController = navController,
                helpMessageResId = R.string.pacu,
                references = listOf(
                    TextAndUrl(R.string.aldreteRefTitle, R.string.aldreteUrl)
                )
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable (
                displayText = displayString,
                fontSize = 30.sp,
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            totalScore = aldreteTotalScore() // Assuming childPughTotalScore() returns an Int
            //Text(text = totalScore.toString())

            diagnosis = when (totalScore) {
                in 9..10 -> parseStyledString(R.string.acceptableForDischarge).toString()
                //"established"
                in 0..8 -> parseStyledString(R.string.closeObservation).toString()// "possible"
                else -> parseStyledString(R.string.notAssessed).toString()// "very unlikely"
            }
        }
    }
}

@Composable
fun aldreteTotalScore(): Int {
    val systolicBp = rememberSaveable { mutableDoubleStateOf(120.0) }
    val  activityScore = buttonAndScoreWithScore(
        optionsWithScores = aldreteExtremeties,
        title = R.string.acitivity,
    )
    val respirationScore =
        buttonAndScoreWithScore(
            aldreteRespiration,
            R.string.respiration,
        )
    val circulationScore = buttonAndScoreExpandContentWithScore(
        aldreteCirculation,
        R.string.circulation,
        {

            Column(){
                Text(
                    text = parseStyledString(R.string.systolicBloodPressure), // Using stringResource directly
                    // Replace with parseStyledString(titleNote) if styling is needed
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
                bpPressure(systolicBp)
//                Row(
//                    verticalAlignment = Alignment.Bottom,
//                ){
//                    InputValue(
//                        label = R.string.systolicBloodPressure,
//                        value = systolicBp,
//                        japaneseUnit = R.string.mmhg,
//                    )
//                    Column()
//                    {
//                        Text(
//                            text = parseStyledString(R.string.twentyTofortynine), // Using stringResource directly
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(top = 2.dp),
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                        val lower = systolicBp.value * 1.21
//                        val upper = systolicBp.value * 1.50
//
//                        val lowerResult = if (lower == floor(lower)) { // Check if it's a whole number
//                            // Decimal place is zero
//                            lower + 1.0
//                        } else {
//                            // Decimal place is not zero, round up
//                            ceil(lower)
//                        }
//
//                        val upperResult = if (upper == floor(upper)) { // Check if it's a whole number
//                            // Decimal place is zero
//                            upper - 1.0
//                        } else {
//                            // Decimal place is not zero, round up
//                            floor(upper)
//                        }
//
//                        val displayString =
//                            buildAnnotatedString {
//                                append(lowerResult.toString())
//                                append(" to ")
//                                append(upperResult.toString())
//                            }
//                        val fontSize = calculateFontSize(displayString.toString())
//                        Log.d("FontSizeDebug", "Calculated size: $fontSize, String: '${displayString.toString()}'")
//
//                        Text(
//                            text = displayString, // Using stringResource directly
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(top = 2.dp),
//                            fontSize = 22.sp //fontSize
//                        )
//                    }
//                }
            }

        }
    )
    val consciousnessScore =
        buttonAndScoreWithScore(
            aldreteConsciousness,
            R.string.cousciousness,
            R.string.space,
            //defaultSelectedOption =  R.string.absent
        )
    val saturationScore = buttonAndScoreWithScore(
        aldreteSaturation,
        R.string.o2saturation,
        //R.string.space,
        //defaultSelectedOption =  R.string.normal08
    )

    val totalScore =
        activityScore +
                respirationScore +
                circulationScore +
                consciousnessScore +
                saturationScore

    return totalScore
}

@Composable
private fun bpPressure(
    systolicBp: MutableDoubleState
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        //horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        InputValue(
            label = R.string.systolicBloodPressure,
            value = systolicBp,
            japaneseUnit = R.string.mmhg,
        )
        Column(
            modifier = Modifier.padding(start = 24.dp)
        )
        {
            Text(
                text = parseStyledString(R.string.plusTwentyTofortynine), // Using stringResource directly
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val plusLower = systolicBp.value * 1.20
            val plusUpper = systolicBp.value * 1.50

            val plusLowerResult = if (plusLower == floor(plusLower)) { // Check if it's a whole number
                // Decimal place is zero
                plusLower + 1.0
            } else {
                // Decimal place is not zero, round up
                ceil(plusLower)
            }

            val plusUpperResult = if (plusUpper == floor(plusUpper)) { // Check if it's a whole number
                // Decimal place is zero
                plusUpper - 1.0
            } else {
                // Decimal place is not zero, round up
                floor(plusUpper)
            }

            val plusDisplayString =
                buildAnnotatedString {
                    append(plusLowerResult.toInt().toString())
                    pushStyle(SpanStyle(fontSize = 18.sp))
                    append(" – ")
                    pop()
                    append(plusUpperResult.toInt().toString())
                }
            val fontSize = calculateFontSize(plusDisplayString.toString())
            Log.d("FontSizeDebug", "Calculated size: $fontSize, String: '${plusDisplayString.toString()}'")

            Text(
                text = plusDisplayString, // Using stringResource directly
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 22.sp //fontSize
            )
            Text(
                text = parseStyledString(R.string.minusTwentyTofortynine), // Using stringResource directly
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val minusLower = systolicBp.value * 0.80
            val minusUpper = systolicBp.value * 0.50

            val minusLowerResult = if (minusLower == floor(minusLower)) { // Check if it's a whole number
                // Decimal place is zero
                minusLower - 1.0
            } else {
                // Decimal place is not zero, round up
                ceil(minusLower)
            }

            val minusUpperResult = if (minusUpper == floor(minusUpper)) { // Check if it's a whole number
                // Decimal place is zero
                minusUpper + 1.0
            } else {
                // Decimal place is not zero, round up
                floor(minusUpper)
            }

            val minusDisplayString =
                buildAnnotatedString {
                    append(minusLowerResult.toInt().toString())
                    pushStyle(SpanStyle(fontSize = 18.sp))
                    append(" – ")
                    pop()
                    append(minusUpperResult.toInt().toString())

                }
            val minusFontSize = calculateFontSize(plusDisplayString.toString())
            Log.d("FontSizeDebug", "Calculated size: $fontSize, String: '${plusDisplayString.toString()}'")

            Text(
                text = minusDisplayString, // Using stringResource directly
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
                fontSize = 22.sp //fontSize
            )
        }
    }
}

//@Preview
//@Composable
//fun AldreteScorePreview() {
//    AldreteScreen(navController = NavController(LocalContext.current))
//}

@Preview
@Composable
fun AldreteScorePreview2() {
    bpPressure(remember { mutableDoubleStateOf(120.0) })
}