package com.keiichi.medguidelines.ui.component

import android.content.Context
import com.keiichi.medguidelines.data.PairedTextItem

fun doAnyDecodeStringsMatchAnyPairedItemsStrings(
    context: Context,
    decodeList: List<PairedTextItem>,
    itemsList: List<PairedTextItem>
): Boolean {
    // Get the set of strings from decodeList.nameResID (Set A)
    val decodeNameResIdStrings = decodeList.mapNotNull { listItem ->
        try {
            context.getString(listItem.originalIndex)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in decodeList: ${listItem.originalIndex}")
            null // Exclude this string from the set
        }
    }.toSet()
    // Get the set of strings from itemsList.nameResID (Set B)
    val itemNameResIdStrings = itemsList.mapNotNull { listItem ->
        try {
            context.getString(listItem.originalIndex)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in itemsList: ${listItem.originalIndex}")
            null // Exclude this string from the set
        }
    }.toSet()
    // Check if there is any intersection between the two sets of strings
    val checkName = decodeNameResIdStrings == itemNameResIdStrings
    //val checkKeywords = decodeKeywordStrings == itemKeywordStrings
    return checkName //&& checkKeywords
}