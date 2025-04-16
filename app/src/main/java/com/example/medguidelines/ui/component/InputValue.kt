package com.example.medguidelines.ui.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.medguidelines.R

@Composable
fun InputValue(
    label: Int,
    value: MutableDoubleState,
    unit: Int,
    changeUnit: MutableState<Boolean> = remember { mutableStateOf(true) },
    changedValueRate: Double = 1.0,
    changedUnit: Int = R.string.space
){
    val textMeasurer = rememberTextMeasurer()
    val labelWidth = textMeasurer.measure(text = stringResource(label)).size.width
    val formatter = remember { DecimalFormat("#") }
    Row(
        modifier = Modifier
            .padding(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        NumberInTextField(
            label = label, value = value, width = Math.round(labelWidth*0.15).toInt()+80,
            multiplier = if (changeUnit.value) 1.0 else changedValueRate,
            formatter = formatter
        )
        if (changedValueRate == 1.0){
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = parseStyledString(unit),
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                ClickableText(
                    text = if (changeUnit.value) unit else changedUnit,
                    onChanged = { changeUnit.value = !changeUnit.value },
                    changed = changeUnit.value
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}