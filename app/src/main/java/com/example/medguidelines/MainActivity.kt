package com.example.medguidelines

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
import com.example.medguidelines.ui.screen.AdropScreen
import com.example.medguidelines.ui.screen.ChildPughScreen
import com.example.medguidelines.ui.screen.ColorectalTNMScreen
import com.example.medguidelines.ui.screen.IndexScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.ContrastAwareMedguidelinesTheme
import com.example.medguidelines.ui.screen.AcutePancreatitisScreen
import com.example.medguidelines.ui.screen.AcuteTonsillitisAlgorithmScreen
import com.example.medguidelines.ui.screen.BloodGasAnalysisScreen
import com.example.medguidelines.ui.screen.EsophagealTNMScreen
import com.example.medguidelines.ui.screen.LiverFibrosisScoreSystemScreen
import com.example.medguidelines.ui.screen.MALBIScreen
import com.example.medguidelines.ui.screen.NetakiridoScreen
import com.example.medguidelines.ui.screen.PancreaticTNMScreen

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