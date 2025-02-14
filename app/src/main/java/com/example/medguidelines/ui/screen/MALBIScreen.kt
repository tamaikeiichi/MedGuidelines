package com.example.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.TitleTopAppBar
import com.example.medguidelines.ui.component.parseStyledString
import kotlin.math.absoluteValue
import kotlin.math.log10

@Composable
fun MALBIScreen(navController: NavController) {
    var grade by remember { mutableStateOf("") }
    var score by remember { mutableDoubleStateOf(0.0) }
    var scoreRound by remember { mutableDoubleStateOf(0.0) }
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.mALBITitle,
                navController = navController,
                referenceText = R.string.mALBIRef,
                referenceUrl = R.string.mALBIUrl
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "mALBI $grade ($scoreRound)",
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                score = mALBIInput()
                grade = when {
                    score <= -2.6 -> "1"
                    score < -2.27 -> "2a"
                    score <= -1.39 -> "2b"
                    else -> "3"
                }
                scoreRound = Math.round(score * 100.0)/100.0
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun mALBIInput(): Double {
    val totalBilirubin by remember { mutableDoubleStateOf(1.0) }
    val albumin by remember { mutableDoubleStateOf(4.1) }
    var changed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp),

            ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Bottom),
                verticalAlignment = Alignment.Bottom
            ) {
                NumberInTextFieldTest(
                    label = R.string.totalBilirubin, value = totalBilirubin , width = 80,
                )
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = parseStyledString(R.string.mgdl),
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable { changed = !changed },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Bottom),
                verticalAlignment = Alignment.Bottom
            ) {
                NumberInTextFieldTest(
                    label = R.string.albumin, value = albumin, width = 80,
                )
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = parseStyledString(R.string.gdl),
                        modifier = Modifier
                            .padding(5.dp),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
    val score = (log10(17.1 * totalBilirubin)) * 0.66 + (10 * albumin * (-0.085))

    return score
}



@Composable
fun NumberInTextFieldTest(
    label: Int,
    value: MutableDoubleState,
    width: Int,
) {
    var text by remember { mutableStateOf(value.toString()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) {
            text = ""
        }
    }
    val fontSize = calculateFontSize(text)

    TextField(
        label = { Text(parseStyledString(label)) },
        value = text,
        onValueChange = {newText ->
            if (newText.matches(Regex("[0-9]*\\.?[0-9]*")) || newText.isEmpty()) {
                text = newText
                value.doubleValue = newText.toDoubleOrNull() ?: 0.0
            }},
        modifier = Modifier
            .padding(5.dp)
            .width(width.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
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
        interactionSource = interactionSource
    )
}

private fun calculateFontSize(text: String): TextUnit {
    val baseSize = 28.sp
    val minSize = 12.sp
    val maxLength = 5

    return when {
        text.length <= maxLength / 2 -> baseSize
        text.length <= maxLength -> (baseSize.value - (text.length - maxLength / 2) * 2).sp
        else -> minSize
    }
}

@Preview
@Composable
fun MALBIScreenPreview(){
    MALBIScreen(navController = NavController(LocalContext.current))
}