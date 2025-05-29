package com.keiichi.medguidelines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.medguidelines.ui.component.AppDimensions
import com.keiichi.medguidelines.ui.component.LocalAppDimensions
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
import com.keiichi.medguidelines.ui.screen.IndexScreen
import com.keiichi.medguidelines.ui.screen.IntrahepaticCholangiocarcinomaTNMScreen
import com.keiichi.medguidelines.ui.screen.LiverFibrosisScoreSystemScreen
import com.keiichi.medguidelines.ui.screen.LungTNMScreen
import com.keiichi.medguidelines.ui.screen.MALBIScreen
import com.keiichi.medguidelines.ui.screen.NetakiridoScreen
import com.keiichi.medguidelines.ui.screen.PancreaticTNMScreen
import com.keiichi.medguidelines.ui.screen.SodiumDifferentialDiagnosisScreen

data class ScreenRoute(
    val route: String,
    val content: @Composable (NavHostController) -> Unit
)

val appScreens = listOf(
    ScreenRoute("ChildPughScreen") { navController -> ChildPughScreen(navController) },
    ScreenRoute("AdropScreen") { navController -> AdropScreen(navController) },
    ScreenRoute("ColorectalTNMScreen") { navController -> ColorectalTNMScreen(navController) },
    ScreenRoute("AcuteTonsillitisAlgorithmScreen") { navController -> AcuteTonsillitisAlgorithmScreen(navController) },
    ScreenRoute("BloodGasAnalysisScreen") { navController -> BloodGasAnalysisScreen(navController) },
    ScreenRoute("AcutePancreatitisScreen") { navController -> AcutePancreatitisScreen(navController) },
    ScreenRoute("NetakiridoScreen") { navController -> NetakiridoScreen(navController) },
    ScreenRoute("PancreaticTNMScreen") { navController -> PancreaticTNMScreen(navController) },
    ScreenRoute("EsophagealTNMScreen") { navController -> EsophagealTNMScreen(navController) },
    ScreenRoute("MALBIScreen") { navController -> MALBIScreen(navController) },
    ScreenRoute("LiverFibrosisScoreSystemScreen") { navController -> LiverFibrosisScoreSystemScreen(navController) },
    ScreenRoute("HomaIRScreen") { navController -> HomaIRScreen(navController) },
    ScreenRoute("LungTNM") { navController -> LungTNMScreen(navController) },
    ScreenRoute("HccTNM") { navController -> HCCTNMScreen(navController) },
    ScreenRoute("IntrahepaticCholangiocarcinomaTNM") { navController -> IntrahepaticCholangiocarcinomaTNMScreen(navController) },
    ScreenRoute("CHADS2") { navController -> Chads2Screen(navController) },
    ScreenRoute("GlasgowComaScaleScreen") { navController -> GlasgowComaScaleScreen(navController) },
    ScreenRoute("SodiumDifferentialDiagnosisScreen") { navController -> SodiumDifferentialDiagnosisScreen(navController) }
    // Add other screens here
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedGuidelinesTheme {
                CompositionLocalProvider(LocalAppDimensions provides AppDimensions()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    {
                        val controller = rememberNavController()

                        NavHost(controller, startDestination = "IndexScreen") {
                            composable("IndexScreen") {
                                ChildComposable {
                                    IndexScreen(
                                        navigateToChildPugh = { controller.navigate("ChildPughScreen") },
                                        navigateToAdrop = { controller.navigate("AdropScreen") },
                                        navigateToColorectalTNM = { controller.navigate("ColorectalTNMScreen") },
                                        navigateToAcuteTonsillitisAlgorithm = {
                                            controller.navigate(
                                                "AcuteTonsillitisAlgorithmScreen"
                                            )
                                        },
                                        navigateToBloodGasAnalysis = { controller.navigate("BloodGasAnalysisScreen") },
                                        navigateToAcutePancreatitis = { controller.navigate("AcutePancreatitisScreen") },
                                        navigateToNetakirido = { controller.navigate("NetakiridoScreen") },
                                        navigateToPancreaticTNM = { controller.navigate("PancreaticTNMScreen") },
                                        navigateToEsophagealTNM = { controller.navigate("EsophagealTNMScreen") },
                                        navigateToMALBI = { controller.navigate("MALBIScreen") },
                                        navigateToLiverFibrosisScoreSystem = { controller.navigate("LiverFibrosisScoreSystemScreen") },
                                        navigateToHomaIR = { controller.navigate("HomaIRScreen") },
                                        navigateToLungTNM = { controller.navigate("LungTNM") },
                                        navigateToHccTNM = { controller.navigate("HccTNM") },
                                        navigateToIntrahepaticCholangiocarcinomaTNM = {
                                            controller.navigate(
                                                "IntrahepaticCholangiocarcinomaTNM"
                                            )
                                        },
                                        navigateToCHADS2 = { controller.navigate("CHADS2") },
                                        navigateToGlasgowComaScale = {
                                            controller.navigate("GlasgowComaScaleScreen")
                                        },
                                        navigateToSodiumDifferentialDiagnosis = {
                                            controller.navigate("SodiumDifferentialDiagnosisScreen")
                                        }
                                    )
                                }
                            }
                            appScreens.forEach { screenRoute ->
                                composable(screenRoute.route) {
                                    ChildComposable {
                                        screenRoute.content(controller)
                                    }
                                }
                            }
//                            composable("ChildPughScreen") {
//                                ChildComposable {
//                                    ChildPughScreen(controller)
//                                }
//                            }
//                            composable("AdropScreen") {
//                                ChildComposable {
//                                    AdropScreen(controller)
//                                }
//                            }
//                            composable("ColorectalTNMScreen") {
//                                ChildComposable {
//                                    ColorectalTNMScreen(controller)
//                                }
//                            }
//                            composable("AcuteTonsillitisAlgorithmScreen") {
//                                ChildComposable {
//                                    AcuteTonsillitisAlgorithmScreen(controller)
//                                }
//                            }
//                            composable("BloodGasAnalysisScreen") {
//                                ChildComposable {
//                                    BloodGasAnalysisScreen(controller)
//                                }
//                            }
//                            composable("AcutePancreatitisScreen") {
//                                ChildComposable {
//                                    AcutePancreatitisScreen(controller)
//                                }
//                            }
//                            composable("NetakiridoScreen") {
//                                ChildComposable {
//                                    NetakiridoScreen(controller)
//                                }
//                            }
//                            composable("PancreaticTNMScreen") {
//                                ChildComposable {
//                                    PancreaticTNMScreen(controller)
//                                }
//                            }
//                            composable("EsophagealTNMScreen") {
//                                ChildComposable {
//                                    EsophagealTNMScreen(controller)
//                                }
//                            }
//                            composable("MALBIScreen") {
//                                ChildComposable {
//                                    MALBIScreen(controller)
//                                }
//                            }
//                            composable("LiverFibrosisScoreSystemScreen") {
//                                ChildComposable {
//                                    LiverFibrosisScoreSystemScreen(controller)
//                                }
//                            }
//                            composable("HomaIRScreen") {
//                                ChildComposable {
//                                    HomaIRScreen(controller)
//                                }
//                            }
//                            composable("LungTNM") {
//                                ChildComposable {
//                                    LungTNMScreen(controller)
//                                }
//                            }
//                            composable("HccTNM") {
//                                ChildComposable {
//                                    HCCTNMScreen(controller)
//                                }
//                            }
//                            composable("IntrahepaticCholangiocarcinomaTNM") {
//                                ChildComposable {
//                                    IntrahepaticCholangiocarcinomaTNMScreen(controller)
//                                }
//                            }
//                            composable("CHADS2") {
//                                ChildComposable {
//                                    Chads2Screen(controller)
//                                }
//                            }
//                            composable("GlasgowComaScaleScreen") {
//                                ChildComposable {
//                                   GlasgowComaScaleScreen(controller)
//                                }
//                            }
//                            composable("SodiumDifferentialDiagnosisScreen") {
//                                ChildComposable {
//                                    SodiumDifferentialDiagnosisScreen(controller)
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildComposable(childScreen: @Composable () -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            childScreen()
        }
    }
}

