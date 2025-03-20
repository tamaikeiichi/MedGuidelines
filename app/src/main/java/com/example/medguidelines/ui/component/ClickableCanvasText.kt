package com.example.medguidelines.ui.component


import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope



@Composable
fun Modifier.tapOrPress(
    onStart: (offsetX: Float) -> Unit,
    onCancel: (offsetX: Float) -> Unit,
    onCompleted: (offsetX: Float) -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.pointerInput(interactionSource) {
        awaitEachGesture {
            val tap =
                awaitFirstDown().also { if (it.pressed != it.previousPressed) it.consume() }
            onStart(tap.position.x)
            val up = waitForUpOrCancellation()
            if (up == null) {
                onCancel(tap.position.x)
            } else {
                if (up.pressed != up.previousPressed) up.consume()
                onCompleted(tap.position.x)
            }
        }
    }
}