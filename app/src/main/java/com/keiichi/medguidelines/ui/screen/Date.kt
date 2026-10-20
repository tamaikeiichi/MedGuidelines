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
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

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

    // 月は Calendar / DatePicker と同じ0始まり。日付だけで差を求める（時刻による端数を出さない）
    fun computeDiff(yS: Int, mS: Int, dS: Int, yE: Int, mE: Int, dE: Int): Long =
        ChronoUnit.DAYS.between(LocalDate.of(yS, mS + 1, dS), LocalDate.of(yE, mE + 1, dE))

    val diffDaysValue = remember {
        val initialDiff = computeDiff(
            yearToday.value, monthToday.value, dayToday.value,
            yearEnd.value, monthEnd.value, dayEnd.value
        )
        mutableDoubleStateOf(initialDiff.toDouble())
    }

    // 終了日を「開始日 + days日」にする
    fun setEndFromStart(days: Long) {
        val newEnd = Calendar.getInstance().apply {
            set(yearToday.value, monthToday.value, dayToday.value)
            add(Calendar.DAY_OF_MONTH, days.toInt())
        }
        yearEnd.value = newEnd.get(Calendar.YEAR)
        monthEnd.value = newEnd.get(Calendar.MONTH)
        dayEnd.value = newEnd.get(Calendar.DAY_OF_MONTH)
    }

    // 終了日は常に開始日より後にする（日数は1日以上）
    fun setDiffAtLeastOneDay(days: Long) {
        val validDays = days.coerceAtLeast(1)
        diffDaysValue.doubleValue = validDays.toDouble()
        setEndFromStart(validDays)
    }

    LaunchedEffect(diffDaysValue.doubleValue) {
        setDiffAtLeastOneDay(diffDaysValue.doubleValue.toLong())
    }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            yearToday.value = year
            monthToday.value = month
            dayToday.value = dayOfMonth
            // 開始日が終了日以降になった場合は，終了日を開始日の1日後にする
            setDiffAtLeastOneDay(
                computeDiff(year, month, dayOfMonth, yearEnd.value, monthEnd.value, dayEnd.value)
            )
        }, yearToday.value, monthToday.value, dayToday.value
    )
    val datePickerDialogEnd = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            // 終了日に開始日以前を選んだ場合も，開始日の1日後にする
            setDiffAtLeastOneDay(
                computeDiff(yearToday.value, monthToday.value, dayToday.value, year, month, dayOfMonth)
            )
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
