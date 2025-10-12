package com.keiichi.medguidelines.ui.component

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keiichi.medguidelines.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopAppBar(
    title: Int,
    navController: NavController,
    references: List<TextAndUrl>,
    @StringRes helpTitleResId: Int = R.string.about, // Default help title
    @StringRes helpMessageResId: Int? = null, // New: Optional help message string resource
    @StringRes refTitleResId: Int = R.string.reference, // Default reference title
) {
    var showRefDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { Text(text = parseStyledString(title)) },
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
//                IconButton(onClick = { expanded = !expanded }) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(id = R.drawable.quick_reference_all_24px),
//                        contentDescription = "Reference",
//                    )
//                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                        DropdownMenuItem(
//                            text = {
//                                Column() {
//                                    for (reference in references) {
//                                        UrlLinkText(reference)
//                                    }
//                                }
//                            },
//                            onClick = {
//                                expanded = false
//                            }
//                        )
//                    }
//                }
            }
        }
    )
    if (showRefDialog && (references[0].url != R.string.space && references[0].text != R.string.space)){
        AlertDialog(
            onDismissRequest = { showRefDialog = false },
            title = { Text(text = stringResource(id = refTitleResId),
                fontSize = 14.sp
            ) }, // "Help" or similar
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
    // Help Dialog
    if (showHelpDialog && helpMessageResId != null) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(text = stringResource(id = helpTitleResId),
                fontSize = 14.sp
            ) }, // "Help" or similar
            text = { Text(text = stringResource(id = helpMessageResId),
                fontSize = 22.sp) },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(stringResource(id = R.string.ok)) // "OK"
                }
            }
        )
    }
}


//@Composable
//fun ReferenceListWithNumbers(references: List<TextAndUrl>) {
//    Column (
//        verticalArrangement = Arrangement.spacedBy(5.dp)
//    ){
//        if (references.isEmpty() || (references.size == 1 && references[0].text == R.string.space && references[0].url == R.string.space)) {
//            // Handle empty or placeholder references if needed, e.g., show a message
//            Text("No references available.")
//        } else {
//            references.forEachIndexed { index, reference ->
//                // Filter out placeholder "space" references if they shouldn't be numbered or displayed
//                if (reference.text != R.string.space || reference.url != R.string.space) {
//                    Row(verticalAlignment = Alignment.Top) { // Align number and text to the top
//                        Text(text = "${index + 1}) ") // Add 1 to index because it's 0-based
//                        UrlLinkText(reference)
//                    }
//                }
//            }
//        }
//    }
//}

// Dummy UrlLinkText for preview if not available
//@Composable
//fun UrlLinkText(textAndUrl: TextAndUrl) {
//    Text(text = "Link: ${stringResource(id = textAndUrl.text)} -> ${stringResource(id = textAndUrl.url)}")
//}

// Dummy TextAndUrl for preview if not available
//data class TextAndUrl(@StringRes val text: Int, @StringRes val url: Int)


@Preview
@Composable
fun TitleTopAppBarPreview() {
    TitleTopAppBar(
        title = R.string.space,
        navController = NavController(LocalContext.current),
        references = listOf(
            TextAndUrl(R.string.mALBIRef, R.string.mALBIUrl),
            TextAndUrl(R.string.space, R.string.space)
        ),
        helpMessageResId = R.string.wilsonTitle // Example: <string name="preview_help_message">This is helpful information about the screen title.</string>

    )
}