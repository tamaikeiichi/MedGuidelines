package com.example.medguidelines.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medguidelines.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextAndExpand(
    firstTitle: Int,
    secondTitle: Int,
    thirdTitle: Int,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        var expandedFirst by remember { mutableStateOf(false) }
        var expandedSecond by remember { mutableStateOf(false) }
        var expandedThird by remember { mutableStateOf(false) }
        val cardModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .then(
                if (secondTitle != R.string.space) {
                    Modifier.clickable { expandedFirst = !expandedFirst }
                } else {
                    Modifier
                }
            )
        Card(
            modifier = cardModifier
        ) {
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .animateContentSize()
            ) {
                Column(){
                    TextInCard(firstTitle)
                    if (expandedFirst) {
                        Card(){
                            Row(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .animateContentSize()
                            ) {
                                Column(){
                                    TextInCard(secondTitle)
                                    if (expandedSecond) {
                                        Card(){
                                            Row(
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .animateContentSize()
                                            ) {
                                                TextInCard(thirdTitle)
                                            }
                                        }

                                    }
                                }
                                if (thirdTitle != R.string.space) {
                                    IconButtonInCard(
                                        expanded = expandedSecond,
                                        onExpandChange = { expandedSecond = it }
                                    )
                                }
                            }
                        }



                    }
                }
                if (secondTitle != R.string.space) {
                    IconButtonInCard(
                        expanded = expandedFirst,
                        onExpandChange = { expandedFirst = it }
                    )
                }
                }


            }


            }
        }





@Composable
fun TextInCard(
    text: Int
) {
    Text(
        text = parseStyledString(text),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(4.dp)
    )
}

@Composable
fun IconButtonInCard(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
){
    IconButton(onClick = { onExpandChange(!expanded) }) {
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
            else Icons.Filled.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand"
        )
    }
}

