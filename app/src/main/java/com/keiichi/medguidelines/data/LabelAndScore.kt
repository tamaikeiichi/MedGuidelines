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

val sleEntryCriterion = listOf(
    LabelAndScore(labelResId = R.string.eightyOrMore, score = 1),
    LabelAndScore(labelResId = R.string.lessThanEighty, score = 0)
)

val constitutional = listOf(
    LabelAndScore(labelResId = R.string.fever, score = 2),
    LabelAndScore(labelResId = R.string.none, score = 0),

)

val hematologic = listOf(
    LabelAndScore(labelResId = R.string.leukopenia, score = 3),
    LabelAndScore(labelResId = R.string.thrombocytopenia, score = 4),
    LabelAndScore(labelResId = R.string.autoimmuneHemolysis, score = 4),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val neuropsychiatric = listOf(
    LabelAndScore(labelResId = R.string.delirium, score = 2),
    LabelAndScore(labelResId = R.string.psychosis, score = 3),
    LabelAndScore(labelResId = R.string.seizure, score = 5),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val mucocutaneus = listOf(
    LabelAndScore(labelResId = R.string.nonScarringAlopecia, score = 2),
    LabelAndScore(labelResId = R.string.oralUlcers, score = 2),
    LabelAndScore(labelResId = R.string.subacuteCutaneousOrDscoidLupus, score = 4),
    LabelAndScore(labelResId = R.string.acuteCutaneousLupus, score = 6),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val serosal = listOf(
    LabelAndScore(labelResId = R.string.pleuralOrPericardialEffusion, score = 5),
    LabelAndScore(labelResId = R.string.acutePericarditis, score = 6),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val musculoskeletal = listOf(
    LabelAndScore(labelResId = R.string.jointInvolvement, score = 6),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val renal = listOf(
    LabelAndScore(labelResId = R.string.proteinuria, score = 4),
    LabelAndScore(labelResId = R.string.renalBiopsyClassIIorV, score = 8),
    LabelAndScore(labelResId = R.string.renalBiopsyClassIIIorIV, score = 10),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val antiphospholipidAntibodies = listOf(
    LabelAndScore(labelResId = R.string.antiphospholipidAntibodies, score = 2),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val complementProtiens = listOf(
    LabelAndScore(labelResId = R.string.lowC3OrC4, score = 3),
    LabelAndScore(labelResId = R.string.lowC3AndC4, score = 4),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val sleSpecificAntibodies = listOf(
    LabelAndScore(labelResId = R.string.sleSpecificAntibodies, score = 6),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val raEntryCriterion = listOf(
    LabelAndScore(labelResId = R.string.atLeastOneJoint, score = 1),
    LabelAndScore(labelResId = R.string.noBetterExplained, score = 1)
)

val jointInvolvement = listOf(
    LabelAndScore(labelResId = R.string.oneLargeJoint, score = 0),
    LabelAndScore(labelResId = R.string.twoToTenLargeJoints, score = 1),
    LabelAndScore(labelResId = R.string.oneToThreeSmallJoints, score = 2),
    LabelAndScore(labelResId = R.string.fourOrTenSmallJoints, score = 3),
    LabelAndScore(labelResId = R.string.moreThanTenJoints, score = 5),
    LabelAndScore(labelResId = R.string.none, score = 0)
)

val serology = listOf(
    LabelAndScore(labelResId = R.string.negativeRfAndNegativeAcpa, score = 0),
    LabelAndScore(labelResId = R.string.lowPositiveRfOrLowPositiveAcpa, score = 2),
    LabelAndScore(labelResId = R.string.highPositiveRfOrHighPositiveAcpa, score = 4)
)

val acutePhaseReactants = listOf(
    LabelAndScore(labelResId = R.string.normalCrpAndNormalEsr, score = 0),
    LabelAndScore(labelResId = R.string.abnormlaCrpOrNormalEsr, score = 1),
)

val durationOfSymptoms = listOf(
    LabelAndScore(labelResId = R.string.lessThanSixWeeks, score = 0),
    LabelAndScore(labelResId = R.string.sixWeeksOrMore, score = 1),
)

val sofaRespiration = listOf(
    LabelAndScore(labelResId = R.string.fourHundredOrMore, score = 0),
    LabelAndScore(labelResId = R.string.belowFourHundred, score = 1),
    LabelAndScore(labelResId = R.string.belowThreeHundred, score = 2),
    LabelAndScore(labelResId = R.string.belowTwoHundred, score = 3),
    LabelAndScore(labelResId = R.string.belowOneHundredWithRespiratorySupport, score = 4),
)

val sofaCoagulation = listOf(
    LabelAndScore(labelResId = R.string.oneHundredAndFiftyOrMore, score = 0),
    LabelAndScore(labelResId = R.string.belowOneHundredAndFifty, score = 1),
    LabelAndScore(labelResId = R.string.belowOneHundred, score = 2),
    LabelAndScore(labelResId = R.string.belowFifty, score = 3),
    LabelAndScore(labelResId = R.string.belowTwenty, score = 4)
)

val sofaLiver = listOf(
    LabelAndScore(labelResId = R.string.below12, score = 0),
    LabelAndScore(labelResId = R.string.between1219, score = 1),
    LabelAndScore(labelResId = R.string.between2059, score = 2),
    LabelAndScore(labelResId = R.string.between60119, score = 3),
    LabelAndScore(labelResId = R.string.above120, score = 4)
)

val sofaCardiovascular = listOf(
    LabelAndScore(labelResId = R.string.seventyOrMore, score = 0),
    LabelAndScore(labelResId = R.string.belowSeventy, score = 1),
    LabelAndScore(labelResId = R.string.dopamine5, score = 2),
    LabelAndScore(labelResId = R.string.dopamine5115, score = 3),
    LabelAndScore(labelResId = R.string.dopamine15, score = 4)
)

val sofaCentralNervousSystem = listOf(
    LabelAndScore(labelResId = R.string.fifteen, score = 0),
    LabelAndScore(labelResId = R.string.between1314, score = 1),
    LabelAndScore(labelResId = R.string.between1012, score = 2),
    LabelAndScore(labelResId = R.string.between69, score = 3),
    LabelAndScore(labelResId = R.string.below6, score = 4)
)

val sofaRenal = listOf(
    LabelAndScore(labelResId = R.string.below12Sofa, score = 0),
    LabelAndScore(labelResId = R.string.between1219Sofa, score = 1),
    LabelAndScore(labelResId = R.string.between2034, score = 2),
    LabelAndScore(labelResId = R.string.between3549, score = 3),
    LabelAndScore(labelResId = R.string.above5Sofa, score = 4)
)

val qSofa = listOf(
    LabelAndScore(labelResId = R.string.qSofaRespiratoryRate, score = 1),
    LabelAndScore(labelResId = R.string.qSofaAlteredMentation, score = 1),
    LabelAndScore(labelResId = R.string.qSofaSystolicBloodPressure, score = 1)
)

val eyeGrade = listOf(
    LabelAndScore(labelResId = R.string.openspontaneously, score = 4),
    LabelAndScore(labelResId = R.string.opentoverbalcommand, score = 3),
    LabelAndScore(labelResId = R.string.openinresponsetopainappliedtothelimbsorsternum,score = 2),
    LabelAndScore(labelResId = R.string.none,score = 1)
)

val verbalGrade = listOf(
    LabelAndScore(labelResId = R.string.oriented, score= 5),
    LabelAndScore(labelResId = R.string.disorientedbutabletoanswerquestions, score = 4),
    LabelAndScore(labelResId = R.string.inappropriateanswerstoquestionswordsdiscernible, score = 3),
    LabelAndScore(labelResId = R.string.incomprehensiblespeech, score = 2),
    LabelAndScore(labelResId = R.string.none, score = 1)
)

val motorGrade = listOf(
    LabelAndScore(labelResId = R.string.obeyscommands, score= 6),
    LabelAndScore(labelResId = R.string.respondstopainwithpurposefulmovement, score = 5),
    LabelAndScore(labelResId = R.string.withdrawsfrompainstimuli, score =4),
    LabelAndScore(labelResId = R.string.respondstopainwithabnormalflexiondecorticateposture, score = 3),
    LabelAndScore(labelResId = R.string.respondstopainwithabnormalrigidextensiondecerebrateposture, score = 2),
    LabelAndScore(labelResId = R.string.none, score =1)
)