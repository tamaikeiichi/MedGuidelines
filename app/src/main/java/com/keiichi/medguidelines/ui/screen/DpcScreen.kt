package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import java.io.InputStream
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.data.dataStore
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.io.ColType
import kotlin.collections.map
import kotlin.text.contains
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.keiichi.medguidelines.data.PairedTextItem
import com.keiichi.medguidelines.ui.component.ShinryokoiMasterScreenItemCard
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.doAnyDecodeStringsMatchAnyPairedItemsStrings
import com.keiichi.medguidelines.ui.component.saveListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun DpcScreen(navController: NavHostController){
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })

    val filePath =  context.resources.openRawResource(R.raw.dpc001348055)
    val dfFromFilePath = DataFrame.readExcel(filePath)
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