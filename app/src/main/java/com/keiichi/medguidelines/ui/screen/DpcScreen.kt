package com.keiichi.medguidelines.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readExcel

@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current
    val df = remember {
        val inputStream = context.resources.openRawResource(R.raw.dpc001348055)
        val sheetName = "１）ＭＤＣ名称"
        DataFrame.readExcel(
            inputStream,
            sheetName)
    }

    MedGuidelinesScaffold (
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
    ){

        Text(df.toString())
        Text("test")
    }
}

@Composable
private fun loadDpcData(sheetName: String): DataFrame<*> {
    val df = remember {
        val inputStream = context.resources.openRawResource(R.raw.dpc001348055)
        DataFrame.readExcel(
            inputStream,
            sheetName)
    }
    return df
}

@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}
