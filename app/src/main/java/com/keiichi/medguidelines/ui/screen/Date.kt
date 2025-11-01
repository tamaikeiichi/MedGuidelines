package com.keiichi.medguidelines.ui.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class TextAndUrl(val textId: Int, val urlId: Int)

@Composable
fun DateScreen(
    navController: NavController,
) {
    val references = listOf(
        TextAndUrl(R.string.space, R.string.space),
    )

    val calendar = Calendar.getInstance()
    val yearToday = remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val monthToday = remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    val dayToday = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    val endCalendar = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_MONTH, 2) // Safely add one month
    }
    val yearEnd = remember { mutableStateOf(endCalendar.get(Calendar.YEAR)) }
    val monthEnd = remember { mutableStateOf(endCalendar.get(Calendar.MONTH)) }
    val dayEnd = remember { mutableStateOf(endCalendar.get(Calendar.DAY_OF_MONTH)) }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            yearToday.value = year
            monthToday.value = month
            dayToday.value = dayOfMonth
        }, yearToday.value, monthToday.value, dayToday.value
    )
    val datePickerDialogEnd = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            yearEnd.value = year
            monthEnd.value = month
            dayEnd.value = dayOfMonth
        }, yearEnd.value, monthEnd.value, dayEnd.value
    )

    val diffDays = remember(yearToday.value, monthToday.value, dayToday.value, yearEnd.value, monthEnd.value, dayEnd.value) {
        val startCalendar = Calendar.getInstance().apply {
            set(yearToday.value, monthToday.value, dayToday.value)
        }
        val finalCalendar = Calendar.getInstance().apply {
            set(yearEnd.value, monthEnd.value, dayEnd.value)
        }
        val diff = finalCalendar.timeInMillis - startCalendar.timeInMillis
        TimeUnit.MILLISECONDS.toDays(diff)
    }

    val displayText = buildAnnotatedString {
        append(diffDays.toString())
        append(" ")
        append(stringResource(id = R.string.days))
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = buildAnnotatedString {
                    append(stringResource(R.string.dateTitle))
                },
                navController = navController,
                references = references,
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(
                displayText = displayText
            )
        },
        modifier = Modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(Dimensions.lazyColumnPadding),
            state = rememberLazyListState()
        ) {
            item {
                MedGuidelinesCard(
                    modifier = Modifier
                        .padding(Dimensions.cardPadding)
                        .clickable(
                            onClick = { datePickerDialog.show() }
                        )
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.todayStartingDate))
                        },
                        fontSize = 20.sp,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.yearMonthDay))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("${yearToday.value}")
                            append("/")
                            append("${monthToday.value + 1}")
                            append("/")
                            append("${dayToday.value}")
                        },
                        fontSize = 25.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center

                    )

                }
                MedGuidelinesCard(
                    modifier = Modifier
                        .padding(Dimensions.cardPadding)
                        .clickable(
                            onClick = { datePickerDialogEnd.show() }
                        )
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.endDate))
                        },
                        fontSize = 20.sp,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.yearMonthDay))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("${yearEnd.value}")
                            append("/")
                            append("${monthEnd.value + 1}")
                            append("/")
                            append("${dayEnd.value}")
                        },
                        fontSize = 25.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center

                    )

                }
            }
        }

    }
}

@Preview
@Composable
fun DateScreenPreview() {
    MedGuidelinesTheme {
        DateScreen(navController = rememberNavController())
    }
}