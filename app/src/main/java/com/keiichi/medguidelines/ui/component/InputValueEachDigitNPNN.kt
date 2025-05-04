package com.keiichi.medguidelines.ui.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R
import kotlin.math.roundToInt

@Composable
fun InputValueEachDigitNPNN(
    label: Int,
    value: MutableDoubleState,
    unit: Int,
    changeUnit: MutableState<Boolean> = remember { mutableStateOf(true) },
    changedValueRate: Double = 1.0,
    changedUnit: Int = R.string.space,
) {
    //val textMeasurer = rememberTextMeasurer()
    //val labelWidth = textMeasurer.measure(text = stringResource(label)).size.width
    val formatter = remember { DecimalFormat("#.##") }

    var textWidth by remember { mutableStateOf(0f) } // State to hold the width
    Layout(
        content = {
            Text(
                text = parseStyledString(label),
                onTextLayout = { textLayoutResult ->
                    textWidth = textLayoutResult.size.width.toFloat() // Get width in pixels
                }
            )
        },
        modifier = Modifier.size(0.dp)
    ) { measurables, constraints ->
        val textPlaceable = measurables.first().measure(constraints)

        layout(0, 0) {
            textPlaceable.place(0, 0)
        }
    }

    Row(
        modifier = Modifier
            .padding(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        NumberInTextFieldEachDigitNPNN(
            label = label, value = value,
            width = (textWidth * 0.3).roundToInt() + 90,//(labelWidth * 0.5).roundToInt()+50,
            multiplier = if (changeUnit.value) 1.0 else changedValueRate,
            formatter = formatter
        )

    }
}

@Preview
@Composable
fun InputValueEachDigitNPNNPreview() {
    val value = remember { mutableDoubleStateOf(7.41) } // Use remember
    val changeUnit = remember { mutableStateOf(true) } // Use remember

    InputValueEachDigitNPNN(
        label = R.string.ph,
        value = value,
        unit = R.string.space,
        changeUnit = changeUnit
    )
}