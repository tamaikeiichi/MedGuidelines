package com.example.medguidelines.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.medguidelines.R
import com.example.medguidelines.data.indexNames

@Composable
fun listItem (name: String, onClick: () -> Unit){
    Text(text = name,
        fontSize = 30.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}