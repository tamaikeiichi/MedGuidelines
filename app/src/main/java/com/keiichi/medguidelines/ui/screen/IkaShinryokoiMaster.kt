package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import java.io.InputStream
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.ui.component.Dimensions
import java.nio.charset.Charset
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.io.ColType

// Define a simple wrapper class
data class IndexedItem<T>(val index: Int, val data: T)

// Define a data class to hold the paired data for clarity (optional but recommended)
data class PairedTextItem(
    val shinryoKoiKanjiMeisho: Any?,
    val tensuShikibetsu: String,
    val tensu: String, val originalIndex: Int
)


@Composable
fun IkaShinryokoiMasterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })
    val numberOfColumns = 50

    val master: AnyFrame? = try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.s_20250602)
        val headerNames = (1..numberOfColumns).map { it.toString() }
        val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }

        inputStream.use { stream ->
            DataFrame.readCSV(
                stream = stream,
                header = headerNames,
                charset = Charset.forName("Shift-JIS"),
                colTypes = columnTypes, // Specify that all columns should be read as String
                parserOptions = ParserOptions() // Keep default or adjust as needed
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }finally {
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
            //val numberFormatter = remember { DecimalFormat("#.##") }
            // Prepare your list of paired data
            val pairedDataList: List<PairedTextItem> = remember(master) {
                if (master == null) return@remember emptyList()

                // Adjust indices if your header generation (1..50) means columns are 0-indexed internally
                // If header = listOf("1", "2", ...), then "5" is at index 4, "10" is at index 9.
                val shinryoKoiKanjiMeishoIndex = 4  // Column "5"
                val tensuShikibetsuIndex = 10
                val tensuIndex = 11 // Column "10"

                if (master.columnsCount() > shinryoKoiKanjiMeishoIndex &&
                    master.columnsCount() > tensuShikibetsuIndex && // Check new column index
                    master.columnsCount() > tensuIndex) {
                    try {
                        val shinryoKoiKanjiMeishoList = master.columns()[shinryoKoiKanjiMeishoIndex].toList()

                        // Extract the tensuShikibetsu column and map to String (or desired type)
                        val tensuShikibetsuList = master.columns()[tensuShikibetsuIndex].toList().map { value ->
                            value?.toString() ?: "" // Or parse to Int if needed: value.toString().toIntOrNull()
                        }

                        val tensuList = master.columns()[tensuIndex].toList().map { value ->
                            value?.toString() ?: ""
                        }

                        // Combine the three lists
                        // First, zip shinryoKoiKanjiMeishoList and tensuShikibetsuList
                        shinryoKoiKanjiMeishoList.zip(tensuShikibetsuList)
                            // Result: List<Pair<Any?, String>> (shinryoKoiKanjiMeisho, tensuShikibetsu)
                            // Then, zip this result with tensuList
                            .zip(tensuList)
                            // Result: List<Pair<Pair<Any?, String>, String>>
                            // ((shinryoKoiKanjiMeisho, tensuShikibetsu), tensu)
                            .mapIndexed { index, nestedPair ->
                                val firstPair = nestedPair.first // This is Pair(shinryoKoiKanjiMeisho, tensuShikibetsu)
                                val kanjiMeisho = firstPair.first
                                val shikibetsu = firstPair.second
                                val currentTensu = nestedPair.second // This is tensu

                                PairedTextItem(
                                    shinryoKoiKanjiMeisho = kanjiMeisho,
                                    tensuShikibetsu = shikibetsu,
                                    tensu = currentTensu,
                                    originalIndex = index
                                )
                            }
                    } catch (e: IndexOutOfBoundsException) {
                        println("Error accessing columns for paired data: ${e.message}")
                        emptyList()
                    }
                } else {
                    println("Not enough columns in DataFrame for paired data.")
                    emptyList()
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(2.dp),
            ) {
                items(
                    items = pairedDataList,
                    key = { pairedItem ->
                        pairedItem.originalIndex // Use the original row index as a stable key
                    }
                ) { pairedItem ->
                    MedGuidelinesCard(
                        modifier = Modifier.padding(Dimensions.cardPadding)
                    ) {
                        // Use a Row to display items side-by-side
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.textPadding),
                            verticalAlignment = Alignment.CenterVertically // Optional: align items vertically
                        ) {
                            Text(
                                text = pairedItem.shinryoKoiKanjiMeisho?.toString() ?: "N/A",
                                modifier = Modifier.weight(4f),
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Space between headline and supporting text
                            Text(
                                text = pairedItem.tensu,
                                modifier = Modifier.weight(1f),
                            )
                        }
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
    IkaShinryokoiMasterScreen(navController = NavHostController(LocalContext.current))
}