package com.example.medguidelines.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
enum class ActionType {
    NAVIGATE_TO_CHILD_PUGH,
    NAVIGATE_TO_ADROP
}

@Parcelize
@Serializable
data class ListItemData(
    val nameResId: Int,
    val actionType: ActionType
) : Parcelable