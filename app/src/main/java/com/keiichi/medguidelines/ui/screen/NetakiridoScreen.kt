package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.keiichi.compose.secondaryContainerLight
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.textAndExpand1Level
import com.keiichi.medguidelines.ui.component.TextAndExpand2Levels
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.Text1Level
import com.keiichi.medguidelines.ui.component.textAndUrl


@Composable
fun NetakiridoScreen(navController: NavController) {
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.netakiridoTitle,
                navController = navController,
                references = listOf(
                    textAndUrl(R.string.netakiridoRefTitle, R.string.netakiridoUrl)
                )
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            val expandedShougai = textAndExpand1Level(
                firstTitle = R.string.shougaiKoureisha,
                cardColor = secondaryContainerLight
            )
            if(expandedShougai){
                Column(){
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
                        val expanded = textAndExpand1Level(
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
                }
            }
            val expandedNinchi = textAndExpand1Level(
                firstTitle = R.string.ninchishoKoureisha,
                cardColor = secondaryContainerLight
            )
            if(expandedNinchi){
                Column {
                    Text1Level(R.string.ninchiI)
                    TextAndExpand2Levels(
                        firstTitle = R.string.ninchiII,
                        secondTitle = R.string.ninchiIINote)
                    TextAndExpand2Levels(
                        firstTitle = R.string.ninchiIII,
                        secondTitle = R.string.ninchiIIINote
                    )
                    Text1Level(R.string.ninchiIV)
                    Text1Level(R.string.ninchiV)
                }
            }
        }
    }
}

