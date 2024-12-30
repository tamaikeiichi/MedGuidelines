package com.example.medguidelines.data

data class UiccTnmIndex(val tnm: String)

val Tfactor = listOf(
    UiccTnmIndex("Tis"),
    UiccTnmIndex("T1"),
    UiccTnmIndex("T2"),
    UiccTnmIndex("T3"),
    UiccTnmIndex("T4a"),
    UiccTnmIndex("T4b")
)
val NMfactor = listOf(
    UiccTnmIndex("N0"),
    UiccTnmIndex("N1a"),
    UiccTnmIndex("N1b"),
    UiccTnmIndex("N1c"),
    UiccTnmIndex("N2a"),
    UiccTnmIndex("N2b"),
    UiccTnmIndex("M1a"),
    UiccTnmIndex("M1b"),
    UiccTnmIndex("M1c")
)

