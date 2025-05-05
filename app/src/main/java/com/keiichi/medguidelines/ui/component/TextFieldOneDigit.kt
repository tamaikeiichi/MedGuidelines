package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldOneDigit(
    value: String,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(
        color = color,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Bottom,
            trim = LineHeightStyle.Trim.Both
        )
    ),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.primary,
        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
    )
) {
        BasicTextField(
            modifier = Modifier
                .width(
                    20.dp
                )
                .height(
                    35.dp
                )

                .padding(2.dp)
                .fillMaxHeight(),
            textStyle = textStyle
//                (
//                color = color,
//                fontSize = fontSize,
//                textAlign = TextAlign.Center,
//                lineHeightStyle = LineHeightStyle(
//                    alignment = LineHeightStyle.Alignment.Bottom,
//                    trim = LineHeightStyle.Trim.Both
//                )
//            )
            ,
            value = value,
            onValueChange = {
                if (it.length <= 1) { // Add this limitation
                    onValueChange(it)
                }
            },
            enabled = enabled,
            readOnly = readOnly,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                // places leading icon, text field with label and placeholder, trailing icon
                Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    TextFieldDefaults.DecorationBox(
                        value = value,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        placeholder = placeholder,
                        label = label,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        supportingText = supportingText,
                        shape = shape,
                        singleLine = singleLine,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                    )
                }
            }
        )
    }

/** Padding from text field top to label top, and from input field bottom to text field bottom */
/*@VisibleForTesting*/
internal val TextFieldWithLabelVerticalPadding = 8.dp

@Preview
@Composable
fun TextFieldOneDigitPreview(){
    TextFieldOneDigit(
        value = "0",
        onValueChange = {},
        fontSize = 20.dp.value.sp,
        color = Color.Black
    )
}