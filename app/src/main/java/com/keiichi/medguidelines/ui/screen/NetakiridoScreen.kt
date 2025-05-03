package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.keiichi.compose.secondaryContainerLight
import com.keiichi.compose.secondaryLight
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.textAndExpand1Level
import com.keiichi.medguidelines.ui.component.TextAndExpand2Levels
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.Text1Level
import com.keiichi.medguidelines.ui.component.textAndUrl

@Composable
fun NetakiridoScreen(navController: NavController) {
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
                cardColor = MaterialTheme.colorScheme.onPrimary,
                textColor = MaterialTheme.colorScheme.primary
            )
            if(expandedShougai){
                Column(){
                    TextAndExpand2Levels(
                        R.string.rankJ,
                        R.string.rankJNote,
                        firstTextColor = MaterialTheme.colorScheme.secondary,
                        secondTextColor = MaterialTheme.colorScheme.tertiary
                    )
                    TextAndExpand2Levels(
                        R.string.rankA,
                        R.string.rankANote,
                        firstTextColor = MaterialTheme.colorScheme.secondary,
                        secondTextColor = MaterialTheme.colorScheme.tertiary
                    )
                    Column(
                    ){
                        val expanded = textAndExpand1Level(
                            firstTitle = R.string.rankBorC,
                            cardColor = MaterialTheme.colorScheme.secondaryContainer,
                            textColor = MaterialTheme.colorScheme.secondary
                        )
                        if (expanded) {
                            Column(
                            )
                            {
                                TextAndExpand2Levels(
                                    firstTitle = R.string.rankBNote,
                                    secondTitle = R.string.rankBNoteSubCategory,
                                    firstTextColor = MaterialTheme.colorScheme.secondary,
                                    secondTextColor = MaterialTheme.colorScheme.tertiary
                                )
                                TextAndExpand2Levels(
                                    firstTitle = R.string.rankCNote,
                                    secondTitle = R.string.rankCNoteSubCategory,
                                    firstTextColor = MaterialTheme.colorScheme.secondary,
                                    secondTextColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
            val expandedNinchi = textAndExpand1Level(
                firstTitle = R.string.ninchishoKoureisha,
                cardColor = MaterialTheme.colorScheme.onPrimary,
                textColor = MaterialTheme.colorScheme.primary
            )
            if(expandedNinchi){
                Column {
                    Text1Level(
                        firstTitle = R.string.ninchiI,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    TextAndExpand2Levels(
                        firstTitle = R.string.ninchiII,
                        secondTitle = R.string.ninchiIINote,
                        firstTextColor = MaterialTheme.colorScheme.secondary,
                        secondTextColor = MaterialTheme.colorScheme.tertiary
                    )
                    TextAndExpand2Levels(
                        firstTitle = R.string.ninchiIII,
                        secondTitle = R.string.ninchiIIINote,
                        firstTextColor = MaterialTheme.colorScheme.secondary,
                        secondTextColor = MaterialTheme.colorScheme.tertiary
                    )
                    Text1Level(
                        firstTitle = R.string.ninchiIV,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text1Level(
                        firstTitle = R.string.ninchiV,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

