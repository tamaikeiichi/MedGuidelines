package com.keiichi.medguidelines.ui.component

fun formatDouble(value: Double): String {
    val stringValue: String
    if (value == value.toInt().toDouble()) {
        stringValue = value.toInt().toString()
    } else {
        stringValue = value.toString()
    }
    return stringValue
}