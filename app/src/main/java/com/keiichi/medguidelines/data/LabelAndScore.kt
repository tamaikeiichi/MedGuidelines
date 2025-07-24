package com.keiichi.medguidelines.data

import androidx.annotation.StringRes
import com.keiichi.medguidelines.R

data class LabelAndScore(
    @StringRes val labelResId: Int,
    val score: Int
)


val  kayserFleischerRings = listOf(
    LabelAndScore(labelResId = R.string.present, score = 2),
    LabelAndScore(labelResId = R.string.absent, score = 0)
)

val neurologicSymptoms = listOf(
    LabelAndScore(labelResId = R.string.severe, score = 2),
    LabelAndScore(labelResId = R.string.mild, score = 1),
    LabelAndScore(labelResId = R.string.absent, score = 0)
)

val serumCeruloplasmin = listOf(
    LabelAndScore(labelResId = R.string.normal02, score = 0),
    LabelAndScore(labelResId = R.string.between0102, score = 1),
    LabelAndScore(labelResId = R.string.below01, score = 2)
)

val coombsNegativeHemolyticAnemia = listOf(
    LabelAndScore(labelResId = R.string.present, score = 1),
    LabelAndScore(labelResId = R.string.absent, score = 0)
)

val liverCopper = listOf(
    LabelAndScore(labelResId = R.string.above4, score = 2),
    LabelAndScore(labelResId = R.string.between084, score = 1),
    LabelAndScore(labelResId = R.string.normal08, score = -1),
    LabelAndScore(labelResId = R.string.rhodaminePositiveGranules, score = 1)
)

val urinaryCopper = listOf(
    LabelAndScore(labelResId = R.string.normal, score = 0),
    LabelAndScore(labelResId = R.string.between12, score = 1),
    LabelAndScore(labelResId = R.string.above2, score = 2),
    LabelAndScore(labelResId = R.string.above5, score = 2)
)

val mutationAnalysis = listOf(
    LabelAndScore(labelResId = R.string.bothChromosomes, score = 4),
    LabelAndScore(labelResId = R.string.oneChromosome, score = 1),
    LabelAndScore(labelResId = R.string.noMutation, score = 0)
)