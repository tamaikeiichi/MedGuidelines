package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ThinRadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp, // The overall size of the radio button
    strokeWidth: Dp = 1.dp, // The thickness of the outer circle
    dotRadiusRatio: Float = 1.0f, // Ratio of the dot's radius to the outer circle's radius
    isNumberDisplayed: Boolean
) {
    val radioColor: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Canvas(modifier = modifier.size(size)) {
        val radius = (size.toPx() / 2) //- (strokeWidth.toPx() / 1)

        // Draw the outer circle (the border)
        drawCircle(
            color = radioColor,
            radius = radius - (strokeWidth.toPx() / 2),
            style = Stroke(width = strokeWidth.toPx())
        )

        // Draw the inner dot if selected
        if (selected) {
            drawCircle(
                color = radioColor,
                radius = if(isNumberDisplayed){
                    radius * dotRadiusRatio
                }else{
                    radius * (dotRadiusRatio - 0.3f)
                }
            )
        }
    }
}