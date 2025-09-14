package com.keiichi.medguidelines.data

import androidx.annotation.StringRes
import androidx.compose.material3.Label
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
    LabelAndScore(labelResId = R.string.rhodaninePositiveGranules, score = 1)
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

val yesNoUnknown = listOf(
    LabelAndScore(labelResId = R.string.yes, score = 1),
    LabelAndScore(labelResId = R.string.no, score = 0),
    LabelAndScore(labelResId = R.string.unknown, score = 100)
)

val maleFemaleUnknown = listOf(
    LabelAndScore(labelResId = R.string.male, score = 1),
    LabelAndScore(labelResId = R.string.female, score = 0),
    LabelAndScore(labelResId = R.string.unknown, score = 100)
)
val aldreteExtremeties = listOf(
    LabelAndScore(labelResId = R.string.fourExtremities, score = 2),
    LabelAndScore(labelResId = R.string.twoExtremities, score = 1),
    LabelAndScore(labelResId = R.string.noExtremities, score = 0)
)
val aldreteRespiration = listOf(
    LabelAndScore(labelResId = R.string.breatheDeeply, score = 2),
    LabelAndScore(labelResId = R.string.dyspneaOrLimited, score = 1),
    LabelAndScore(labelResId = R.string.apneic, score = 0)
)
val aldreteCirculation = listOf(
    LabelAndScore(labelResId = R.string.twentyPercent, score = 2),
    LabelAndScore(labelResId = R.string.fortyninePercent, score = 1),
    LabelAndScore(labelResId = R.string.fiftyPercent, score = 0)
)
val aldreteConsciousness = listOf(
    LabelAndScore(labelResId = R.string.fullyAwake, score = 2),
    LabelAndScore(labelResId = R.string.arousableOnCalling, score = 1),
    LabelAndScore(labelResId = R.string.noResponding, score = 0)
)
val aldreteSaturation = listOf(
    LabelAndScore(labelResId = R.string.roomAir, score = 2),
    LabelAndScore(labelResId = R.string.aboveNinety, score = 1),
    LabelAndScore(labelResId = R.string.belowNinety, score = 0)
)