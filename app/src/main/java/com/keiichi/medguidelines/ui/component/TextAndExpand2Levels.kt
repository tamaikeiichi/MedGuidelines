package com.keiichi.medguidelines.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.R

@Composable
fun textAndExpand1Level(
    firstTitle: Int,
    cardColor: Color = MaterialTheme.colorScheme.onSecondary,
    textColor: Color = MaterialTheme.colorScheme.secondary
): Boolean {
    var expanded by remember { mutableStateOf(false) }
    Column() {
        val cardModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .width(IntrinsicSize.Max)
            .clickable { expanded = !expanded }
        Card(
            modifier = cardModifier,
            colors = CardDefaults.cardColors(
                containerColor = cardColor // Set the card color here
            )

        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .animateContentSize(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    TextInCardTitleMedium(
                        text = firstTitle,
                        color = textColor
                    )
                }
                IconButtonInCard(
                    expanded = expanded,
                    onExpandChange = { expanded = it }
                )
            }
        }
    }
    return expanded
}

@Composable
fun Text1Level(
    firstTitle: Int,
    firstTextColor: Color = MaterialTheme.colorScheme.secondary,
    firstCardColor: Color = MaterialTheme.colorScheme.onSecondary,
) {
    Column() {
        val cardModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .width(IntrinsicSize.Max)
        Card(
            modifier = cardModifier,
            colors = CardDefaults.cardColors(
                containerColor = firstCardColor
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .animateContentSize(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    TextInCardTitleMedium(
                        text = firstTitle,
                        color = firstTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun TextAndExpand2Levels(
    firstTitle: Int,
    secondTitle: Int,
    firstTextColor: Color = MaterialTheme.colorScheme.secondary,
    secondTextColor: Color = MaterialTheme.colorScheme.tertiary,
    firstCardColor: Color = MaterialTheme.colorScheme.onSecondary,
    //secondCardColor: Color,
) {
    Column() {
        var expanded by remember { mutableStateOf(false) }
        val cardModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .width(IntrinsicSize.Max)
            .then(
                if (secondTitle != R.string.space) {
                    Modifier.clickable { expanded = !expanded }
                } else {
                    Modifier
                        .padding(4.dp)
                        .animateContentSize()
                }
            )
        Card(
            modifier = cardModifier,
            colors = CardDefaults.cardColors(
                containerColor = firstCardColor // Set the card color here
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .animateContentSize(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    TextInCardTitleMedium(
                        text = firstTitle,
                        color = firstTextColor
                    )
                }
                if (secondTitle != R.string.space) {
                    IconButtonInCard(
                        expanded = expanded,
                        onExpandChange = { expanded = it }
                    )
                }
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .padding(8.dp),

                    ) {
                    TextInCardTitleMedium(
                        text = secondTitle,
                        color = secondTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun TextAndExpand3Levels(
    firstTitle: Int,
    secondTitle1: Int,
    secondTitle2: Int,
    thirdTitle11: Int,
    thirdTitle21: Int,
    firstColor: Color,
    secondColor: Color,
    thirdColor: Color
) {
    Column() {
        var expanded1 by remember { mutableStateOf(false) }
        var expanded2 by remember { mutableStateOf(false) }
        var expanded3 by remember { mutableStateOf(false) }
        val cardModifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .fillMaxWidth()
            .width(IntrinsicSize.Max)
            .clickable { expanded1 = !expanded1 }
        val rowModifier = Modifier
            .padding(2.dp)
            .animateContentSize()
        Card(
            modifier = cardModifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = rowModifier
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        TextInCardTitleMedium(
                            text = firstTitle,
                            color = firstColor
                        )
                    }
                    IconButtonInCard(
                        expanded = expanded1,
                        onExpandChange = { expanded1 = it }
                    )
                }
                Row(
                    modifier = rowModifier
                ) {
                    if (expanded1) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextInCardTitleMedium(
                                text = secondTitle1,
                                color = secondColor
                            )
                        }
                        IconButtonInCard(
                            expanded = expanded2,
                            onExpandChange = { expanded2 = it }
                        )
                    }
                }
                Row(
                    modifier = rowModifier
                ) {
                    if (expanded2) {
                        TextInCardTitleMedium(
                            text = thirdTitle11,
                            color = thirdColor
                        )
                    }
                }
                Row(
                    modifier = rowModifier
                ) {
                    if (expanded1) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextInCardTitleMedium(
                                text = secondTitle2,
                                color = secondColor
                            )
                        }
                        IconButtonInCard(
                            expanded = expanded3,
                            onExpandChange = { expanded3 = it }
                        )
                    }
                }
                Row(
                    modifier = rowModifier
                )
                {
                    if (expanded3) {
                        TextInCardTitleMedium(
                            text = thirdTitle21,
                            color = thirdColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextInCardTitleMedium(
    text: Int,
    color: Color,
) {
    Text(
        text = parseStyledString(text),
        style = MaterialTheme.typography.titleMedium,
        color = color,
        modifier = Modifier
            .padding(4.dp)
    )
}

@Composable
fun IconButtonInCard(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    IconButton(onClick = { onExpandChange(!expanded) }) {
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
            else Icons.Filled.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand"
        )
    }
}

