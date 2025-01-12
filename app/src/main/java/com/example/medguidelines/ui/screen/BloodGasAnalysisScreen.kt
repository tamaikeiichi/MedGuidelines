package com.example.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medguidelines.R
import com.example.medguidelines.ui.component.TitleTopAppBar
import com.example.medguidelines.ui.component.parseStyledString
import kotlinx.coroutines.flow.Flow

@Composable
fun BloodGasAnalysisScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TitleTopAppBar(
                title = stringResource(id = R.string.bloodGasAnalysisTitle),
                navController = navController
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            state = rememberLazyListState()
        ) {
            item {
                BloodGasAnalysisInput()
            }

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodGasAnalysisInput() {
    val ph = remember { mutableDoubleStateOf(7.40) }
    val po2 = remember { mutableDoubleStateOf(100.0) }
    val pco2 = remember { mutableDoubleStateOf(40.0) }
    val hco3 = remember { mutableDoubleStateOf(24.0) }
    val na = remember { mutableDoubleStateOf(145.0) }
    val k = remember { mutableDoubleStateOf(4.5) }
    val cl = remember { mutableDoubleStateOf(98.0) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        FlowRow() {
            BloodGasAnalysisOutlinedTextField(label = R.string.ph, value = ph, width = 90)
            BloodGasAnalysisOutlinedTextField(label = R.string.po2, value = po2, width = 110)
            BloodGasAnalysisOutlinedTextField(label = R.string.pco2, value = pco2, width = 100)
            BloodGasAnalysisOutlinedTextField(label = R.string.hco3, value = hco3, width = 100)
            BloodGasAnalysisOutlinedTextField(label = R.string.na, value = na, width = 110)
            BloodGasAnalysisOutlinedTextField(label = R.string.k, value = k, width = 100)
            BloodGasAnalysisOutlinedTextField(label = R.string.cl, value = cl, width = 110)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGasAnalysisOutlinedTextField(label: Int, value: MutableDoubleState, width: Int) {
    TextField(
        label = { Text(parseStyledString(label)) },
        value = value.doubleValue.toString(),
        onValueChange = {},
        modifier = Modifier
            .padding(8.dp)
            .width(width.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = TextStyle.Default.copy(
            fontSize = 28.sp,
            textAlign = TextAlign.Right
        ),
        maxLines = 1,
        //TextFieldColors =

    )
}

@Preview
@Composable
fun BloodGasAnalysisScreenPreview() {
    BloodGasAnalysisScreen(navController = NavController(LocalContext.current))
}
