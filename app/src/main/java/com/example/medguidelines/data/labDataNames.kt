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

val ageGrade = listOf(
    labDataNames(R.string.ageGrade1),
    labDataNames(R.string.ageGrade2),
)
val dehydrationGrade = listOf(
    labDataNames(R.string.dehydrationGrade1),
    labDataNames(R.string.dehydrationGrade2),
    )
val respirationGrade = listOf(
    labDataNames(R.string.respirationGrade1),
    labDataNames(R.string.respirationGrade2),
    )
val orientationGrade = listOf(
    labDataNames(R.string.orientationGrade1),
    labDataNames(R.string.orientationGrade2)
)
val pressureGrade = listOf(
    labDataNames(R.string.pressureGrade1),
    labDataNames(R.string.pressureGrade2)
)

val Tfactor = listOf(
    labDataNames(R.string.Tis),
    labDataNames(R.string.T1),
    labDataNames(R.string.T2),
    labDataNames(R.string.T3),
    labDataNames(R.string.T4a),
    labDataNames(R.string.T4b),
)

val Nfactor = listOf(
    labDataNames(R.string.N0),
    labDataNames(R.string.N1a),
    labDataNames(R.string.N1b),
    labDataNames(R.string.N1c),
    labDataNames(R.string.N2a),
    labDataNames(R.string.N2b),

)

val Mfactor = listOf(
    labDataNames(R.string.M0),
    labDataNames(R.string.M1a),
    labDataNames(R.string.M1b),
    labDataNames(R.string.M1c)
)