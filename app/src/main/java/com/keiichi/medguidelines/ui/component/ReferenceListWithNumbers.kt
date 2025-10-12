package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R

@Composable
fun ReferenceListWithNumbers(references: List<TextAndUrl>) {
    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ){
        if (references.isEmpty() || (references.size == 1 && references[0].text == R.string.space && references[0].url == R.string.space)) {
            // Handle empty or placeholder references if needed, e.g., show a message
            Text("No references available.")
        } else {
            references.forEachIndexed { index, reference ->
                // Filter out placeholder "space" references if they shouldn't be numbered or displayed
                if (reference.text != R.string.space || reference.url != R.string.space) {
                    Row(verticalAlignment = Alignment.Top) { // Align number and text to the top
                        Text(text = "${index + 1}) ") // Add 1 to index because it's 0-based
                        UrlLinkText(reference)
                    }
                }
            }
        }
    }
}