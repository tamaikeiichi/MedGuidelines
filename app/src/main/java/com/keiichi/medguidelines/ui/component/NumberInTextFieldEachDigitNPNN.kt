package com.keiichi.medguidelines.ui.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInTextFieldEachDigitNPNN(
    label: Int,
    value: MutableDoubleState,
    width: Int,
    multiplier: Double = 1.0,
    formatter: DecimalFormat = remember { DecimalFormat("#.##") }
) {
    var text1 by remember { mutableStateOf(formatter.format(value.doubleValue * multiplier)) }
    var text2 by remember { mutableStateOf(formatter.format(value.doubleValue * multiplier)) }
    var text3 by remember { mutableStateOf(formatter.format(value.doubleValue * multiplier)) }
    val interactionSource1 = remember { MutableInteractionSource() }
    val isFocused1 by interactionSource1.collectIsFocusedAsState()
    val interactionSource2 = remember { MutableInteractionSource() }
    val isFocused2 by interactionSource2.collectIsFocusedAsState()
    val interactionSource3 = remember { MutableInteractionSource() }
    val isFocused3 by interactionSource3.collectIsFocusedAsState()

    LaunchedEffect(isFocused1, multiplier) {
        if (!isFocused1) {
            text1 =
                formatter.format(
                    (value.doubleValue * multiplier)//.toString()
                )
        }
    }
    LaunchedEffect(isFocused1, isFocused2, isFocused3) {
        if (isFocused1) {
            text1 = ""
        }
        if (isFocused2) {
            text2 = ""
        }
        if (isFocused3) {
            text3 = ""
        }
    }

    val fontSize = calculateFontSize(text1)
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        TextFieldOneDigit(
            //label = { Text(parseStyledString(label)) },
            value = text1,
            onValueChange = { newText ->
                text1 = newText
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) // multiplier
            },
            modifier = Modifier,
                //.padding(5.dp)
                //.width(10.dp),
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
            interactionSource = interactionSource1,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.onErrorContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.errorContainer,
            )
        )
        Text(
            text = ".",
            style = TextStyle.Default.copy(
                color = MaterialTheme.colorScheme.primary,
                background = MaterialTheme.colorScheme.onPrimary,
            )
        )
        TextFieldOneDigit(
            //label = 1,//{ Text(parseStyledString(label)) },
            value = text2,
            onValueChange = { newText ->
                text2 = newText
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) // multiplier
            },
            modifier = Modifier
                //.padding(5.dp)
                .width(width.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = fontSize,
                //textAlign = TextAlign
                //    .Right,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Bottom,
                    trim = LineHeightStyle.Trim.Both
                )
            ),
            maxLines = 1,
            interactionSource = interactionSource2,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.onErrorContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.errorContainer,
            )
        )
        TextFieldOneDigit(
            //label = { Text(parseStyledString(label)) },
            value = text3,
            onValueChange = { newText ->
                text3 = newText
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) // multiplier
            },
            modifier = Modifier
                //.padding(5.dp)
                .width(width.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = fontSize,
                //textAlign = TextAlign
                //    .Right,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Bottom,
                    trim = LineHeightStyle.Trim.Both
                )
            ),
            maxLines = 1,
            interactionSource = interactionSource3,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.onErrorContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.errorContainer,
            )
        )
    }
}



@Preview
@Composable
fun NumberInTextFieldEachDigitNPNNPreview(){
    NumberInTextFieldEachDigitNPNN(
        label = R.string.ph,
        value = remember { mutableDoubleStateOf(0.0) },
        width = 50
    )
}