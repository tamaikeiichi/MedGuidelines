package com.keiichi.medguidelines.ui.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopAppBarVariable(
    displayText: AnnotatedString,
    fontSize: TextUnit = 24.sp,
    modifier: Modifier = Modifier,
    paddingValues: Dp = 16.dp, // Default padding around the text
    navController: NavController,
    references: List<TextAndUrl>,
    @StringRes helpTitleResId: Int = R.string.about, // Default help title
    @StringRes helpMessageResId: Int? = null, // New: Optional help message string resource
    @StringRes refTitleResId: Int = R.string.reference, // Default reference title
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val textStyle = TextStyle(
        fontSize = fontSize,
        lineHeight = 1.2.em,
    )
    var showRefDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var barHeight by remember { mutableStateOf(0.dp) }
    Column(modifier = modifier) {
        BoxWithConstraints(modifier = modifier.fillMaxWidth()) { // The modifier here sets up the context for maxWidth
            val availableWidthPx =
                constraints.maxWidth // maxWidth from BoxWithConstraints in pixels

            val textLayoutResult: TextLayoutResult =
                remember(displayText, textStyle, density, availableWidthPx) {
                    textMeasurer.measure(
                        text = displayText,
                        style = textStyle,
                        constraints = Constraints(maxWidth = availableWidthPx) // Use the actual available width
                    )
                }

            val textHeight = with(density) { textLayoutResult.size.height.toDp() }
            barHeight = textHeight + (paddingValues * 2) // Add padding above and below text
        }
        CenterAlignedTopAppBar(
            modifier = Modifier.height(barHeight),
            title = { Text(
                text = displayText,
                fontSize = fontSize,
                style = textStyle,
//                modifier = Modifier
//                    .padding(
//                        Dimensions.textPadding
//                    )
                    )
                    },
            navigationIcon = {
                var navigateBackEvent by remember { mutableStateOf(false) }

                if (navigateBackEvent) {
                    LaunchedEffect(Unit) { // Use a constant key like Unit for one-shot
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            Log.w("TitleTopAppBar", "No previous back stack entry to pop.")
                            // Handle case where there's nothing to pop (e.g., close activity)
                            // (LocalContext.current as? Activity)?.finish()
                        }
                        navigateBackEvent = false // Reset the event
                    }
                }
                IconButton(
                    onClick = {
                        Log.d("TitleTopAppBar", "Navigation icon clicked.")
                        // Only trigger the event if not already processing or if there's something to pop
                        if (!navigateBackEvent && navController.previousBackStackEntry != null) {
                            navigateBackEvent = true
                        } else if (navController.previousBackStackEntry == null) {
                            Log.w("TitleTopAppBar", "Clicked, but nothing to pop.")
                            // Optionally, still trigger navigation to close activity if it's the root
                            // navigateBackEvent = true // If you want to handle closing activity via the LaunchedEffect
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description",
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                titleContentColor = MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.primary,
            ),
            actions = {
                // Help IconButton - will appear to the LEFT of the reference button
                if (helpMessageResId != null) {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "help"
                        )
                    }
                }
                if ((references[0].url != R.string.space && references[0].text != R.string.space)) {
                    IconButton(onClick = { showRefDialog = true }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.quick_reference_all_24px),
                            contentDescription = "Reference",
                        )
                    }
                }
            }
        )
        if (showRefDialog && (references[0].url != R.string.space && references[0].text != R.string.space)) {
            AlertDialog(
                onDismissRequest = { showRefDialog = false },
                title = {
                    Text(
                        text = stringResource(id = refTitleResId),
                        fontSize = 14.sp
                    )
                }, // "Help" or similar
                text = {
                    ReferenceListWithNumbers(references)
                },
                confirmButton = {
                    TextButton(onClick = { showRefDialog = false }) {
                        Text(stringResource(id = R.string.ok)) // "OK"
                    }
                }
            )
        }
        if (showHelpDialog && helpMessageResId != null) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = {
                    Text(
                        text = stringResource(id = helpTitleResId),
                        fontSize = 14.sp
                    )
                }, // "Help" or similar
                text = {
                    Text(
                        text = stringResource(id = helpMessageResId),
                        fontSize = 22.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text(stringResource(id = R.string.ok)) // "OK"
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun TitleTopAppBarVariablePreview() {
    TitleTopAppBarVariable(
        displayText = AnnotatedString("Mild \n bbbbbbbbb \n ccccc \n"),
        navController = NavController(LocalContext.current),
        references = listOf(
            TextAndUrl(R.string.mALBIRef, R.string.mALBIUrl),
            TextAndUrl(R.string.space, R.string.space)
        ),
        helpMessageResId = R.string.wilsonTitle // Example: <string name="preview_help_message">This is helpful information about the screen title.</string>

    )
}