package com.keiichi.medguidelines.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopAppBar(
    title: Int,
    navController: NavController,
    references: List<TextAndUrl>
) {
    var expanded by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }
    val debounceTime = 300L // Adjust as needed
    var alreadyClicked = false
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
//                    val currentTime = System.currentTimeMillis()
//
//                    if (
//                        //currentTime - lastClickTime > debounceTime &&
//                    !alreadyClicked) {
//                        //lastClickTime = currentTime // Update the last click time
//                        navController.popBackStack() // Perform the navigation
//                        alreadyClicked = true
//                    }
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
            if (references[0].url != R.string.space && references[0].text != R.string.space) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.quick_reference_all_24px),
                        contentDescription = "Reference",
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = {
                                Column() {
                                    for (reference in references) {
                                        UrlLinkText(reference)
                                    }
                                }
                            },
                            onClick = {
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun TitleTopAppBarPreview() {
    TitleTopAppBar(
        title = R.string.space,
        navController = NavController(LocalContext.current),
        references = listOf(
            TextAndUrl(R.string.mALBIRef, R.string.mALBIUrl),
            TextAndUrl(R.string.space, R.string.space)
        )
    )
}