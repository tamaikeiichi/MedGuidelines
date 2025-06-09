package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.print
import java.io.InputStream
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import java.io.InputStreamReader
import java.nio.charset.Charset


// Define a simple wrapper class
data class IndexedItem<T>(val index: Int, val data: T)

@Composable
fun IkaShinryokoiMasterScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })

    val master: AnyFrame? = try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.s_20250602)
        inputStream.use { stream -> // Ensures inputStream is closed automatically
            DataFrame.readCSV(
                stream = stream, // Pass the InputStream
                charset = Charset.forName("Shift-JIS") // Specify the charset directly
                // You can add other parameters like delimiter if needed, but they have defaults
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }finally {
        // inputStream is closed by openRawResource().use {} or by the reader if that's how you used it
        // If you passed the reader directly without a .use block on the stream, ensure it's closed.
        // However, DataFrame.readCSV(reader) should ideally handle closing the reader it's given
        // if it consumes it fully. If using inputStream.use { ... DataFrame.readCSV(InputStreamReader(it, ...)) },
        // the 'use' block handles closing the inputStream.
    }

    MedGuidelinesScaffold {
        Column {
            MyCustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    searchQuery = newQuery
                },
                onSearch = { query ->
                    println("Search submitted: $query")
                },
            )


// Prepare your list
            val indexedShinryoKoiShoryakuKanjiMeisho: List<IndexedItem<Any?>> = remember(master) {
                val fifthColumnData = master?.let { df ->
                    val targetColumnIndex = 4
                    if (df.columnsCount() > targetColumnIndex) {
                        try {
                            df.columns()[targetColumnIndex].toList()
                        } catch (e: IndexOutOfBoundsException) {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                } ?: emptyList()

                // Wrap each item with its index
                fifthColumnData.mapIndexed { index, item -> IndexedItem(index, item) }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(10.dp),
            ) {
                items(
                    items = indexedShinryoKoiShoryakuKanjiMeisho,
                    key = { indexedItem: IndexedItem<Any?> ->
                        indexedItem.index
                    }
                ) { indexedItem ->
                    val cellValue = indexedItem.data
                    MedGuidelinesCard(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = cellValue?.toString()
                                ?: "NULL",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
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
}

@Preview
@Composable
fun IkaShinryokoiMasterScreenPreview(){
    IkaShinryokoiMasterScreen()
}