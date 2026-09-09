package com.keiichi.medguidelines.ui.screen


import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
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
        add(Calendar.WEEK_OF_MONTH, 2)
    }
    val yearEnd = remember { mutableStateOf(endCalendar.get(Calendar.YEAR)) }
    val monthEnd = remember { mutableStateOf(endCalendar.get(Calendar.MONTH)) }
    val dayEnd = remember { mutableStateOf(endCalendar.get(Calendar.DAY_OF_MONTH)) }

    fun computeDiff(yS: Int, mS: Int, dS: Int, yE: Int, mE: Int, dE: Int): Long {
        val start = Calendar.getInstance().apply { set(yS, mS, dS) }
        val end = Calendar.getInstance().apply { set(yE, mE, dE) }
        return TimeUnit.MILLISECONDS.toDays(end.timeInMillis - start.timeInMillis)
    }

    val diffDaysValue = remember {
        val initialDiff = computeDiff(
            yearToday.value, monthToday.value, dayToday.value,
            yearEnd.value, monthEnd.value, dayEnd.value
        )
        mutableDoubleStateOf(initialDiff.toDouble())
    }

    LaunchedEffect(diffDaysValue.doubleValue) {
        val days = diffDaysValue.doubleValue.toLong()
        val newEnd = Calendar.getInstance().apply {
            set(yearToday.value, monthToday.value, dayToday.value)
            add(Calendar.DAY_OF_MONTH, days.toInt())
        }
        yearEnd.value = newEnd.get(Calendar.YEAR)
        monthEnd.value = newEnd.get(Calendar.MONTH)
        dayEnd.value = newEnd.get(Calendar.DAY_OF_MONTH)
    }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            yearToday.value = year
            monthToday.value = month
            dayToday.value = dayOfMonth
            diffDaysValue.doubleValue = computeDiff(
                year, month, dayOfMonth,
                yearEnd.value, monthEnd.value, dayEnd.value
            ).toDouble()
        }, yearToday.value, monthToday.value, dayToday.value
    )
    val datePickerDialogEnd = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            yearEnd.value = year
            monthEnd.value = month
            dayEnd.value = dayOfMonth
            diffDaysValue.doubleValue = computeDiff(
                yearToday.value, monthToday.value, dayToday.value,
                year, month, dayOfMonth
            ).toDouble()
        }, yearEnd.value, monthEnd.value, dayEnd.value
    )

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
        modifier = Modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + Dimensions.lazyColumnPadding,
                bottom = innerPadding.calculateBottomPadding() + Dimensions.lazyColumnPadding,
                start = Dimensions.lazyColumnPadding,
                end = Dimensions.lazyColumnPadding
            ),
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
                        color = MaterialTheme.colorScheme.onBackground,
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
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                MedGuidelinesCard(
                    modifier = Modifier
                        .padding(Dimensions.cardPadding)
                        .widthIn(max = 220.dp)
                ) {
                    Text(
                        text = stringResource(R.string.numberOfDays),
                        fontSize = 20.sp,
                    )
                    InputValue(
                        label = R.string.space,
                        value = diffDaysValue,
                        japaneseUnit = R.string.days,
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
