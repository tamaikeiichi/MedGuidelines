package com.example.medguidelines.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.compose.inverseOnSurfaceLight
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.TitleTopAppBar
import com.example.medguidelines.ui.component.parseStyledString
import com.example.medguidelines.ui.component.textAndUrl
import kotlin.math.sqrt

val references = listOf(
    textAndUrl(R.string.mALBIRef, R.string.mALBIUrl),
    textAndUrl(R.string.netakiridoRefTitle, R.string.netakiridoUrl)
)

@Composable
fun LiverFibrosisScoreSystemScreen(navController: NavController) {
    var grade by remember { mutableStateOf("") }
    var score by remember { mutableDoubleStateOf(0.0) }
    var scoreRound by remember { mutableDoubleStateOf(0.0) }
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.liverFibrosisScoreSystemTitle,
                navController = navController,
                references = references,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
            ,
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                score = Fib4Calculator()
                val mediumColorValue = CalculateGradientProportion(
                    maxValue = 5F,
                    minValue = 0.1F,
                    firstThreshold = 1.3F,
                    secondThreshold = 2.67F,
                    fibrosisScore = score.toFloat())
//                GraphFibrosis(
//                    width = 200F,
//                    fibrosisScore = 100F,
//                    mediumColorValue = mediumColorValue
//                )
            }
        }
    }
}

@Composable
private fun CalculateGradientProportion(
    maxValue: Float,
    minValue: Float,
    firstThreshold: Float,
    secondThreshold: Float,
    fibrosisScore: Float
)//: Float
{
val mediumColorValue =
    1 - (secondThreshold - firstThreshold) / (maxValue - minValue)
    //return mediumColorValue
    GraphFibrosis(
        width = 200F,
        fibrosisScore = fibrosisScore,
        mediumColorValue = mediumColorValue,
        maxValue = maxValue,
        minValue = minValue,
        firstThreshold = firstThreshold,
        secondThreshold = secondThreshold,
    )
}

@Composable
fun GraphFibrosis(
    width: Float,
    fibrosisScore: Float,
    mediumColorValue: Float,
    maxValue: Float,
    minValue: Float,
    firstThreshold: Float,
    secondThreshold: Float,
) {
    val canvasHeightValue = 200
    val canvasHeight = canvasHeightValue.dp
    Canvas(
        modifier = Modifier
            .size(canvasHeight)
    )
    {
        val rectColorStops = arrayOf(
            0.0f to Color(0xFFFF0180),
            mediumColorValue to Color(0xFFFFE30B),
            1.0f to Color(0xFF0B7CFF))
        val rectGradient = Brush.verticalGradient(
            //colors = rectColors,
            colorStops = rectColorStops,
            startY = size.height * (0),
            endY = size.height * (1F / 1F)
        )
        val rectCornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
        val circleSize = 20F
        val circleColors = listOf(Color(0xFFFF1C07), Color(0xFFFDFDFF))
        val circleHeight =
            if (fibrosisScore > maxValue) 0F
            else if (fibrosisScore < minValue) size.height
            else  (fibrosisScore/ (maxValue-minValue)) * size.height
        val circleGradient = Brush.radialGradient(
            colors = circleColors,
            center = Offset(x = width / 2, y = circleHeight),
            radius = circleSize * 1.1F
        )
        drawRoundRect(
            size = Size(width = width, height = size.height),
            brush = rectGradient,
            cornerRadius = rectCornerRadius
        )
        drawCircle(
            brush = circleGradient,
            radius = circleSize,
            center = Offset(x = width / 2, y = circleHeight),
        )
        drawLine(
            color = Color.Black,
            start = Offset(x = 0F, y = circleHeight),
            end = Offset(x = width, y = circleHeight),
            strokeWidth = 2F
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Fib4Calculator(): Double {
    val factor1 = remember { mutableDoubleStateOf(40.0) }
    val factor2 = remember { mutableDoubleStateOf(30.0) }
    val factor3 = remember { mutableDoubleStateOf(150.0) }
    val factor4 = remember { mutableDoubleStateOf(30.0) }
    var changedFactor1Unit by remember { mutableStateOf(true) }
    var changedFactor2Unit by remember { mutableStateOf(true) }
    var changedFactor3Unit by remember { mutableStateOf(true) }
    var changedFactor4Unit by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                //.align(Alignment.Bottom)
                .wrapContentHeight(
                    align = Alignment.Bottom
                ),
            //verticalArrangement = Arrangement.Bottom,
        ) {
            InputValue(
                label = R.string.age, value = factor1, width = 100,
                unit = R.string.years, changeUnit = changedFactor1Unit, changedValueRate = 1.0
            )
            InputValue(
                label = R.string.ast, value = factor2, width = 100,
                unit = R.string.iul, changeUnit = changedFactor2Unit, changedValueRate = 1.0
            )
            InputValue(
                label = R.string.plateletCount, value = factor3, width = 100,
                unit = R.string.unit109L, changeUnit = changedFactor3Unit, changedValueRate = 1.0
            )
            InputValue(
                label = R.string.alt, value = factor4, width = 100,
                unit = R.string.iul, changeUnit = changedFactor4Unit, changedValueRate = 1.0
            )
        }
    }
    val score = (factor1.doubleValue * factor2.doubleValue) /
            (factor3.doubleValue * sqrt(factor4.doubleValue))
    return score
}

@Composable
fun InputValue(
    label: Int,
    value: MutableDoubleState,
    width: Int,
    unit: Int,
    changeUnit: Boolean,
    changedValueRate: Double
){

    Row(
        modifier = Modifier
            .padding(4.dp),
        //.align(Alignment.Bottom),
        verticalAlignment = Alignment.Bottom,
    ) {
        NumberInTextField(
            label = label, value = value, width = width,
            multiplier = if (changeUnit) 1.0 else changedValueRate
        )
        Column(
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = parseStyledString(unit),
                //onChanged = { changedBilirubinUnit = !changedBilirubinUnit },
                //changed = changedBilirubinUnit
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ClickableText(
    text: Int,
    onChanged: (Boolean) -> Unit,
    changed: Boolean
){
    Text(
        text = parseStyledString(text),
        modifier = Modifier
            .padding(5.dp)
            .clickable {
                onChanged(changed)
            }
            .background(
                color = inverseOnSurfaceLight,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(5.dp),
    )
}

@Composable
private fun NumberInTextField(
    label: Int,
    value: MutableDoubleState,
    width: Int,
    multiplier: Double
) {
    var text by remember { mutableStateOf((value.doubleValue * multiplier).toString()) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(key1 = value.doubleValue, key2 = multiplier) {
        text = (value.doubleValue * multiplier).toString()
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
        onValueChange = {newText ->
            if (newText.matches(Regex("[0-9]*\\.?[0-9]*")) || newText.isEmpty()) {
                text = newText
                value.doubleValue = (newText.toDoubleOrNull() ?: 0.0) * multiplier
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
fun LiverFibrosisScoreSystemScreenPreview(){
    LiverFibrosisScoreSystemScreen(navController = NavController(LocalContext.current))
}

