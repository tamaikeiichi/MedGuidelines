package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.RadioButtonAndExpand
import com.example.medguidelines.ui.component.TextAndExpand
import com.example.medguidelines.ui.component.TitleTopAppBar


@Composable
fun NetakiridoScreen(navController: NavController) {
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = stringResource(id = R.string.netakiridoTitle),
                navController = navController,
                referenceText = R.string.space,
                referenceUrl = R.string.space
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(innerPadding)

        ) {
            TextAndExpand(
                R.string.ageTitle,
                R.string.ptTitle,
                R.string.dehydrationTitle
            )
        }
            }
        }

