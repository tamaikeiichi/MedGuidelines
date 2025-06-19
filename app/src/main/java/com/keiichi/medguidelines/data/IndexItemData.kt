package com.keiichi.medguidelines.data

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import com.keiichi.medguidelines.ScreenRoute // Make sure this is imported (from MainActivity or its new file)
import com.keiichi.medguidelines.ui.screen.AcutePancreatitisScreen
import com.keiichi.medguidelines.ui.screen.AcuteTonsillitisAlgorithmScreen
import com.keiichi.medguidelines.ui.screen.AdropScreen
import com.keiichi.medguidelines.ui.screen.BloodGasAnalysisScreen
import com.keiichi.medguidelines.ui.screen.Chads2Screen
import com.keiichi.medguidelines.ui.screen.ChildPughScreen
import com.keiichi.medguidelines.ui.screen.ColorectalTNMScreen
import com.keiichi.medguidelines.ui.screen.EsophagealTNMScreen
import com.keiichi.medguidelines.ui.screen.GlasgowComaScaleScreen
import com.keiichi.medguidelines.ui.screen.HCCTNMScreen
import com.keiichi.medguidelines.ui.screen.HomaIRScreen
import com.keiichi.medguidelines.ui.screen.IntrahepaticCholangiocarcinomaTNMScreen
import com.keiichi.medguidelines.ui.screen.LiverFibrosisScoreSystemScreen
import com.keiichi.medguidelines.ui.screen.LungTNMScreen
import com.keiichi.medguidelines.ui.screen.MALBIScreen
import com.keiichi.medguidelines.ui.screen.NetakiridoScreen
import com.keiichi.medguidelines.ui.screen.PancreaticTNMScreen
import com.keiichi.medguidelines.ui.screen.SodiumDifferentialDiagnosisScreen
import com.keiichi.medguidelines.ui.screen.LilleModelScreen
import com.keiichi.medguidelines.ui.screen.EcogScreen
import com.keiichi.medguidelines.ui.screen.GammaCalculateScreen
import com.keiichi.medguidelines.ui.screen.Icd10EnglishScreen
import com.keiichi.medguidelines.ui.screen.IkaShinryokoiMasterScreen

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
    NAVIGATE_TO_SODIUM_DIFFERENTIAL_DIAGNOSIS,
    NAVIGATE_TO_LILLE_MODEL,
    NAVIGATE_TO_ECOG,
    NAVIGATE_TO_INFUSION_CALCULATOR,
    NAVIGATE_TO_IKASHINRYOKOIMASTER,
    NAVIGATE_TO_ICD10,
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
    ListItemData(
        R.string.aDropTitle, ActionType.NAVIGATE_TO_ADROP,
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
    ListItemData(
        R.string.mALBITitle, ActionType.NAVIGATE_TO_MALBI,
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
    ListItemData(R.string.lilleModelInr, ActionType.NAVIGATE_TO_LILLE_MODEL,
        keywords = listOf(
            R.string.alcoholicHepatitis,
        )
    ),
    ListItemData(R.string.ecogPerformanceStatus, ActionType.NAVIGATE_TO_ECOG,
        keywords = listOf(
            R.string.Ps,
        )),
    ListItemData(R.string.infusionCalculator, ActionType.NAVIGATE_TO_INFUSION_CALCULATOR),
    ListItemData(R.string.ikashiRinryokuMasterKensaku, ActionType.NAVIGATE_TO_IKASHINRYOKOIMASTER),
    ListItemData(R.string.icd10, ActionType.NAVIGATE_TO_ICD10),
)

data class IndexScreenActions(
    val navigateToChildPugh: () -> Unit,
    val navigateToAdrop: () -> Unit,
    val navigateToColorectalTNM: () -> Unit,
    val navigateToAcuteTonsillitisAlgorithm: () -> Unit,
    val navigateToBloodGasAnalysis: () -> Unit,
    val navigateToAcutePancreatitis: () -> Unit,
    val navigateToNetakirido: () -> Unit,
    val navigateToPancreaticTNM: () -> Unit,
    val navigateToEsophagealTNM: () -> Unit,
    val navigateToMALBI: () -> Unit,
    val navigateToLiverFibrosisScoreSystem: () -> Unit,
    val navigateToHomaIR: () -> Unit,
    val navigateToLungTNM: () -> Unit,
    val navigateToHccTNM: () -> Unit,
    val navigateToIntrahepaticCholangiocarcinomaTNM: () -> Unit,
    val navigateToCHADS2: () -> Unit,
    val navigateToGlasgowComaScale: () -> Unit,
    val navigateToSodiumDifferentialDiagnosis: () -> Unit,
    val navigateToLilleModel: () -> Unit,
    val navigateToEcog: () -> Unit,
    val navigateToInfusionCalculator: () -> Unit,
    val navigateToIkaShinryokoiMaster: () -> Unit,
    val navigateToIcd10: () -> Unit,
)

@Composable
fun rememberIndexScreenActions(navController: NavHostController): IndexScreenActions {
    return IndexScreenActions(
        navigateToChildPugh = { navController.navigate("ChildPughScreen") },
        navigateToAdrop = { navController.navigate("AdropScreen") },
        navigateToColorectalTNM = { navController.navigate("ColorectalTNMScreen") },
        navigateToAcuteTonsillitisAlgorithm = {
            navController.navigate("AcuteTonsillitisAlgorithmScreen")
        },
        navigateToBloodGasAnalysis = { navController.navigate("BloodGasAnalysisScreen") },
        navigateToAcutePancreatitis = { navController.navigate("AcutePancreatitisScreen") },
        navigateToNetakirido = { navController.navigate("NetakiridoScreen") },
        navigateToPancreaticTNM = { navController.navigate("PancreaticTNMScreen") },
        navigateToEsophagealTNM = { navController.navigate("EsophagealTNMScreen") },
        navigateToMALBI = { navController.navigate("MALBIScreen") },
        navigateToLiverFibrosisScoreSystem = { navController.navigate("LiverFibrosisScoreSystemScreen") },
        navigateToHomaIR = { navController.navigate("HomaIRScreen") },
        navigateToLungTNM = { navController.navigate("LungTNM") },
        navigateToHccTNM = { navController.navigate("HccTNM") },
        navigateToIntrahepaticCholangiocarcinomaTNM = {
            navController.navigate("IntrahepaticCholangiocarcinomaTNM")
        },
        navigateToCHADS2 = { navController.navigate("CHADS2") },
        navigateToGlasgowComaScale = { navController.navigate("GlasgowComaScaleScreen") },
        navigateToSodiumDifferentialDiagnosis = {
            navController.navigate("SodiumDifferentialDiagnosisScreen")
        },
        navigateToLilleModel = { navController.navigate("LilleModelScreen") },
        navigateToEcog = { navController.navigate("EcogScreen") },
        navigateToInfusionCalculator = { navController.navigate("GammaCalculateScreen") },
        navigateToIkaShinryokoiMaster = { navController.navigate("IkaShinryokoiMaster") },
        navigateToIcd10 = { navController.navigate("Icd10EnglishScreen") },
    )
}

fun getAppScreens(): List<ScreenRoute> {
    return listOf(
        ScreenRoute("ChildPughScreen") { navController -> ChildPughScreen(navController) },
        ScreenRoute("AdropScreen") { navController -> AdropScreen(navController) },
        ScreenRoute("ColorectalTNMScreen") { navController -> ColorectalTNMScreen(navController) },
        ScreenRoute("AcuteTonsillitisAlgorithmScreen") { navController ->
            AcuteTonsillitisAlgorithmScreen(navController)
        },
        ScreenRoute("BloodGasAnalysisScreen") { navController -> BloodGasAnalysisScreen(navController) },
        ScreenRoute("AcutePancreatitisScreen") { navController -> AcutePancreatitisScreen(navController) },
        ScreenRoute("NetakiridoScreen") { navController -> NetakiridoScreen(navController) },
        ScreenRoute("PancreaticTNMScreen") { navController -> PancreaticTNMScreen(navController) },
        ScreenRoute("EsophagealTNMScreen") { navController -> EsophagealTNMScreen(navController) },
        ScreenRoute("MALBIScreen") { navController -> MALBIScreen(navController) },
        ScreenRoute("LiverFibrosisScoreSystemScreen") { navController ->
            LiverFibrosisScoreSystemScreen(navController)
        },
        ScreenRoute("HomaIRScreen") { navController -> HomaIRScreen(navController) },
        ScreenRoute("LungTNM") { navController -> LungTNMScreen(navController) },
        ScreenRoute("HccTNM") { navController -> HCCTNMScreen(navController) },
        ScreenRoute("IntrahepaticCholangiocarcinomaTNM") { navController ->
            IntrahepaticCholangiocarcinomaTNMScreen(navController)
        },
        ScreenRoute("CHADS2") { navController -> Chads2Screen(navController) },
        ScreenRoute("GlasgowComaScaleScreen") { navController -> GlasgowComaScaleScreen(navController) },
        ScreenRoute("SodiumDifferentialDiagnosisScreen") { navController ->
            SodiumDifferentialDiagnosisScreen(navController)
        },
        ScreenRoute("LilleModelScreen") { navController -> LilleModelScreen(navController) },
        ScreenRoute("EcogScreen") { navController -> EcogScreen(navController) },
        ScreenRoute("GammaCalculateScreen") { navController -> GammaCalculateScreen(navController) },
        ScreenRoute("IkaShinryokoiMaster") { navController -> IkaShinryokoiMasterScreen(navController) },
        ScreenRoute("Icd10EnglishScreen") { navController -> Icd10EnglishScreen(navController) },
    )
}

fun IndexScreenActions.executeNavigation(actionType: ActionType) {
    when (actionType) {
        ActionType.NAVIGATE_TO_CHILD_PUGH -> this.navigateToChildPugh()
        ActionType.NAVIGATE_TO_ADROP -> this.navigateToAdrop()
        ActionType.NAVIGATE_TO_COLORECTAL_TNM -> this.navigateToColorectalTNM()
        ActionType.NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM -> this.navigateToAcuteTonsillitisAlgorithm()
        ActionType.NAVIGATE_TO_BLOOD_GAS_ANALYSIS -> this.navigateToBloodGasAnalysis()
        ActionType.NAVIGATE_TO_ACUTE_PANCREATITIS -> this.navigateToAcutePancreatitis()
        ActionType.NAVIGATE_TO_NETAKIRIDO -> this.navigateToNetakirido()
        ActionType.NAVIGATE_TO_PANCREASE_TNM -> this.navigateToPancreaticTNM() // Ensure this matches the function name in IndexScreenActions
        ActionType.NAVIGATE_TO_ESOPAGEAL_TNM -> this.navigateToEsophagealTNM()
        ActionType.NAVIGATE_TO_MALBI -> this.navigateToMALBI()
        ActionType.NAVIGATE_TO_LIVERFIBROSISSCORESYSTEM -> this.navigateToLiverFibrosisScoreSystem()
        ActionType.NAVIGATE_TO_HOMAIR -> this.navigateToHomaIR()
        ActionType.NAVIGATE_TO_LUNG_TNM -> this.navigateToLungTNM()
        ActionType.NAVIGATE_TO_HCC_TNM -> this.navigateToHccTNM()
        ActionType.NAVIGATE_TO_INTRAHEPATICCHOLANGIOCARCINOMA_TNM -> this.navigateToIntrahepaticCholangiocarcinomaTNM()
        ActionType.NAVIGATE_TO_CHADS2 -> this.navigateToCHADS2()
        ActionType.NAVIGATE_TO_GLASGOW_COMA_SCALE -> this.navigateToGlasgowComaScale()
        ActionType.NAVIGATE_TO_SODIUM_DIFFERENTIAL_DIAGNOSIS -> this.navigateToSodiumDifferentialDiagnosis()
        ActionType.NAVIGATE_TO_LILLE_MODEL -> this.navigateToLilleModel()
        ActionType.NAVIGATE_TO_ECOG -> this.navigateToEcog()
        ActionType.NAVIGATE_TO_INFUSION_CALCULATOR -> this.navigateToInfusionCalculator()
        ActionType.NAVIGATE_TO_IKASHINRYOKOIMASTER -> this.navigateToIkaShinryokoiMaster()
        ActionType.NAVIGATE_TO_ICD10 -> this.navigateToIcd10()
        // Add more cases for other ActionType if needed

        // Consider adding an else branch for robustness, especially if ActionType might expand
        // else -> { Log.w("Navigation", "Unhandled action type: $actionType") }
    }
}