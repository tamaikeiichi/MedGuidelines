package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.NameRepairStrategy
import org.jetbrains.kotlinx.dataframe.io.readExcel

// Data class to hold the final DataFrame objects.
data class DpcDataSheets(
    var nenrei: DataFrame<*>? = null,
    var shujutu: DataFrame<*>? = null,
    // Other properties...
)

@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current

    var df by remember { mutableStateOf(DpcDataSheets()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val loadedNenrei = withContext(Dispatchers.IO) {
                loadDpcData(context, "５）年齢、出生時体重等")
            }
            println("✅ 'nenrei' sheet read successfully. Rows: ${loadedNenrei.rowsCount()}")

            val loadedShujutu = withContext(Dispatchers.IO) {
                loadDpcData(context, "６）手術 ")
            }
            println("✅ 'shujutu' sheet read successfully. Rows: ${loadedShujutu.rowsCount()}")

            // Update the UI state with the final DataFrame objects.
            withContext(Dispatchers.Main) {
                df = df.copy(
                    nenrei = loadedNenrei,
                    shujutu = loadedShujutu
                )
            }

        } catch (t: Throwable) {
            errorMessage = t.message ?: "An unknown error occurred"
            t.printStackTrace()
        } finally {
            isLoading = false
        }
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
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(text = "Error: $errorMessage")
                else -> {
                    Column {
                        Text("Nenrei DataFrame is null: ${df.nenrei == null}")
                        Text("Shujutu DataFrame is null: ${df.shujutu == null}")
                        Text("Nenrei rows loaded: ${df.nenrei?.rowsCount() ?: 0}")
                        Text("Shujutu rows loaded: ${df.shujutu?.rowsCount() ?: 0}")
                        Text("${df.nenrei?.schema()}")
                        Text("${df.shujutu?.schema()}")
                    }
                }
            }
        }
    }
}

// THIS IS THE CORRECTED FUNCTION using only kotlinx-dataframe
private fun loadDpcData(context: Context, sheetName: String): DataFrame<*> {
    context.resources.openRawResource(R.raw.dpc001348055).use { inputStream ->
        return DataFrame.readExcel(
            inputStream = inputStream,
            sheetName = sheetName,
            skipRows = 0,
            nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
            firstRowIsHeader = false
            //parseEmptyAsNull = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}
