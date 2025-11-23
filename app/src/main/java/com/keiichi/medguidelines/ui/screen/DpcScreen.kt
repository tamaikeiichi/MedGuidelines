package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.R
import androidx.navigation.NavHostController
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

@Composable
fun DpcScreen(navController: NavHostController){
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })

    val filePath =  context.resources.openRawResource(R.raw.dpc001348055)
//    val dfFromFilePath = DataFrame.readExcel(
//        filePath,
//        sheetName = "１）ＭＤＣ名称")
    val mdc = DataFrame.readExcel(
        inputStream = filePath,
        sheetName = "１）ＭＤＣ名称"
    )
    MedGuidelinesScaffold (){
        println(mdc.schema())
    }

}

@Preview
@Composable
fun DpcScreenPreview(){
    DpcScreen(
        navController = NavHostController(LocalContext.current)
    )
}