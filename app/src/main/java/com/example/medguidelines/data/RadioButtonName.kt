package com.example.medguidelines.data

import com.example.medguidelines.R

data class RadioButtonName(val stringId: Int)

val bilirubinGrade = listOf(
    RadioButtonName(R.string.bilirubinGrade1),
    RadioButtonName(R.string.bilirubinGrade2),
    RadioButtonName(R.string.bilirubinGrade3),
)

val albuminGrade = listOf(
    RadioButtonName(R.string.albuminGrade1),
    RadioButtonName(R.string.albuminGrade2),
    RadioButtonName(R.string.albuminGrade3),

)

val ptGrade = listOf(
    RadioButtonName(R.string.ptGrade1),
    RadioButtonName(R.string.ptGrade2),
    RadioButtonName(R.string.ptGrade3),
)

val ascitesGrade = listOf(
    RadioButtonName(R.string.ascitesGrade1),
    RadioButtonName(R.string.ascitesGrade2),
    RadioButtonName(R.string.ascitesGrade3),
)

val encephalopathyGrade = listOf(
    RadioButtonName(R.string.encephalopathyGrade1),
    RadioButtonName(R.string.encephalopathyGrade2),
    RadioButtonName(R.string.encephalopathyGrade3),
)

val Tfactor = listOf(
    RadioButtonName(R.string.Tis),
    RadioButtonName(R.string.T1),
    RadioButtonName(R.string.T2),
    RadioButtonName(R.string.T3),
    RadioButtonName(R.string.T4a),
    RadioButtonName(R.string.T4b),
)

val Nfactor = listOf(
    RadioButtonName(R.string.N0),
    RadioButtonName(R.string.N1a),
    RadioButtonName(R.string.N1b),
    RadioButtonName(R.string.N1c),
    RadioButtonName(R.string.N2a),
    RadioButtonName(R.string.N2b),

)

val Mfactor = listOf(
    RadioButtonName(R.string.M0),
    RadioButtonName(R.string.M1a),
    RadioButtonName(R.string.M1b),
    RadioButtonName(R.string.M1c)
)

val absencePresence = listOf(
    RadioButtonName(R.string.absence),
    RadioButtonName(R.string.presence),
)

val mclsaacAge = listOf(
    RadioButtonName(R.string.mclsaacAge1),
    RadioButtonName(R.string.mclsaacAge2),
    RadioButtonName(R.string.mclsaacAge3),
)

val tonsillitisRedFlag = listOf(
    RadioButtonName(R.string.none),
    RadioButtonName(R.string.tonsillitisRedFlag1),
    RadioButtonName(R.string.tonsillitisRedFlag2),
    RadioButtonName(R.string.tonsillitisRedFlag3),
    RadioButtonName(R.string.tonsillitisRedFlag4),
    RadioButtonName(R.string.tonsillitisRedFlag5),
    )

val noYes = listOf(
    RadioButtonName(R.string.no),
    RadioButtonName(R.string.yes),
)

val CTGradeInflammation = listOf(
    RadioButtonName(R.string.anteriorPararenalSpace),
    RadioButtonName(R.string.rootOfMesocolon),
    RadioButtonName(R.string.beyondTheInferiorRootOfKidney),
)

val CTGradePoorContrast = listOf(
    RadioButtonName(R.string.localizedToOneSegmentOrOnlyPeripancrease),
    RadioButtonName(R.string.lessThanTwoSegments),
    RadioButtonName(R.string.twoSengemtsOrMore),
)