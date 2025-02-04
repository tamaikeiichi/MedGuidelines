package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.NumberInTextField
import com.example.medguidelines.ui.component.TitleTopAppBar

@Composable
fun MALBIScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.mALBITitle,
                navController = navController,
                referenceText = R.string.mALBIRef,
                referenceUrl = R.string.mALBIUrl
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                MALBIInput()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MALBIInput() {
    val totalBilirubin = remember { mutableDoubleStateOf(1.0) }
    val albumin = remember { mutableDoubleStateOf(4.0) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
        ) {
            NumberInTextField(
                label = R.string.totalBilirubin, value = totalBilirubin, width = 100,
            )
            NumberInTextField(
                label = R.string.albumin, value = albumin, width = 110,
            )

        }
    }
}
