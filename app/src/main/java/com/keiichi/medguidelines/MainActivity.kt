package com.keiichi.medguidelines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keiichi.medguidelines.ui.screen.AdropScreen
import com.keiichi.medguidelines.ui.screen.ChildPughScreen
import com.keiichi.medguidelines.ui.screen.ColorectalTNMScreen
import com.keiichi.medguidelines.ui.screen.IndexScreen
import androidx.compose.runtime.Composable
import com.keiichi.compose.ContrastAwareMedguidelinesTheme
import com.keiichi.medguidelines.ui.screen.AcutePancreatitisScreen
import com.keiichi.medguidelines.ui.screen.AcuteTonsillitisAlgorithmScreen
import com.keiichi.medguidelines.ui.screen.BloodGasAnalysisScreen
import com.keiichi.medguidelines.ui.screen.EsophagealTNMScreen
import com.keiichi.medguidelines.ui.screen.HomaIRScreen
import com.keiichi.medguidelines.ui.screen.LiverFibrosisScoreSystemScreen
import com.keiichi.medguidelines.ui.screen.MALBIScreen
import com.keiichi.medguidelines.ui.screen.NetakiridoScreen
import com.keiichi.medguidelines.ui.screen.PancreaticTNMScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContrastAwareMedguidelinesTheme {
                val controller = rememberNavController()
                NavHost(controller, startDestination = "IndexScreen") {
                    composable("IndexScreen") {
                        ChildComposable {
                            IndexScreen(
                                navigateToChildPugh = { controller.navigate("ChildPughScreen") },
                                navigateToAdrop = { controller.navigate("AdropScreen") },
                                navigateToColorectalTNM = { controller.navigate("ColorectalTNMScreen") },
                                navigateToAcuteTonsillitisAlgorithm = { controller.navigate("AcuteTonsillitisAlgorithmScreen")},
                                navigateToBloodGasAnalysis = { controller.navigate("BloodGasAnalysisScreen")},
                                navigateToAcutePancreatitis = { controller.navigate("AcutePancreatitisScreen")},
                                navigateToNetakirido = { controller.navigate("NetakiridoScreen")},
                                navigateToPancreaticTNM = { controller.navigate("PancreaticTNMScreen")},
                                navigateToEsophagealTNM = { controller.navigate("EsophagealTNMScreen")},
                                navigateToMALBI = { controller.navigate("MALBIScreen")},
                                navigateToLiverFibrosisScoreSystem = { controller.navigate("LiverFibrosisScoreSystemScreen")},
                                navigateToHomaIR = { controller.navigate("HomaIRScreen")}
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