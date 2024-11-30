package com.example.medguidelines.data

import com.example.medguidelines.R

data class labDataNames(val stringid: Int)

val bilirubinGrade = listOf(
    labDataNames(R.string.bilirubinGrade1),
    labDataNames(R.string.bilirubinGrade2),
    labDataNames(R.string.bilirubinGrade3),
)

val albuminGrade = listOf(
    labDataNames(R.string.albuminGrade1),
    labDataNames(R.string.albuminGrade2),
    labDataNames(R.string.albuminGrade3),

)

val ptGrade = listOf(
    labDataNames(R.string.ptGrade1),
    labDataNames(R.string.ptGrade2),
    labDataNames(R.string.ptGrade3),
)

val ascitesGrade = listOf(
    labDataNames(R.string.ascitesGrade1),
    labDataNames(R.string.ascitesGrade2),
    labDataNames(R.string.ascitesGrade3),
)

val encephalopathyGrade = listOf(
    labDataNames(R.string.encephalopathyGrade1),
    labDataNames(R.string.encephalopathyGrade2),
    labDataNames(R.string.encephalopathyGrade3),
)