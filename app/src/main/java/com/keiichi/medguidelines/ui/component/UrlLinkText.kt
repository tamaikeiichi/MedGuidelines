package com.keiichi.medguidelines.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource

data class TextAndUrl(
    val text: Int,
    val url: Int
)

@Composable
fun UrlLinkText(
    reference: TextAndUrl,
    urlHandler: UriHandler = LocalUriHandler.current
) {
    val url = stringResource(id = reference.url)
    Text(
        text = parseStyledString(reference.text),
        modifier = Modifier
            .clickable(enabled = true, onClickLabel = "Open URL") {
                urlHandler.openUri(url)
            }
    )
}