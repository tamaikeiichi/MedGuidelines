package com.keiichi.medguidelines.ui.component

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keiichi.medguidelines.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInTextFieldEachDigitNPNN(
    modifier: Modifier = Modifier,
    label: Int,
    value: MutableDoubleState,
    width: Int,
    multiplier: Double = 1.0,
    formatter: DecimalFormat = remember { DecimalFormat("#.##") }
) {
    val interactionSource1 = remember { MutableInteractionSource() }
    val isFocused1 by interactionSource1.collectIsFocusedAsState()
    val interactionSource2 = remember { MutableInteractionSource() }
    val isFocused2 by interactionSource2.collectIsFocusedAsState()
    val interactionSource3 = remember { MutableInteractionSource() }
    val isFocused3 by interactionSource3.collectIsFocusedAsState()

    val focusRequester1 = remember { FocusRequester() } // Create FocusRequesters
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current // Get the FocusManager

    val (initialText1, initialText2, initialText3) = separateDoubleIntoDigits(value.doubleValue * multiplier)
    var text1 by remember { mutableStateOf(initialText1) }
    var text2 by remember { mutableStateOf(initialText2) }
    var text3 by remember { mutableStateOf(initialText3) }
//    LaunchedEffect(value.doubleValue) {
//        val (newText1, newText2, newText3) = separateDoubleIntoDigits(value.doubleValue * multiplier)
//        text1 = newText1
//        text2 = newText2
//        text3 = newText3
//    }
//
    LaunchedEffect(text1) {
        if (text1.length == 1 && isFocused1) {
            focusManager.moveFocus(FocusDirection.Right)
            //focusRequester2.requestFocus() // Request focus on the second TextField
            value.doubleValue = combineDigitsToDouble(text1, text2, text3)
        }
    }
    LaunchedEffect(text2) {
        if (text2.length == 1 && isFocused2) {
            focusManager.moveFocus(FocusDirection.Right)
            //focusRequester3.requestFocus() // Request focus on the second TextField
            value.doubleValue = combineDigitsToDouble(text1, text2, text3)
        }
    }

    LaunchedEffect(text3) {
        if (text3.length == 1 && isFocused3) {
            value.doubleValue = combineDigitsToDouble(text1, text2, text3)
            focusManager.moveFocus(FocusDirection.Right)
        }
    }

    LaunchedEffect(isFocused1 || isFocused2 || isFocused3) {
        if (!isFocused1 || !isFocused2 || !isFocused3) {
            val (newText1, newText2, newText3) = separateDoubleIntoDigits(value.doubleValue * multiplier)
            text1 = newText1
            text2 = newText2
            text3 = newText3
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

    val fontSize = calculateFontSize(text1) * 1

    Column() {
        Text(
            modifier = Modifier.padding(5.dp),
            text = parseStyledString(label),
            style = TextStyle(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = modifier.padding(5.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextFieldOneDigit(
                value = text1,
                onValueChange = { newText ->
                    text1 = newText
                    value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) // multiplier
                },
                modifier = Modifier
                    .focusRequester(focusRequester1)
                //.padding(5.dp)
                //.width(20.dp)
                ,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(
                    fontSize = fontSize,
                    textAlign = TextAlign
                        .Center,
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
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    background = MaterialTheme.colorScheme.onPrimary,
                    fontSize = fontSize,
                ),
                modifier = Modifier.padding(vertical = 1.dp)
            )
            TextFieldOneDigit(
                //label = 1,//{ Text(parseStyledString(label)) },
                value = text2,
                onValueChange = { newText ->
                    text2 = newText
                    value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) // multiplier
                },
                modifier = Modifier
                    .focusRequester(focusRequester2)
                    //.padding(5.dp)
                    .width(width.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle.Default.copy(
                    fontSize = fontSize,
                    textAlign = TextAlign
                        .Center,
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
                    .focusRequester(focusRequester3)
                    //.padding(5.dp)
                    .width(width.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle.Default.copy(
                    fontSize = fontSize,
                    textAlign = TextAlign
                        .Center,
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
}

@SuppressLint("DefaultLocale")
fun separateDoubleIntoDigits(number: Double): Triple<String, String, String> {
    // 1. Format to String
    val formattedNumber = String.format("%.2f", number) // "7.41"

    // 2. Remove the decimal point
    val numberWithoutDecimal = formattedNumber.replace(".", "") // "741"

    // 3. Check if the string is composed of 3 digits
    if (numberWithoutDecimal.length != 3) {
        throw IllegalArgumentException("The double number must have a integer part of one digit and a decimal part of 2 digit")
    }

    // 4. Separate the digits
    val digit1 = numberWithoutDecimal[0].toString() // "7"
    val digit2 = numberWithoutDecimal[1].toString() // "4"
    val digit3 = numberWithoutDecimal[2].toString() // "1"

    return Triple(digit1, digit2, digit3)
}

fun combineDigitsToDouble(text1: String, text2: String, text3: String): Double {
    // 1. Concatenate the digits into a single string
    val combinedString = "$text1.$text2$text3" // e.g., "7.41"

    // 2. Convert to Double
    return combinedString.toDoubleOrNull() ?: 7.0
}

@Preview
@Composable
fun NumberInTextFieldEachDigitNPNNPreview(){
    NumberInTextFieldEachDigitNPNN(
        label = R.string.ph,
        value = remember { mutableDoubleStateOf(7.14) },
        width = 50
    )
}