package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.room.Room
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.AppDatabase
import com.keiichi.medguidelines.data.DpcEntry
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
// 1. DELETE THE INCORRECT IMPORT for nrow
// import org.jetbrains.kotlinx.dataframe.nrow
// 2. ADD THE CORRECT IMPORT for rowsCount
import java.io.InputStream

// This is our new, more robust Excel reading function using Apache POI
suspend fun readExcelWithPoi(inputStream: InputStream, sheetIndex: Int): DataFrame<*> {
    return withContext(Dispatchers.IO) { // Ensure this runs on a background thread
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(sheetIndex)

        // Get header row
        val headerRow = sheet.getRow(0)
        val headers = headerRow.map { cell -> cell.stringCellValue }.toTypedArray()

        // Get data rows
        val dataRows = (1..sheet.lastRowNum).map { rowIndex ->
            val row = sheet.getRow(rowIndex)
            row.map { cell ->
                when (cell.cellType) {
                    org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                    org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue
                    org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue
                    else -> ""
                }
            }
        }

        // Manually construct the DataFrame
        dataFrameOf(*headers)(*dataRows.flatten().toTypedArray())
    }
}
// This is a simplified example. You would typically use a proper DI framework.
private fun getDb(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "dpc-database"
    ).build()
}

@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { getDb(context) }
    val dpcDao = remember { db.dpcDao() }

    // This state will now come from the database
    val dpcEntries by dpcDao.getAll().collectAsState(initial = emptyList<DpcEntry>())
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Checking database...") }

    LaunchedEffect(Unit) {
        // Run this check in the background
        withContext(Dispatchers.IO) {
            val count = dpcDao.getCount()
            if (count == 0) {
                // Database is empty, we need to populate it from Excel
                statusMessage = "Database is empty. Populating from Excel file..."
                try {
                    val filePath = context.resources.openRawResource(R.raw.dpc001348055)
                    val workbook = WorkbookFactory.create(filePath)
                    val sheet = workbook.getSheetAt(0)

                    val entriesToInsert = mutableListOf<DpcEntry>()
                    // Iterate row by row instead of loading all at once
                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex) ?: continue
                        // Create an entry from the row
                        val entry = DpcEntry(
                            column1 = row.getCell(0)?.stringCellValue ?: "",
                            column2 = row.getCell(1)?.stringCellValue ?: "",
                            column3 = row.getCell(2)?.numericCellValue
                        )
                        entriesToInsert.add(entry)

                        // To avoid memory spikes, insert in batches
                        if (entriesToInsert.size >= 1000) {
                            dpcDao.insertAll(entriesToInsert)
                            entriesToInsert.clear()
                            statusMessage = "Inserted ${rowIndex} rows..."
                        }
                    }
                    // Insert any remaining entries
                    if (entriesToInsert.isNotEmpty()) {
                        dpcDao.insertAll(entriesToInsert)
                    }
                    statusMessage = "Database population complete!"
                } catch (t: Throwable) {
                    statusMessage = "Error populating database: ${t.message}"
                    t.printStackTrace()
                }
            } else {
                statusMessage = "Data loaded from existing database."
            }
            isLoading = false
        }
    }

    MedGuidelinesScaffold {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(text = statusMessage, modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                Text(text = "Loaded ${dpcEntries.size} entries from database.")
                // Here you would add a LazyColumn to display the dpcEntries
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}
