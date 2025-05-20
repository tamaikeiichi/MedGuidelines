package com.keiichi.medguidelines.data

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
    //      Tis,     T1a,   T1b,     T1c    , T2,    T3,    T4
    listOf("0", "IA", "IA", "IA", "IB", "IIA", "III"), //N0
    listOf("N/A", "IIB", "IIB", "IIB", "IIB", "IIB", "III"),//N1
    listOf("N/A", "III", "III", "III", "III", "III", "III"),//N2
    listOf("N/A", "IV", "IV", "IV", "IV", "IV", "IV"),//M1
)

val esophagealCancerTNM = listOf(
    //      Tis,     T1a,   T1b,   T2,     T3    , T4a,    T4b
    listOf("0", "I", "I", "II", "II", "IVA", "IVA"), //N0
    listOf("N/A", "I", "I", "II", "III", "IVA", "IVA"),//N1
    listOf("N/A", "III", "III", "III", "III", "IVA", "IVA"),//N2
    listOf("N/A", "IVA", "IVA", "IVA", "IVA", "IVA", "IVA"),//N3
    listOf("N/A", "IVB", "IVB", "IVB", "IVB", "IVB", "IVB"),//M1
)

val lungCancerTNM = listOf(
    //      Tis,     T1a,   T1b,    T1c,    T2a,    T2b,    T3,     T4
    listOf("0", "IA1", "IA2", "IA3", "IB", "IIA", "IIB", "IIIA"),//N0
    listOf("N/A", "IIA", "IIA", "IIA", "IIB", "IIB", "IIIA", "IIIA"),//N1
    listOf("N/A", "IIB", "IIB", "IIB", "IIIA", "IIIA", "IIIA", "IIIB"),//N2a
    listOf("N/A", "IIIA", "IIIA", "IIIA", "IIIB", "IIIB", "IIIB", "IIIB"),//N2b
    listOf("N/A", "IIIB", "IIIB", "IIIB", "IIIB", "IIIB", "IIIC", "IIIC"),//N2c
    listOf("N/A", "IVA", "IVA", "IVA", "IVA", "IVA", "IVA", "IVA"),//M1a
    listOf("N/A", "IVA", "IVA", "IVA", "IVA", "IVA", "IVA", "IVA"),//M1b
    listOf("N/A", "IVB", "IVB", "IVB", "IVB", "IVB", "IVB", "IVB"),//M1c
)

val hccTNM = listOf(
    //       T1a,   T1b,    T2,      T3,     T4
    listOf("IA", "IB", "II", "IIIA", "IIIB"),//N0
    listOf("IVA", "IVA", "IVA", "IVA", "IVA"),//N1
    listOf("IVB", "IVB", "IVB", "IVB", "IVB"),//M1
)

val intrahepaticCholangiocarcinomaTNM = listOf(
    //      Tis,  T1a, T1b, T2,  T3,     T4
    listOf("0", "IA", "IB", "II", "IIIA", "IIIB"), //N0
    listOf("N/A", "IIIB", "IIIB", "IIIB", "IIIB", "IIIB"),//N1
    listOf("N/A", "IV", "IV", "IV", "IV", "IV"),//M1
)
