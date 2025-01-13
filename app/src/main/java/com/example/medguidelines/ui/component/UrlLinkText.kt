package com.example.medguidelines.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource

@Composable
fun UrlLinkText(
    referenceUrl: Int,
    referenceText: Int,
    urlHandler: UriHandler = LocalUriHandler.current
) {
    val url = stringResource(id = referenceUrl)
    Text(
        text = parseStyledString(referenceText) ,
        modifier = Modifier
            .clickable(enabled = true, onClickLabel = "Open URL") {
                urlHandler.openUri(url)
            }
    )
}