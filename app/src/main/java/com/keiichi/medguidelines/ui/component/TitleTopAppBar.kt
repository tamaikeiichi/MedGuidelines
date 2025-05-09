package com.keiichi.medguidelines.ui.component

import androidx.activity.result.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.keiichi.medguidelines.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleTopAppBar(
    title: Int,
    navController: NavController,
    references: List<TextAndUrl>
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var lastClickTime by remember { mutableStateOf(0L) }
    val debounceTime = 300L // Adjust as needed
    CenterAlignedTopAppBar(
        title = { Text(text = parseStyledString(title)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime > debounceTime) {
                        lastClickTime = currentTime // Update the last click time
                        navController.popBackStack() // Perform the navigation
                    }
                },
//                modifier = Modifier
//                    .debouncedClick(coroutineScope = coroutineScope, onClick = {
//                    navController.popBackStack()
                //})
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
                            })
                    }
                }
            }
        }
    )
}

// Extension function for debounced clicks
//fun Modifier.singleClick(
//    onClick: () -> Unit,
//    debounceTime: Long = 300L // Adjust the debounce time as needed (milliseconds)
//): Modifier = composed {
//    var lastClickTime by remember { mutableStateOf(0L) }
//    clickable(
//        indication = null, // Or provide your desired indication
//        interactionSource = remember { MutableInteractionSource() }
//    ) {
//        val currentTime = System.currentTimeMillis()
//        if (currentTime - lastClickTime > debounceTime) {
//            lastClickTime = currentTime
//            onClick()
//        }
//    }
//}

// Custom modifier for debounced clicks using SharedFlow
fun Modifier.debouncedClick(
    coroutineScope: CoroutineScope,
    debounceTime: Long = 300L,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val clicks = remember { MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ) }

    LaunchedEffect(clicks) {
        clicks.debounce(debounceTime).collect {
            onClick()
        }
    }

    clickable(
        interactionSource = interactionSource,
        indication = null, // Or your desired indication
        onClick = {
            coroutineScope.launch {
                clicks.emit(Unit)
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