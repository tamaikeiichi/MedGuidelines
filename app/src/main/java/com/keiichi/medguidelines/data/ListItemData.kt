package com.keiichi.medguidelines.data

import android.os.Parcelable
import androidx.annotation.StringRes
import com.keiichi.medguidelines.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
enum class ActionType {
    NAVIGATE_TO_CHILD_PUGH,
    NAVIGATE_TO_ADROP,
    NAVIGATE_TO_COLORECTAL_TNM,
    NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM,
    NAVIGATE_TO_BLOOD_GAS_ANALYSIS,
    NAVIGATE_TO_ACUTE_PANCREATITIS,
    NAVIGATE_TO_NETAKIRIDO,
    NAVIGATE_TO_PANCREASE_TNM,
    NAVIGATE_TO_ESOPAGEAL_TNM,
    NAVIGATE_TO_MALBI,
    NAVIGATE_TO_LIVERFIBROSISSCORESYSTEM,
    NAVIGATE_TO_HOMAIR,
    NAVIGATE_TO_LUNG_TNM,
    NAVIGATE_TO_HCC_TNM,
    NAVIGATE_TO_INTRAHEPATICCHOLANGIOCARCINOMA_TNM,
    NAVIGATE_TO_CHADS2,
    NAVIGATE_TO_GLASGOW_COMA_SCALE,
    NAVIGATE_TO_SODIUM_DIFFERENTIAL_DIAGNOSIS
}

@Serializable // Only for kotlinx.serialization
data class ListItemData(
    @StringRes val nameResId: Int,
    val actionType: ActionType, // ActionType still needs @Serializable
    var isFavorite: Boolean = false,
    var keywords: List<Int> = emptyList()
)

val itemsList = listOf(
    ListItemData(R.string.childPughTitle, ActionType.NAVIGATE_TO_CHILD_PUGH),
    ListItemData(R.string.aDropTitle, ActionType.NAVIGATE_TO_ADROP,
        keywords = listOf(
            R.string.lung,
            R.string.pneumonia
        ),
    ),
    ListItemData(R.string.colorectalTNMTitle, ActionType.NAVIGATE_TO_COLORECTAL_TNM),
    ListItemData(
        R.string.acuteTonsillitisAlgorithmTitle,
        ActionType.NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM
    ),
    ListItemData(R.string.bloodGasAnalysisTitle, ActionType.NAVIGATE_TO_BLOOD_GAS_ANALYSIS),
    ListItemData(R.string.acutePancreatitisTitle, ActionType.NAVIGATE_TO_ACUTE_PANCREATITIS),
    ListItemData(R.string.netakiridoTitle, ActionType.NAVIGATE_TO_NETAKIRIDO),
    ListItemData(R.string.pancreaticTNMTitle, ActionType.NAVIGATE_TO_PANCREASE_TNM),
    ListItemData(R.string.esophagealTNMTitle, ActionType.NAVIGATE_TO_ESOPAGEAL_TNM),
    ListItemData(R.string.mALBITitle, ActionType.NAVIGATE_TO_MALBI,
        keywords = listOf(
            R.string.liver
        ),
    ),
    ListItemData(
        nameResId = R.string.liverFibrosisScoreSystemTitle,
        actionType = ActionType.NAVIGATE_TO_LIVERFIBROSISSCORESYSTEM,
        keywords = listOf(
            R.string.fib4,
            R.string.nafldFibrosisScore,
            R.string.elfScore,
            R.string.apri,
            R.string.m2bpgi,
            R.string.caIndex,
            R.string.shearWaveElastography
        )
    ),
    ListItemData(R.string.homaIrHomaBetaTitle, ActionType.NAVIGATE_TO_HOMAIR),
    ListItemData(R.string.lungTNMTitle, ActionType.NAVIGATE_TO_LUNG_TNM),
    ListItemData(R.string.hccTNMTitle, ActionType.NAVIGATE_TO_HCC_TNM),
    ListItemData(
        R.string.intrahepaticCholangiocarcinomaTNMTitle,
        ActionType.NAVIGATE_TO_INTRAHEPATICCHOLANGIOCARCINOMA_TNM
    ),
    ListItemData(R.string.chads2AndHelte2s2Title, ActionType.NAVIGATE_TO_CHADS2,
        keywords = listOf(
            R.string.af,
            R.string.stroke
        )
    ),
    ListItemData(R.string.glasgowComaScaleTitle, ActionType.NAVIGATE_TO_GLASGOW_COMA_SCALE),
    ListItemData(
        R.string.sodiumDifferentialDiagnosisTitle,
        ActionType.NAVIGATE_TO_SODIUM_DIFFERENTIAL_DIAGNOSIS
    ),
)