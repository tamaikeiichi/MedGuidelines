package com.keiichi.medguidelines.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readExcel

@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current
    val inputStream = context.resources.openRawResource(R.raw.dpc001348055)
    val df = DataFrame.readExcel(inputStream)

    println(df)

}


@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}
