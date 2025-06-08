package com.keiichi.medguidelines.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV
import com.keiichi.medguidelines.R
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.print
import java.io.InputStream

@Composable
fun IkaShinryokoiMasterScreen() {
    val context = LocalContext.current
    val master: AnyFrame? = try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.s_20250602)
        inputStream.use { stream ->
            DataFrame.readCSV(stream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    MedGuidelinesScaffold {
        MedGuidelinesCard {
            if (master != null) {
                // Now you can work with 'master' DataFrame
                // Example: Display the number of rows
                Text("Number of rows: ${master.rowsCount()}")

                // Example: Print the first 5 rows to logcat (for debugging)
                master.head().print()

                // Example: Get a specific column as a list
                // val firstColumnData = master.columns().firstOrNull()?.toList()

                // Example: Filter and display
                // val filteredData = master.filter { it["SomeColumnName"] > someValue }
                // LazyColumn {
                //     items(filteredData.rows().toList()) { row ->
                //         Text(row.toString()) // Customize how you display each row
                //     }
                // }

            } else {
                Text("Could not load data.")
            }
        }
    }
}

@Preview
@Composable
fun IkaShinryokoiMasterScreenPreview(){
    IkaShinryokoiMasterScreen()
}