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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
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

//regarding APRI:
//Diagnostic Accuracy of Noninvasive Fibrosis Models to Detect Change in Fibrosis Stage
//Siddiqui, Mohammad ShadabAllende, Daniela et al.
//Clinical Gastroenterology and Hepatology, Volume 17, Issue 9, 1877 - 1885.e5

val references = listOf(
    textAndUrl(R.string.mALBIRef, R.string.mALBIUrl),
    textAndUrl(R.string.netakiridoRefTitle, R.string.netakiridoUrl)
)
data class scores(
    val fib4score: Double,
    val apri: Double
)
@Composable
fun LiverFibrosisScoreSystemScreen(navController: NavController) {
    var allScores by remember { mutableStateOf(scores(0.0,0.0)) }
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
                allScores = inputAndCalculate()
                CalculateGradientProportion(
                    maxValue = 5F,
                    minValue = 0.1F,
                    firstThreshold = 1.3F,
                    secondThreshold = 2.67F,
                    fibrosisScore = allScores.fib4score.toFloat(),
                    firstLabel = stringResource(R.string.lowRisk),
                    secondLabel = stringResource(R.string.intermediateRisk),
                    thirdLabel = stringResource(R.string.highRisk)
                )
                Text(
                    text = "${stringResource(R.string.fib4score)} ${allScores.fib4score}",
                    modifier = Modifier
                        .padding(10.dp)

                )
                CalculateGradientProportion(
                    maxValue = 10F,
                    minValue = 0.01F,
                    firstThreshold = 1.34F,
                    secondThreshold = 0F,
                    fibrosisScore = allScores.apri.toFloat(),
                    firstLabel = stringResource(R.string.fibrosisStage02),
                    secondLabel = stringResource(R.string.fibrosisStage34),
                    thirdLabel = ""
                )
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
    fibrosisScore: Float,
    firstLabel: String,
    secondLabel: String,
    thirdLabel: String
)//: Float
{
    val mediumColorValue =
        (secondThreshold - firstThreshold) / (maxValue - minValue)
    GraphFibrosis(
        canvasWidth = 200F,
        fibrosisScore = fibrosisScore,
        mediumColorValue = mediumColorValue,
        maxValue = maxValue,
        minValue = minValue,
        firstThreshold = firstThreshold,
        secondThreshold = secondThreshold,
        firstLabel = firstLabel,
        secondLabel = secondLabel,
        thirdLabel = thirdLabel
    )
}

@Composable
fun GraphFibrosis(
    canvasWidth: Float,
    fibrosisScore: Float,
    mediumColorValue: Float,
    maxValue: Float,
    minValue: Float,
    firstThreshold: Float,
    secondThreshold: Float,
    firstLabel: String,
    secondLabel: String,
    thirdLabel: String
) {
    val canvasHeightValue = 50
    val canvasHeight = canvasHeightValue.dp
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .height(canvasHeight)//, canvasWidth.dp)
            .fillMaxWidth()
    )
    {
        drawIntoCanvas { canvas ->
            val rectColorStops = arrayOf(
                0.0f to Color(0xFF1BFF0B),
                mediumColorValue to Color(0xFFFFE30B),
                1.0f to Color(0xFFFF0180)
            )
            val rectGradient = Brush.horizontalGradient(
                //colors = rectColors,
                colorStops = rectColorStops,
                startX = size.width * (0),
                endX = size.width * (1F / 1F)
            )
            val rectCornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            val circleSize = 20F
            val circleColors = listOf(Color(0xFFFF1C07), Color(0xFFFDFDFF))
            val circleWidth =
                if (fibrosisScore > maxValue) 0F
                else if (fibrosisScore < minValue) size.width
                else  (fibrosisScore/ (maxValue-minValue)) * size.width
            val circleGradient = Brush.radialGradient(
                colors = circleColors,
                center = Offset(x = circleWidth, y = size.height / 2),
                radius = circleSize * 1.1F
            )
            drawRoundRect(
                size = Size(width = size.width, height = size.height),
                brush = rectGradient,
                cornerRadius = rectCornerRadius
            )
            drawThresholdLine(
                height = size.height,
                xPosition = ((firstThreshold/ (maxValue-minValue))) * size.width
            )
            if (secondThreshold != 0F) {
                drawThresholdLine(
                    height = size.height,
                    xPosition = ((secondThreshold / (maxValue - minValue))) * size.width
                )
            }
            drawText(
                textMeasurer = textMeasurer,
                text = firstLabel,
                topLeft = Offset(10F+0F * size.width,10F)
            )
            if (secondThreshold != 0F){
                drawText(
                    textMeasurer = textMeasurer,
                    text = secondLabel,
                    topLeft = Offset(10F+(firstThreshold/ (maxValue-minValue)) * size.width,10F)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = thirdLabel,
                    topLeft = Offset(10F+(secondThreshold/ (maxValue-minValue)) * size.width,10F)
                )
            } else {
                drawText(
                    textMeasurer = textMeasurer,
                    text = secondLabel,
                    topLeft = Offset(10F+(firstThreshold/ (maxValue-minValue)) * size.width,10F)
                )
            }
            drawCircle(
                brush = circleGradient,
                radius = circleSize,
                center = Offset(x = circleWidth, y = size.height/2),
            )
        }
    }
}

fun DrawScope.drawThresholdLine(
    height: Float,
    xPosition: Float
) {
    drawLine(
        color = Color.Black,
        start = Offset(x = xPosition, y = 0F),
        end = Offset(x = xPosition, y = height),
        strokeWidth = 3F
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun inputAndCalculate(): scores {
    val age = remember { mutableDoubleStateOf(40.0) }
    val ast = remember { mutableDoubleStateOf(35.0) }
    val platelet = remember { mutableDoubleStateOf(150.0) }
    val alt = remember { mutableDoubleStateOf(30.0) }
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
                .wrapContentHeight(
                    align = Alignment.Bottom
                ),
        ) {
            InputValue(
                label = R.string.age, value = age, width = 100,
                unit = R.string.years, changeUnit = changedFactor1Unit, changedValueRate = 1.0
            )
            InputValue(
                label = R.string.ast, value = ast, width = 100,
                unit = R.string.iul, changeUnit = changedFactor2Unit, changedValueRate = 1.0
            )
            InputValue(
                label = R.string.plateletCount, value = platelet, width = 100,
                unit = R.string.unit109L, changeUnit = changedFactor3Unit, changedValueRate = 1.0
            )
            InputValue(
                label = R.string.alt, value = alt, width = 100,
                unit = R.string.iul, changeUnit = changedFactor4Unit, changedValueRate = 1.0
            )
        }
    }

    val fib4score = (age.doubleValue * ast.doubleValue) /
            (platelet.doubleValue * sqrt(alt.doubleValue))

    val apri = ast.doubleValue / 30 / platelet.doubleValue

    val allScores = scores(fib4score, apri)

    return allScores
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

