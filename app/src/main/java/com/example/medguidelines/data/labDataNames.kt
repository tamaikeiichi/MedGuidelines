package com.example.medguidelines.data

import android.provider.Settings.Global.getString
import androidx.compose.ui.res.stringResource
import com.example.medguidelines.R

data class labDataNames(val stringid: Int)

val ascitesgrade = listOf(
    labDataNames(R.string.ascitesgrade1),
    labDataNames(R.string.ascitesgrade2),
    labDataNames(R.string.ascitesgrade3),
)

val encephalophathygrade = listOf(
    labDataNames(R.string.encephalopathygrade1),
    labDataNames(R.string.encephalopathygrade2),
    labDataNames(R.string.encephalopathygrade3),
)