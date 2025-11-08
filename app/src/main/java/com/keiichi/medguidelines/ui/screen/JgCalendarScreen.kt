package com.keiichi.medguidelines.ui.screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBarVariable

data class Calendar(
    var meiji: Int,
    var taisho: Int,
    var showa: Int,
    var heisei: Int,
    var reiwa: Int,
    var ac: Int
) {
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JgCalendarScreen(
    navController: NavController,
) {
    val configuration = LocalConfiguration.current
    val isJapaneseLocale = remember(configuration) {
        configuration.locales[0].language == "ja"
    }

    val references = listOf(
        TextAndUrl(R.string.jEraName, R.string.jgCalendarUrl),
    )
    val meiji = remember { mutableDoubleStateOf(0.0) }
    val taisho = remember { mutableDoubleStateOf(0.0) }
    val showa = remember { mutableDoubleStateOf(0.0) }
    val heisei = remember { mutableDoubleStateOf(0.0) }
    val reiwa = remember { mutableDoubleStateOf(7.0) }
    val ac = remember { mutableDoubleStateOf(2025.0) }

    var isMeijiDirectlySet by remember { mutableStateOf(false) }
    var isTaishoDirectlySet by remember { mutableStateOf(false) }
    var isShowaDirectlySet by remember { mutableStateOf(false) }
    var isHeiseiDirectlySet by remember { mutableStateOf(false) }
    var isReiwaDirectlySet by remember { mutableStateOf(false) }
    var isAcDirectlySet by remember { mutableStateOf(false) }

    var allCalendarScores by remember {
        mutableStateOf(
            Calendar(
                0,
                0,
                0,
                0,
                0,
                0
            )
        )
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBarVariable(
                title = buildAnnotatedString {
                    append(stringResource(R.string.JgCalendarTitle))
                },
                navController = navController,
                references = references,
            )
        },
        bottomBar = {
            val displayText =
                buildAnnotatedString {

                    if (isMeijiDirectlySet) {
                        append(stringResource(R.string.meiji))
                        append(" ")
                        append(meiji.doubleValue.toInt().toGannenString(isJapaneseLocale))

                        append(stringResource(R.string.nen))
                        append("\n")
                    }
                    if(isTaishoDirectlySet) {
                        append(stringResource(R.string.taisho))
                        append(" ")
                        append(taisho.doubleValue.toInt().toGannenString(isJapaneseLocale))
                        append(stringResource(R.string.nen))
                        append("\n")
                    }
                    if (isShowaDirectlySet) {
                        append(stringResource(R.string.showa))
                        append(" ")
                        append(showa.doubleValue.toInt().toGannenString(isJapaneseLocale))
                        append(stringResource(R.string.nen))
                        append("\n")
                    }
                    if(isHeiseiDirectlySet) {
                        append(stringResource(R.string.heisei))
                        append(" ")
                        append(heisei.doubleValue.toInt().toGannenString(isJapaneseLocale))
                        append(stringResource(R.string.nen))
                        append("\n")
                    }
                    if(isReiwaDirectlySet) {
                        append(stringResource(R.string.reiwa))
                        append(" ")
                        append(reiwa.doubleValue.toInt().toGannenString(isJapaneseLocale))
                        append(stringResource(R.string.nen))
                        append("\n")
                    }
                    append(stringResource(R.string.ad))
                    append(" ")
                    append(ac.value.toInt().toString())
                    append(stringResource(R.string.nen))

                }
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
                allCalendarScores = inputAndCalculateCalendar(
                    meiji = meiji,
                    taisho = taisho,
                    showa = showa,
                    heisei = heisei,
                    reiwa = reiwa,
                    ac = ac,
                    isMeijiDirectlySet = isMeijiDirectlySet,
                    onIsMeijiDirectlySetChange = { isMeijiDirectlySet = it }, // Pass the setter
                    isTaishoDirectlySet = isTaishoDirectlySet,
                    onIsTaishoDirectlySetChange = { isTaishoDirectlySet = it }, // Pass the setter
                    isShowaDirectlySet = isShowaDirectlySet,
                    onIsShowaDirectlySetChange = { isShowaDirectlySet = it }, // Pass the setter
                    isHeiseiDirectlySet = isHeiseiDirectlySet,
                    onIsHeiseiDirectlySetChange = { isHeiseiDirectlySet = it }, // Pass the setter
                    isReiwaDirectlySet = isReiwaDirectlySet,
                    onIsReiwaDirectlySetChange = { isReiwaDirectlySet = it }, // Pass the setter
                    isAcDirectlySet = isAcDirectlySet,
                    onIsAcDirectlySetChange = { isAcDirectlySet = it } // Pass the setter
                )
            }
            }
        }
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun inputAndCalculateCalendar(
    meiji: MutableDoubleState,
    taisho: MutableDoubleState,
    showa: MutableDoubleState,
    heisei: MutableDoubleState,
    reiwa: MutableDoubleState,
    ac: MutableDoubleState,
    isMeijiDirectlySet: Boolean,
    onIsMeijiDirectlySetChange: (Boolean) -> Unit, // Add this
    isTaishoDirectlySet: Boolean,
    onIsTaishoDirectlySetChange: (Boolean) -> Unit, // Add this
    isShowaDirectlySet: Boolean,
    onIsShowaDirectlySetChange: (Boolean) -> Unit, // Add this
    isHeiseiDirectlySet: Boolean,
    onIsHeiseiDirectlySetChange: (Boolean) -> Unit, // Add this
    isReiwaDirectlySet: Boolean,
    onIsReiwaDirectlySetChange: (Boolean) -> Unit, // Add this
    isAcDirectlySet: Boolean,
    onIsAcDirectlySetChange: (Boolean) -> Unit, // Change this
): Calendar {


    LaunchedEffect(meiji.doubleValue) {
        if (meiji.doubleValue > 0) { // Optional: only calculate if there's a value
            ac.doubleValue = meiji.doubleValue + 1867
            onIsMeijiDirectlySetChange(true) // Mark AC as being set by another component
            if (ac.doubleValue != 1912.0) {
                onIsTaishoDirectlySetChange(false)
            }
            onIsShowaDirectlySetChange(false)
            onIsHeiseiDirectlySetChange(false)
            onIsReiwaDirectlySetChange(false)
            onIsAcDirectlySetChange(true)
        }
    }

    // When Taisho changes, recalculate AC
    LaunchedEffect(taisho.doubleValue) {
        if (taisho.doubleValue > 0) {
            ac.doubleValue = taisho.doubleValue + 1911
            onIsTaishoDirectlySetChange(true)
            onIsMeijiDirectlySetChange(false)
            if (ac.doubleValue != 1926.0) {
                onIsShowaDirectlySetChange(false)
            }
            onIsHeiseiDirectlySetChange(false)
            onIsReiwaDirectlySetChange(false)
            onIsAcDirectlySetChange(true)
        }
    }
    LaunchedEffect(showa.doubleValue) {
        if (showa.doubleValue > 0) {
            ac.doubleValue = showa.doubleValue + 1925
            onIsShowaDirectlySetChange(true)
            onIsMeijiDirectlySetChange(false)
            onIsTaishoDirectlySetChange(false)
            if (ac.doubleValue != 1989.0) {
                onIsHeiseiDirectlySetChange(false)
            }
            onIsReiwaDirectlySetChange(false)
            onIsAcDirectlySetChange(true)
        }
    }
    LaunchedEffect(heisei.doubleValue) {
        if (heisei.doubleValue > 0) {
            ac.doubleValue = heisei.doubleValue + 1988
            onIsHeiseiDirectlySetChange(true)
            onIsMeijiDirectlySetChange(false)
            onIsTaishoDirectlySetChange(false)
            onIsShowaDirectlySetChange(false)
            if (ac.doubleValue != 2019.0) {
                onIsReiwaDirectlySetChange(false)
            }
            onIsAcDirectlySetChange(true)
        }
    }
    LaunchedEffect(reiwa.doubleValue) {
        if (reiwa.doubleValue > 0) {
            ac.doubleValue = reiwa.doubleValue + 2018
            onIsReiwaDirectlySetChange(true)
            onIsMeijiDirectlySetChange(false)
            onIsTaishoDirectlySetChange(false)
            onIsShowaDirectlySetChange(false)
            onIsHeiseiDirectlySetChange(false)
            onIsAcDirectlySetChange(true)
        }
    }

    LaunchedEffect(ac.doubleValue) {
        val acYear = ac.doubleValue.toInt()
        if (acYear > 0) {
            // Meiji (1868-1912)
            if (acYear in 1868..<1912) {
                meiji.doubleValue = (acYear - 1867).toDouble()
            } else {
                meiji.doubleValue = 0.0 // Clear if outside the range
            }
            if (acYear == 1912){
                meiji.doubleValue = 45.0
                taisho.doubleValue = 1.0
            }
            // Taisho (1912-1926)
            if (acYear in 1913..<1926) {
                taisho.doubleValue = (acYear - 1911).toDouble()
            } else if (acYear != 1912) {
                taisho.doubleValue = 0.0
            }
            if (acYear == 1926){
                taisho.doubleValue = 15.0
                showa.doubleValue = 1.0
            }

            // Showa (1926-1989)
            if (acYear in 1927..<1989) {
                showa.doubleValue = (acYear - 1925).toDouble()
            } else if (acYear != 1926) {
                showa.doubleValue = 0.0
            }
            if (acYear == 1989){
                showa.doubleValue = 64.0
                heisei.doubleValue = 1.0
            }

            // Heisei (1989-2019)
            if (acYear in 1990..<2019) {
                heisei.doubleValue = (acYear - 1988).toDouble()
            } else if (acYear != 1989) {
                heisei.doubleValue = 0.0
            }
            if(acYear == 2019) {
                heisei.doubleValue = 31.0
                reiwa.doubleValue = 1.0
            }

            // Reiwa (2019+)
            if (acYear > 2019) {
                reiwa.doubleValue = (acYear - 2018).toDouble()
            } else if(acYear != 2019) {
                reiwa.doubleValue = 0.0
            }

            // Now, set the input flags correctly
            onIsAcDirectlySetChange(true)
//            onIsMeijiDirectlySetChange(false)
//            onIsTaishoDirectlySetChange(false)
//            onIsShowaDirectlySetChange(false)
//            onIsHeiseiDirectlySetChange(false)
//            onIsReiwaDirectlySetChange(false)
        }
    }


    MedGuidelinesCard(
        modifier = Modifier
            .padding(
                Dimensions.cardPadding
            )
    ) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp)
                .wrapContentHeight(
                    align = Alignment.Bottom
                ),
            itemVerticalAlignment = Alignment.Bottom,
        ) {
            InputValue(
                label = R.string.meiji, value = meiji,
                japaneseUnit = R.string.nen,
                isSetByOtherComponent = !isMeijiDirectlySet
            )
            InputValue(
                label = R.string.taisho, value = taisho,
                japaneseUnit = R.string.nen,
                isSetByOtherComponent = !isTaishoDirectlySet
            )
            InputValue(
                label = R.string.showa, value = showa,
                japaneseUnit = R.string.nen,
                isSetByOtherComponent = !isShowaDirectlySet
            )
            InputValue(
                label = R.string.heisei, value = heisei,
                japaneseUnit = R.string.nen,
                isSetByOtherComponent = !isHeiseiDirectlySet
            )
            InputValue(
                label = R.string.reiwa, value = reiwa,
                japaneseUnit = R.string.nen,
                isSetByOtherComponent = !isReiwaDirectlySet
            )
            InputValue(
                label = R.string.ad, value = ac,
                japaneseUnit = R.string.nen,
                isSetByOtherComponent = !isAcDirectlySet
            )
        }
    }

    val meiji = meiji.doubleValue.toInt()
    val taisho = taisho.doubleValue.toInt()
    val showa = showa.doubleValue.toInt()
    val heisei = heisei.doubleValue.toInt()
    val reiwa = reiwa.doubleValue.toInt()
    val ac = ac.doubleValue.toInt()

    val allScores =
        Calendar(
            meiji = meiji,
            taisho = taisho,
            showa = showa,
            heisei = heisei,
            reiwa = reiwa,
            ac = ac
        )
    return allScores
}

private fun Int.toGannenString(isJapaneseLocale: Boolean): String {
    return if (this == 1 && isJapaneseLocale) {
        "元" // You can also use a string resource here if you prefer
    } else {
        this.toString()
    }
}

@Preview
@Composable
fun JgCalendarScreenPreview() {
    JgCalendarScreen(navController = NavController(LocalContext.current))
}

