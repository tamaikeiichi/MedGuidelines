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
import com.example.compose.AppTheme
import com.example.medguidelines.ui.screen.AdropScreen
import com.example.medguidelines.ui.screen.ChildPughScreen
import com.example.medguidelines.ui.screen.ColorectalTNMScreen
import com.example.medguidelines.ui.screen.IndexScreen
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.medguidelines.ui.screen.AcuteTonsillitisAlgorithmScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val controller = rememberNavController()
                NavHost(controller, startDestination = "IndexScreen") {
                    composable("IndexScreen") {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                            ) {
                                IndexScreen(
                                    navigateToChildPugh = { controller.navigate("ChildPughScreen") },
                                    navigateToAdrop = { controller.navigate("AdropScreen") },
                                    navigateToColorectalTNM = { controller.navigate("ColorectalTNMScreen") },
                                    navigateToAcuteTonsillitisAlgorithm = { controller.navigate("AcuteTonsillitisAlgorithmScreen")},

                                )
                            }
                        }
                    }
                    composable("ChildPughScreen") {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                            ) {
                                ChildPughScreen()
                            }

                        }
                    }
                    composable("AdropScreen") {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                            ) {
                                AdropScreen()
                            }

                        }
                    }
                    composable("ColorectalTNMScreen") {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                            ) {
                                ColorectalTNMScreen()
                            }
                        }
                    }
                    composable("AcuteTonsillitisAlgorithmScreen") {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                            ) {
                                AcuteTonsillitisAlgorithmScreen()
                            }

                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AppTheme (darkTheme = false,
        dynamicColor = true,
        ){
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ){
                innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            )
            {
                IndexScreen(
                    navigateToChildPugh = {},
                    navigateToAdrop = {},
                    navigateToColorectalTNM = {},
                    navigateToAcuteTonsillitisAlgorithm = {},
                    )
            }
        }
    }
}
