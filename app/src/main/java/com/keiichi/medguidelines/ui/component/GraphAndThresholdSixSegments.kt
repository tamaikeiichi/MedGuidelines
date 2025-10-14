package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.MaterialTheme
import com.keiichi.medguidelines.R

@Composable
fun GraphAndThresholdSixSegments(
    maxValue: Float,
    minValue: Float,
    firstThreshold: Float,
    secondThreshold: Float = 0F,
    thirdThreshold: Float = 0F,
    fourthThreshold: Float = 0F,
    fifthThreshold: Float = 0F,
    sixthThreshold: Float = 0F,
    firstLabel: String,
    secondLabel: String = "",
    thirdLabel: String = "",
    thirdLabelInDetail: String = "",
    fourthLabel: String = "",
    fifthLabel: String = "",
    sixthLabel: String = "",
    seventhLabel: String = "",
    score: Double,
    displayAsInt: Boolean = false,
    invertColors: Boolean = false
) {
    val mediumColorValue =
        (((secondThreshold + firstThreshold) / 2) - minValue) / (maxValue - minValue)
    val canvasHeightValue = 50
    val canvasHeight = canvasHeightValue.dp
    val textMeasurer = rememberTextMeasurer()
    var offsetXOfThirdLabel: Float = 0F
    var offsetYOfThirdLabel: Float = 0F
    var heightOfThirdLabel: Float = 0F
    var widthOfThirdLabel: Float = 0F

    var thirdLabelTapped by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val helpImageWidth = 45
    val helpImageHeight = 45
    val imageBitmap: ImageBitmap? =
        ContextCompat.getDrawable(context, R.drawable.baseline_help_24)
            ?.toBitmap(width = helpImageWidth, height = helpImageHeight)?.asImageBitmap()

    val isDarkTheme = isSystemInDarkTheme() // Call it once

    // Declare variables before the if-else
    val greenColor: Color
    val g2y1Color: Color
    val g1y2color: Color
    val yellowColor: Color
    val y2r1Color: Color
    val y1r2Color: Color
    val redColor: Color

    if (isDarkTheme) {
        greenColor = Color(0xFF109A07)
        yellowColor = Color(0xFF968607)
        redColor = Color(0xFF8B0045)
        g2y1Color = lerp(greenColor, yellowColor, 0.33F)
        g1y2color = lerp(greenColor, yellowColor, 0.66F)
        y2r1Color = lerp(yellowColor, redColor, 0.33F)
        y1r2Color = lerp(yellowColor, redColor, 0.66F)

    } else {
        greenColor = Color(0xFF1BFF0B)
        yellowColor = Color(0xFFFFE30B)
        redColor = Color(0xFFFF0180)
        g2y1Color = lerp(greenColor, yellowColor, 0.33F)
        g1y2color = lerp(greenColor, yellowColor, 0.66F)
        y2r1Color = lerp(yellowColor, redColor, 0.33F)
        y1r2Color = lerp(yellowColor, redColor, 0.66F)
    }

    // Conditionally assign the final colors based on the invertColors flag
    val finalGreenColor = if (invertColors) redColor else greenColor
    val finalG2y1Color = if (invertColors) y1r2Color else g2y1Color
    val finalG1y2color = if (invertColors) y2r1Color else g1y2color
    val finalYellowColor = yellowColor // Yellow stays in the middle
    val finalY2r1Color = if (invertColors) g1y2color else y2r1Color
    val finalY1r2Color = if (invertColors) g2y1Color else y1r2Color
    val finalRedColor = if (invertColors) greenColor else redColor

    val labelTextStyle = TextStyle(
        color = MaterialTheme.colorScheme.tertiary
    )

    Canvas(
        modifier = Modifier
            .height(canvasHeight)
            .fillMaxWidth()
            .tapOrPress(
                onStart = { offsetX, offsetY ->
                    Unit
                },
                onCancel = { offsetX, offsetY ->
                    Unit
                },
                onCompleted = { offsetX, offsetY ->
                    val isInsideXRegion =
                        offsetX > offsetXOfThirdLabel
                                && offsetX < offsetXOfThirdLabel + widthOfThirdLabel + helpImageWidth
                    val isInsideYRegion =
                        offsetY > offsetYOfThirdLabel
                                && offsetY < offsetYOfThirdLabel + heightOfThirdLabel + helpImageHeight
                    if (isInsideXRegion && isInsideYRegion) {
                        thirdLabelTapped = !thirdLabelTapped
                    }
                }
            )
    )
    {
        drawIntoCanvas {
            val rectColorStops =
                    arrayOf(
                        0.0f to finalGreenColor,
                        ((firstThreshold - minValue) / (maxValue - minValue)) to finalG2y1Color,
                        ((secondThreshold - minValue) / (maxValue - minValue)) to finalG1y2color,
                        ((thirdThreshold - minValue) / (maxValue - minValue)) to finalYellowColor,
                        ((fourthThreshold - minValue) / (maxValue - minValue)) to finalY2r1Color,
                        ((fifthThreshold - minValue) / (maxValue - minValue)) to finalY1r2Color,
                        1.0f to finalRedColor
                    )
            val rectGradient = Brush.horizontalGradient(
                colorStops = rectColorStops,
                startX = size.width * (0),
                endX = size.width * (1F / 1F)
            )
            val rectCornerRadius = CornerRadius(0.dp.toPx(), 10.dp.toPx())
            val circleSize = 20F
            val circleColors = listOf(Color(0xFFFF1C07), Color(0xFFFDFDFF))
            val circleXOffset =
                if (score > maxValue) size.width
                else if (score < minValue) 0F
                else ((score.toFloat() - minValue) / (maxValue - minValue)) * size.width
            val circleYOffset = size.height * 0.75F
            val circleGradient = Brush.radialGradient(
                colors = circleColors,
                center = Offset(x = circleXOffset, y = circleYOffset),
                radius = circleSize * 1.1F
            )
            drawRoundRect(
                size = Size(width = size.width, height = size.height / 2),
                brush = rectGradient,
                topLeft = Offset(x = 0F, y = size.height / 2),
                cornerRadius = rectCornerRadius
            )

            drawText(
                textMeasurer = textMeasurer,
                text = firstLabel,
                style = labelTextStyle,
                topLeft = Offset(10F + 0F * size.width, 10F)
            )
            drawThresholdWithLabel(
                thresholdValue = firstThreshold,
                minValue = minValue,
                maxValue = maxValue,
                label = secondLabel,
                textMeasurer = textMeasurer,
                labelTextStyle = labelTextStyle
            )
            if (secondThreshold != 0F) {
                drawThresholdWithLabel(
                    thresholdValue = secondThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    label = thirdLabel,
                    textMeasurer = textMeasurer,
                    labelTextStyle = labelTextStyle
                )
            }
            if (thirdThreshold != 0F) {
                drawThresholdWithLabel(
                    thresholdValue = thirdThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    label = fourthLabel,
                    textMeasurer = textMeasurer,
                    labelTextStyle = labelTextStyle
                )
            }
            if (fourthThreshold != 0F) {
                drawThresholdWithLabel(
                    thresholdValue = fourthThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    label = fifthLabel,
                    textMeasurer = textMeasurer,
                    labelTextStyle = labelTextStyle
                )
            }
            if (fifthThreshold != 0F) {
                drawThresholdWithLabel(
                    thresholdValue = fifthThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    label = sixthLabel,
                    textMeasurer = textMeasurer,
                    labelTextStyle = labelTextStyle
                )
            }
            if (sixthThreshold != 0F) {
                drawThresholdWithLabel(
                    thresholdValue = sixthThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    label = seventhLabel,
                    textMeasurer = textMeasurer,
                    labelTextStyle = labelTextStyle
                )
            }
            drawScaleValue(
                threshold = firstThreshold,
                minValue = minValue,
                maxValue = maxValue,
                textMeasurer = textMeasurer,
                displayAsInt = displayAsInt
            )
            if (secondThreshold != 0F){
                drawScaleValue(
                    threshold = secondThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    textMeasurer = textMeasurer,
                    displayAsInt = displayAsInt
                )
            }
            if (thirdThreshold != 0F){
                drawScaleValue(
                    threshold = thirdThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    textMeasurer = textMeasurer,
                    displayAsInt = displayAsInt
                )
            }
            if (fourthThreshold != 0F){
                drawScaleValue(
                    threshold = fourthThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    textMeasurer = textMeasurer,
                    displayAsInt = displayAsInt
                )
            }
            if (fifthThreshold != 0F){
                drawScaleValue(
                    threshold = fifthThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    textMeasurer = textMeasurer,
                    displayAsInt = displayAsInt
                )
            }
            if (sixthThreshold != 0F){
                drawScaleValue(
                    threshold = sixthThreshold,
                    minValue = minValue,
                    maxValue = maxValue,
                    textMeasurer = textMeasurer,
                    displayAsInt = displayAsInt
                )
            }
            val alphaCircle =
                if (score == 0.0) 0.2F
                else 1F
            drawCircle(
                brush = circleGradient,
                radius = circleSize,
                center = Offset(x = circleXOffset, y = circleYOffset),
                alpha = alphaCircle
            )
            val textBackgroundColor = Color(0xFFFFF8E6)
            val textBackgroundCornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
            val textSize = textMeasurer.measure(text = score.toString())
            if (circleXOffset <= size.width / 2) {
                drawRoundRect(
                    color = textBackgroundColor,
                    size = Size(
                        width = textSize.size.width.toFloat() + 10F,
                        height = textSize.size.height.toFloat() + 5F
                    ),
                    //brush = circleGradient,
                    topLeft = Offset(
                        x = circleXOffset + circleSize * 1.5F
                                //- textMeasurer.measure(text = score.toString()).size.width
                                - 5F,
                        y = circleYOffset - textSize.size.height / 2 - 2.5F
                    ),
                    cornerRadius = textBackgroundCornerRadius
                )
            } else {
                drawRoundRect(
                    color = textBackgroundColor,
                    size = Size(
                        width = textSize.size.width.toFloat() + 10F,
                        height = textSize.size.height.toFloat() + 5F
                    ),
                    topLeft = Offset(
                        x = circleXOffset - circleSize * 1.5F
                                - textSize.size.width - 5F,
                        y = circleYOffset - textSize.size.height / 2 - 2.5F
                    ),
                    cornerRadius = textBackgroundCornerRadius
                )
            }
            val scoreStringAlpha =
                if (score == 0.0) 0.3F
                else 1F
            if (circleXOffset <= size.width / 2) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = score.toString(),
                    topLeft = Offset(
                        x = circleXOffset + circleSize * 1.5F,
                        y = circleYOffset - textMeasurer.measure(text = score.toString()).size.height / 2

                    ),
                    style = TextStyle(
                        color = Color(red = 0f, green = 0f, blue = 0f, alpha = scoreStringAlpha),
                    )
                )
            } else {
                drawText(
                    textMeasurer = textMeasurer,
                    text = score.toString(),
                    topLeft = Offset(
                        x = circleXOffset - circleSize * 1.5F
                                - textMeasurer.measure(text = score.toString()).size.width,
                        y = circleYOffset - textMeasurer.measure(text = score.toString()).size.height / 2
                    ),
                    style = TextStyle(
                        color = Color(red = 0f, green = 0f, blue = 0f, alpha = scoreStringAlpha),
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawThresholdWithLabel(thresholdValue: Float,
                                             minValue: Float,
                                             maxValue: Float,
                                             label: String,
                                             textMeasurer: androidx.compose.ui.text.TextMeasurer,
                                             labelTextStyle: androidx.compose.ui.text.TextStyle
) {
    // Calculate the x-position based on the threshold's percentage of the total range
    val xPosition = ((thresholdValue - minValue) / (maxValue - minValue)) * size.width

    // Draw the vertical threshold line
    drawThresholdLine(
        height = size.height / 2,
        xPosition = xPosition
    )

    // Draw the text label slightly to the right of the line
    drawText(
        textMeasurer = textMeasurer,
        text = label,
        style = labelTextStyle,
        topLeft = Offset(x = xPosition + 10F, y = 10F)
    )
}

private fun DrawScope.drawScaleValue(
    threshold: Float,
    minValue: Float,
    maxValue: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    displayAsInt: Boolean = false
) {
    val textToDisplay = if (displayAsInt) {
        threshold.toInt().toString()
    } else {
        threshold.toString()
    }

    rotate(
        degrees = -90F,
        pivot = Offset(
            x = (((threshold - minValue)
                    / (maxValue - minValue)) * size.width), y = (size.height / 2)
        )
    ) {
        drawText(
            textMeasurer = textMeasurer,
            text = textToDisplay,
            topLeft = Offset(
                x = (((threshold - minValue) / (maxValue - minValue)) * size.width)
                        + (textMeasurer.measure(text = threshold.toString()).size.height) / 4,
                y = (size.height / 2)
                        - (textMeasurer.measure(text = threshold.toString()).size.height / 2)
            ),
            style = TextStyle(fontSize = 10.sp, color = Color.Gray)
        )
    }
}


@Preview
@Composable
fun GraphAndThresholdSixSegmentsPreview(){
    GraphAndThresholdSixSegments(
        maxValue = 100F,
        minValue = 0F,
        firstThreshold = 15F,
        secondThreshold = 30F,
        thirdThreshold = 45F,
        fourthThreshold = 60F,
        fifthThreshold = 90F,
        firstLabel = "G5",
        secondLabel = "G4",
        thirdLabel = "G3b",
        fourthLabel = "G3a",
        fifthLabel = "G2",
        sixthLabel = "G1",
        score = 80.0,
        displayAsInt = true,
        invertColors = true
    )
}
