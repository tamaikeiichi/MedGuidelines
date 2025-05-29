package com.keiichi.medguidelines.data

import android.os.Parcelable
import androidx.annotation.StringRes
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
    NAVIGATE_TO_PANCREATITIS_TNM,
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

//@Parcelize
//@Serializable
//data class ListItemData(
//    @StringRes val nameResId: Int,
//    val actionType: ActionType,
//    var isFavorite: Boolean = false
//) : Parcelable

@Serializable // Only for kotlinx.serialization
data class ListItemData(
    @StringRes val nameResId: Int,
    val actionType: ActionType, // ActionType still needs @Serializable
    var isFavorite: Boolean = false
)