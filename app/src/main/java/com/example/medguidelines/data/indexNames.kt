package com.example.medguidelines.data

import com.example.medguidelines.R

data class IndexNames (val stringid: Int)

val indexNames = listOf(
    IndexNames(R.string.childPughTitle),
    IndexNames(R.string.aDropTitle),
    IndexNames(R.string.childPughTitle),
)

data class  IndexTitleAndDestination (val stringId: Int, val onClick: () -> Unit)

val indexTitleAndDestination = listOf(
    IndexTitleAndDestination(stringId = (R.string.childPughTitle), onClick = {navigateToChildPugh()}),
    IndexTitleAndDestination(stringId = (R.string.aDropTitle), onClick = {navigateToAdrop()})
)
