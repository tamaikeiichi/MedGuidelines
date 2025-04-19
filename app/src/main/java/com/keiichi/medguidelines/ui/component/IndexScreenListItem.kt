package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IndexScreenListItem (name: Int, onClick: () -> Unit){
    Column {
        Card {
            Text(text = parseStyledString(name),
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable { onClick() }
            )
        }
        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}
