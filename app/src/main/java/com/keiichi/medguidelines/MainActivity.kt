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
import com.keiichi.medguidelines.ui.screen.HCCTNMScreen
import com.keiichi.medguidelines.ui.screen.HomaIRScreen
import com.keiichi.medguidelines.ui.screen.IndexScreen
import com.keiichi.medguidelines.ui.screen.IntrahepaticCholangiocarcinomaTNMScreen
import com.keiichi.medguidelines.ui.screen.LiverFibrosisScoreSystemScreen
import com.keiichi.medguidelines.ui.screen.LungTNMScreen
import com.keiichi.medguidelines.ui.screen.MALBIScreen
import com.keiichi.medguidelines.ui.screen.NetakiridoScreen
import com.keiichi.medguidelines.ui.screen.PancreaticTNMScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedGuidelinesTheme {
                CompositionLocalProvider(LocalAppDimensions provides AppDimensions()) {
                    // Your main navigation and screen content goes here
                    // All composables
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
                                        navigateToCHADS2 = { controller.navigate("CHADS2") }
                                    )
                                }
                            }
                            composable("ChildPughScreen") {
                                ChildComposable {
                                    ChildPughScreen(controller)
                                }
                            }
                            composable("AdropScreen") {
                                ChildComposable {
                                    AdropScreen(controller)
                                }
                            }
                            composable("ColorectalTNMScreen") {
                                ChildComposable {
                                    ColorectalTNMScreen(controller)
                                }
                            }
                            composable("AcuteTonsillitisAlgorithmScreen") {
                                ChildComposable {
                                    AcuteTonsillitisAlgorithmScreen(controller)
                                }
                            }
                            composable("BloodGasAnalysisScreen") {
                                ChildComposable {
                                    BloodGasAnalysisScreen(controller)
                                }
                            }
                            composable("AcutePancreatitisScreen") {
                                ChildComposable {
                                    AcutePancreatitisScreen(controller)
                                }
                            }
                            composable("NetakiridoScreen") {
                                ChildComposable {
                                    NetakiridoScreen(controller)
                                }
                            }
                            composable("PancreaticTNMScreen") {
                                ChildComposable {
                                    PancreaticTNMScreen(controller)
                                }
                            }
                            composable("EsophagealTNMScreen") {
                                ChildComposable {
                                    EsophagealTNMScreen(controller)
                                }
                            }
                            composable("MALBIScreen") {
                                ChildComposable {
                                    MALBIScreen(controller)
                                }
                            }
                            composable("LiverFibrosisScoreSystemScreen") {
                                ChildComposable {
                                    LiverFibrosisScoreSystemScreen(controller)
                                }
                            }
                            composable("HomaIRScreen") {
                                ChildComposable {
                                    HomaIRScreen(controller)
                                }
                            }
                            composable("LungTNM") {
                                ChildComposable {
                                    LungTNMScreen(controller)
                                }
                            }
                            composable("HccTNM") {
                                ChildComposable {
                                    HCCTNMScreen(controller)
                                }
                            }
                            composable("IntrahepaticCholangiocarcinomaTNM") {
                                ChildComposable {
                                    IntrahepaticCholangiocarcinomaTNMScreen(controller)
                                }
                            }
                            composable("CHADS2") {
                                ChildComposable {
                                    Chads2Screen(controller)
                                }
                            }
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


//@OptIn(ExperimentalAnimationApi::class)
//@Composable
//fun NavGraph(navController: NavHostController) {
//    AnimatedNavHost(
//        navController = navController,
//        startDestination = "IndexScreen",
//        enterTransition = { fadeIn(animationSpec = tween(700)) },
//        exitTransition = { fadeOut(animationSpec = tween(700)) },
//        popEnterTransition = {
//            fadeIn(animationSpec = tween(700))
//        },
//        popExitTransition = {
//            fadeOut(animationSpec = tween(700))
//        }
//    ) {
//        composable(
//            route = Screen.IndexScreen.route + "/{key}",
//            arguments = listOf(navArgument("key") { type = NavType.StringType }),
//        ) { entry ->
//            val key = entry.arguments?.getString("key")
//            IndexScreen(navController = navController, key = key)
//        }
//        composable(route = Screen.ChildScreen.route) {
//            ChildScreen(navController = navController)
//        }
//    }
//}