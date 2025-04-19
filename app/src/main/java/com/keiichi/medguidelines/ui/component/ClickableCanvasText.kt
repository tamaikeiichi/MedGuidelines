package com.keiichi.medguidelines.ui.component


import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.tapOrPress(
    onStart: (offsetX: Float, offsetY: Float) -> Unit,
    onCancel: (offsetX: Float, offsetY: Float) -> Unit,
    onCompleted: (offsetX: Float, offsetY: Float) -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.pointerInput(interactionSource) {
        awaitEachGesture {
            val down = awaitFirstDown()
            onStart(down.position.x, down.position.y)
            val up = waitForUpOrCancellation()
            if (up == null) {
                onCancel(down.position.x, down.position.y)
            } else {
                onCompleted(up.position.x, up.position.y)
                //                        val isInsideXRegion =
//                            selectedPositionX > offsetXOfThirdLabel && selectedPositionX < offsetXOfThirdLabel + widthOfThirdLabel
//                        val isInsideYRegion =
//                            selectedPositionY > offsetYOfThirdLabel && selectedPositionY < offsetYOfThirdLabel + heightOfThirdLabel
//                        if (isInsideXRegion && isInsideYRegion) {
//                            thirdLabelTapped = !thirdLabelTapped
            }
        }
    }
}

//@Composable
//fun Modifier.tapOrPress(
//    onStart: (offsetX: Float) -> Unit,
//    onCancel: (offsetX: Float) -> Unit,
//    onCompleted: (offsetX: Float) -> Unit
//): Modifier {
//    val interactionSource = remember { MutableInteractionSource() }
//    return this.pointerInput(interactionSource) {
//        awaitEachGesture {
//            val tap =
//                awaitFirstDown().also { if (it.pressed != it.previousPressed) it.consume() }
//            onStart(tap.position.x)
//            val up = waitForUpOrCancellation()
//            if (up == null) {
//                onCancel(tap.position.x)
//            } else {
//                if (up.pressed != up.previousPressed) up.consume()
//                onCompleted(tap.position.x)
//            }
//        }
//    }
//}