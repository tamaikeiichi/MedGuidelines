package com.example.medguidelines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medguidelines.data.indexnames
import com.example.medguidelines.ui.screen.AdropScreen
import com.example.medguidelines.ui.screen.ChildPughScreen
import com.example.medguidelines.ui.screen.IndexScreen
import com.example.medguidelines.ui.theme.MedGuidelinesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedGuidelinesTheme {
                val controller = rememberNavController()
                NavHost(controller, startDestination = "IndexScreen") {
                    composable("IndexScreen"){
                        Scaffold(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                        ) { innerPadding ->
                            IndexScreen(indexnames,
                                navigateToChildPugh = {controller.navigate("ChildPughScreen")},
                                navigateToAdrop = {controller.navigate("AdropScreen")},
                            )
                        }
                    }
                    composable("ChildPughScreen"){
                        Scaffold(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                        ) {
                            innerPadding ->
                            ChildPughScreen()
                        }
                    }
                    composable("AdropScreen"){
                        Scaffold(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                        ) { innerPadding ->
                            AdropScreen()
                        }
                    }
                }

            }
        }
    }
}
