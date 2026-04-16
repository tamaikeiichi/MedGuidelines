package com.keiichi.medguidelines.ui.component

import android.icu.text.DecimalFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInTextField(
    label: Int,
    value: MutableDoubleState,
    width: Int,
    multiplier: Double = 1.0,
    isJapaneseUnit: MutableState<Boolean> = remember { mutableStateOf(true) },
    formatter: DecimalFormat = remember { DecimalFormat("#.##") },
    changeValueRate: Double = 1.0,
    onFocusChanged: (Boolean) -> Unit = {},
    isSetByOtherComponent: Boolean = false,
    onLabelClick: (() -> Unit)? = null // ラベルクリック時のコールバック
) {
    var text by remember { mutableStateOf(formatter.format(value.doubleValue * multiplier)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(Unit) {
        snapshotFlow { isJapaneseUnit.value }
            .drop(1)
            .collect { currentIsJapaneseUnitValue ->
                if (currentIsJapaneseUnitValue) {
                    text = formatter.format((value.doubleValue))
                } else {
                    text = formatter.format((value.doubleValue * changeValueRate))
                }
            }
    }

    LaunchedEffect(value.doubleValue){
        text = formatter.format(value.doubleValue)
    }

    LaunchedEffect(isFocused) {
        if (!isFocused) {
            text =
                if (isJapaneseUnit.value) {
                    formatter.format((value.doubleValue))
                } else {
                    formatter.format((value.doubleValue) * changeValueRate)
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
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = parseStyledString(label),
                    modifier = if (onLabelClick != null) {
                        Modifier.clickable { onLabelClick() }
                    } else {
                        Modifier
                    }
                )
                if (onLabelClick != null) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = "help",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(18.dp) // ラベル縮小対策で大きめに設定
                            .clickable { onLabelClick() },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        value = text,
        onValueChange = { newText ->
            text = newText
            if (isJapaneseUnit.value) {
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0)
            } else {
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) / changeValueRate
            }
        },
        modifier = Modifier
            .padding(5.dp)
            .width(width.dp)
            .onFocusChanged { focusState ->
                onFocusChanged(focusState.isFocused)
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        textStyle = TextStyle.Default.copy(
            fontSize = fontSize,
            textAlign = TextAlign.Right,
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
            unfocusedTextColor = if (isSetByOtherComponent) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.onBackground,
            focusedTextColor = if (isSetByOtherComponent) MaterialTheme.colorScheme.surfaceDim else MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview(showBackground = true, name = "はてなあり (値あり)")
@Composable
fun PreviewNumberInTextFieldWithHelp() {
    val mockValue = remember { mutableDoubleStateOf(1234.5) }
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            NumberInTextField(
                label = R.string.bukkaTaiouRyo,
                value = mockValue,
                width = 250,
                onLabelClick = {} // 非nullでアイコンを表示
            )
        }
    }
}

@Preview(showBackground = true, name = "はてなあり (空の状態)")
@Composable
fun PreviewNumberInTextFieldEmptyWithHelp() {
    val mockValue = remember { mutableDoubleStateOf(0.0) }
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            // 値が0かつフォーカスなしの状態。ラベルが中央に大きく表示されるはずです。
            NumberInTextField(
                label = R.string.bukkaTaiouRyo,
                value = mockValue,
                width = 250,
                onLabelClick = {}
            )
        }
    }
}
