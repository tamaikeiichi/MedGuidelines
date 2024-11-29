package com.example.medguidelines.data

import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.example.medguidelines.R

data class IndexNames (val stringid: Int)

val indexnames = listOf(
    IndexNames(R.string.index1),
    IndexNames(R.string.index2),
    IndexNames(R.string.index1),
)