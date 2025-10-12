package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.MaterialTheme
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.PopupClickable
import com.keiichi.medguidelines.ui.component.drawThresholdLine

@Composable
fun GraphAndThreshold(
    maxValue: Float,
    minValue: Float,
    firstThreshold: Float,
    secondThreshold: Float = 0F,
    thirdThreshold: Float = 0F,
    firstLabel: String,
    secondLabel: String = "",
    thirdLabel: String = "",
    thirdLabelInDetail: String = "",
    fourthLabel: String = "",
    score: Double
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
    val yellowColor: Color
    val redColor: Color

    if (isDarkTheme) {
        greenColor = Color(0xFF109A07)
        yellowColor = Color(0xFF968607)
        redColor = Color(0xFF8B0045)
    } else {
        greenColor = Color(0xFF1BFF0B)
        yellowColor = Color(0xFFFFE30B)
        redColor = Color(0xFFFF0180)
    }

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
                if (secondThreshold == 0F) {
                    arrayOf(
                        0.0f to greenColor,
                        (firstThreshold - minValue) / (maxValue - minValue) to yellowColor,
                        1.0f to redColor
                    )
                } else {
                    arrayOf(
                        0.0f to greenColor,
                        mediumColorValue to yellowColor,
                        1.0f to redColor
                    )
                }
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
            drawThresholdLine(
                height = size.height / 2,
                xPosition = (((firstThreshold - minValue) / (maxValue - minValue))) * size.width
            )
            drawText(
                textMeasurer = textMeasurer,
                text = secondLabel,
                style = labelTextStyle,
                topLeft = Offset(
                    10F + ((firstThreshold - minValue) / (maxValue - minValue)) * size.width,
                    10F
                )
            )
            if (secondThreshold != 0F) {
                drawThresholdLine(
                    height = size.height / 2,
                    xPosition = (((secondThreshold - minValue) / (maxValue - minValue))) * size.width
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = thirdLabel,
                    style = labelTextStyle,
                    topLeft = Offset(
                        10F + ((secondThreshold - minValue) / (maxValue - minValue)) * size.width,
                        10F
                    )
                )
                offsetXOfThirdLabel =
                    10F + ((secondThreshold - minValue) / (maxValue - minValue)) * size.width
                offsetYOfThirdLabel = 10F
                heightOfThirdLabel =
                    textMeasurer.measure(text = thirdLabel.toString()).size.height.toFloat()
                widthOfThirdLabel =
                    textMeasurer.measure(text = thirdLabel.toString()).size.width.toFloat()

                if (thirdLabelInDetail != "") {
                    if (imageBitmap != null) {
                        drawImage(
                            image = imageBitmap,
                            topLeft = Offset(
                                x = offsetXOfThirdLabel + widthOfThirdLabel,
                                y = offsetYOfThirdLabel
                            ),
                            colorFilter = ColorFilter.tint(Color(0xFF885200))
                        )
                    }
                }
            }
            if (thirdThreshold != 0F) {
                drawThresholdLine(
                    height = size.height / 2,
                    xPosition = (((thirdThreshold - minValue) / (maxValue - minValue))) * size.width
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = fourthLabel,
                    topLeft = Offset(
                        10F + ((thirdThreshold - minValue) / (maxValue - minValue)) * size.width,
                        10F
                    )
                )
            }
            if (thirdLabelInDetail != "") {
                drawText(
                    textMeasurer = textMeasurer,
                    text = fourthLabel,
                    topLeft = Offset(
                        10F + ((thirdThreshold - minValue) / (maxValue - minValue)) * size.width,
                        10F
                    )
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
            rotate(
                degrees = -90F,
                pivot = Offset(
                    x = (((firstThreshold - minValue)
                            / (maxValue - minValue)) * size.width), y = (size.height / 2)
                )
            ) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = firstThreshold.toString(),
                    topLeft = Offset(
                        x = (((firstThreshold - minValue) / (maxValue - minValue)) * size.width)
                                + (textMeasurer.measure(text = firstThreshold.toString()).size.height) / 4,
                        y = (size.height / 2)
                                - (textMeasurer.measure(text = firstThreshold.toString()).size.height / 2)
                    ),
                    style = TextStyle(fontSize = 10.sp, color = Color.Gray)
                )
            }
            if (secondThreshold != 0F){
                rotate(
                    degrees = -90F,
                    pivot = Offset(
                        x = (((secondThreshold - minValue)
                                / (maxValue - minValue)) * size.width), y = (size.height / 2)
                    )
                ) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = secondThreshold.toString(),
                        topLeft = Offset(
                            x = (((secondThreshold - minValue) / (maxValue - minValue)) * size.width)
                                    + (textMeasurer.measure(text = secondThreshold.toString()).size.height) / 4,
                            y = (size.height / 2)
                                    - (textMeasurer.measure(text = secondThreshold.toString()).size.height / 2)
                        ),
                        style = TextStyle(fontSize = 10.sp, color = Color.Gray)
                    )
                }
            }
            if (thirdThreshold != 0F){
                rotate(
                    degrees = -90F,
                    pivot = Offset(
                        x = (((thirdThreshold - minValue)
                                / (maxValue - minValue)) * size.width), y = (size.height / 2)
                    )
                ) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = thirdThreshold.toString(),
                        topLeft = Offset(
                            x = (((thirdThreshold - minValue) / (maxValue - minValue)) * size.width)
                                    + (textMeasurer.measure(text = thirdThreshold.toString()).size.height) / 4,
                            y = (size.height / 2)
                                    - (textMeasurer.measure(text = thirdThreshold.toString()).size.height / 2)
                        ),
                        style = TextStyle(fontSize = 10.sp, color = Color.Gray)
                    )
                }
            }

        }
    }
    if (thirdLabelTapped) {
        Popup(
        ) {
            PopupClickable(
                text = thirdLabelInDetail,
                onClick = { thirdLabelTapped = !thirdLabelTapped })
        }
    }
}