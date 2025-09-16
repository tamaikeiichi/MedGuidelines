package com.keiichi.medguidelines.ui.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.collect

//@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInTextField(
    label: Int,
    value: MutableDoubleState,
    width: Int,
    multiplier: Double = 1.0,
    isJapaneseUnit: MutableState<Boolean> = remember { mutableStateOf(true) },
    formatter: DecimalFormat = remember { DecimalFormat("#.##") },
    changeValueRate: Double = 1.0
) {
    var text by remember { mutableStateOf(formatter.format(value.doubleValue * multiplier)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(Unit) { // Runs once to set up the flow collection
        snapshotFlow { isJapaneseUnit.value } // Create a flow that emits when isJapaneseUnit.value changes
            .drop(1) // Important: Skip the initial emission of isJapaneseUnit.value
            .collect { currentIsJapaneseUnitValue ->
                // This block will only execute when isJapaneseUnit.value changes,
                // not on the initial composition.
                if (currentIsJapaneseUnitValue) {
                    text = formatter.format((value.doubleValue))
                } else {
                    text = formatter.format((value.doubleValue * changeValueRate))
                }
            }
    }

    LaunchedEffect(isFocused//, isJapaneseUnit.value//, multiplier
         ) {
        if (!isFocused) {
            //if (isJapaneseUnit) {
            text =
                if (isJapaneseUnit.value) {
                    formatter.format(
                        (value.doubleValue
                                //* multiplier
                                )
                    )
                } else {
                    formatter.format(
                        (value.doubleValue
                                //* multiplier
                                ) * changeValueRate
                    )
                }
        }
    }
    LaunchedEffect(isFocused) {
        if (isFocused) {
            text = ""
        }
    }
    val fontSize = calculateFontSize(text)
    TextField(
        label = { Text(parseStyledString(label)) },
        value = text,
        onValueChange = { newText ->
            text = newText
            if (isJapaneseUnit.value) {
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) // multiplier
            } else {
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) / changeValueRate// changeValueRate
            }
        },
        modifier = Modifier
            .padding(5.dp)
            .width(width.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
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
        interactionSource = interactionSource,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.error,
        )
    )
}
