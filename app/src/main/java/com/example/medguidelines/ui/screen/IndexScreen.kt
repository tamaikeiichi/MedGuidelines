package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.medguidelines.R
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource

@Composable
fun IndexScreen(){
    LazyColumn {
        item {
            Text(text = stringResource(id = R.string.index1))
        }

            }
        }

