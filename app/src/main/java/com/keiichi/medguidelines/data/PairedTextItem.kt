package com.keiichi.medguidelines.data

import kotlinx.serialization.Serializable

@Serializable
data class PairedTextItem(
    val kanjiMeisho: String?,
    val tensuShikibetsu: String,
    val tensu: String,
    val kanaMeisho: String?,
    val originalIndex: Int,
    var isFavorite: Boolean = false,
    val kanjiText: String,
    val kanaText: String
)// : IndexableItem
