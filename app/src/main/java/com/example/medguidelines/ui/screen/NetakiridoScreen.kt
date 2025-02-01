package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.TextAndExpand
import com.example.medguidelines.ui.component.TextAndExpandTest
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
//            TextAndExpand(
//                R.string.ageTitle,
//                R.string.ptTitle,
//                R.string.dehydrationTitle
//            )
            TextAndExpandTest(
                firstTitle = R.string.ageTitle,
                secondTitle = R.string.ptTitle,
                nextCard = {
                    TextAndExpandTest(
                        firstTitle = R.string.ptTitle,
                        secondTitle = R.string.albuminTitle,
                        nextCard = {
                            TextAndExpandTest(
                                firstTitle = R.string.albuminTitle,
                                secondTitle = R.string.bloodGasAnalysisTitle,
                                nextCard = {
                                    TextAndExpandTest(
                                        firstTitle = R.string.bloodGasAnalysisTitle,
                                        secondTitle = R.string.space,
                                    )
                                }
                            )
                        },
                    )
                },
            )
        }
    }
}

