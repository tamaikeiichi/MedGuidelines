package com.example.medguidelines.data

data class UiccTnm(val stage: String)

val colorectalCancerTNM = listOf(
    //Tis, T1, T2, T3, T4a, T4b
    listOf("0", "I", "I", "IIA", "IIB", "IIC"), //N0
    listOf("N/A", "IIIA", "IIIA", "IIIB", "IIIB", "IIIC"),//N1a
    listOf("N/A", "IIIA", "IIIA", "IIIB", "IIIB", "IIIC"),//N1b
    listOf("N/A", "IIIA", "IIIA", "IIIB", "IIIB", "IIIC"),//N1c
    listOf("N/A", "IIIA", "IIIB", "IIIB", "IIIC", "IIIC"),//N2a
    listOf("N/A", "IIIB", "IIIB", "IIIC", "IIIC", "IIIC"),//N2b
    listOf("N/A", "IVA", "IVA", "IVA", "IVA", "IVA"),//M1a
    listOf("N/A", "IVB", "IVB", "IVB", "IVB", "IVB"),//M1b
    listOf("N/A", "IVC", "IVC", "IVC", "IVC", "IVC"),//M1c
    )

val pancreaticCancerTNM = listOf(
    //Tis, T1a, T1b, T1c, T2, T3, T4
    listOf("0", "I", "I", "IIA", "IIB", "IIC"), //N0
    listOf("N/A", "IIIA", "IIIA", "IIIB", "IIIB", "IIIC"),//N1
    listOf("N/A", "IIIA", "IIIA", "IIIB", "IIIB", "IIIC"),//N2
    listOf("N/A", "IVA", "IVA", "IVA", "IVA", "IVA"),//M1
)