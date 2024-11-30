package com.example.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.medguidelines.R

@Composable
fun AdropScreen() {
    Text(text = stringResource(id = R.string.aDropTitle),
        fontSize = 30.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  }
    )
}