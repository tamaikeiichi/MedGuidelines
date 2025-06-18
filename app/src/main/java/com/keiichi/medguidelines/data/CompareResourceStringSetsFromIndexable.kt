package com.keiichi.medguidelines.data

import android.content.Context
import android.content.res.Resources
import com.keiichi.medguidelines.data.IndexableItem // Import the interface

/**
 * Compares two lists of IndexableItem based on strings derived from their
 * 'itemIdentifier' property, which is used to look up a string resource.
 *
 * @param T The type of items in the lists, must implement IndexableItem.
 * @param context The Android Context.
 * @param listA The first list of items.
 * @param listB The second list of items.
 * @return True if the sets of resolved string resources are identical, false otherwise.
 */
fun <T : IndexableItem> compareResourceStringSetsFromIndexable( // T is constrained to IndexableItem
    context: Context,
    listA: List<T>,
    listB: List<T>
): Boolean {
    // No need for idExtractor anymore, as T is guaranteed to have 'itemIdentifier'
    val stringsFromListA = listA.mapNotNull { item ->
        try {
            context.getString(item.itemIdentifier) // Directly access item.itemIdentifier
        } catch (e: Resources.NotFoundException) {
            println("Warning: String resource not found for ID ${item.itemIdentifier} in listA. Item: $item")
            null
        } catch (e: Exception) {
            println("Error resolving string for ID ${item.itemIdentifier} in listA. Item: $item. Error: ${e.message}")
            null
        }
    }.toSet()

    val stringsFromListB = listB.mapNotNull { item ->
        try {
            context.getString(item.itemIdentifier) // Directly access item.itemIdentifier
        } catch (e: Resources.NotFoundException) {
            println("Warning: String resource not found for ID ${item.itemIdentifier} in listB. Item: $item")
            null
        } catch (e: Exception) {
            println("Error resolving string for ID ${item.itemIdentifier} in listB. Item: $item. Error: ${e.message}")
            null
        }
    }.toSet()

    return stringsFromListA == stringsFromListB
}