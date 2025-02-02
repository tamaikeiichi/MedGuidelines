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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.textAndExpand1Levels
import com.example.medguidelines.ui.component.TextAndExpand2Levels
import com.example.medguidelines.ui.component.TitleTopAppBar


@Composable
fun NetakiridoScreen(navController: NavController) {
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.netakiridoTitle,
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
            TextAndExpand2Levels(
                R.string.rankJ,
                R.string.rankJNote,
            )
            TextAndExpand2Levels(
                R.string.rankA,
                R.string.rankANote
            )
            Column(
//                modifier = Modifier
//                    .padding(8.dp)
            ){
                val expanded = textAndExpand1Levels(
                    firstTitle = R.string.rankBorC
                )
                if (expanded) {
                    Column(

                    )
                    {
                        TextAndExpand2Levels(
                            firstTitle = R.string.rankBNote,
                            secondTitle = R.string.rankBNoteSubCategory
                        )
                        TextAndExpand2Levels(
                            firstTitle = R.string.rankCNote,
                            secondTitle = R.string.rankCNoteSubCategory
                        )
                    }
                }
            }
//            TextAndExpand3Levels(
//                firstTitle = R.string.netakiri,
//                secondTitle1 = R.string.rankBNote,
//                secondTitle2 = R.string.rankCNote,
//                thirdTitle11 = R.string.rankBNoteSubCategory,
//                thirdTitle21 = R.string.rankCNoteSubCategory
//            )
        }
    }
}

