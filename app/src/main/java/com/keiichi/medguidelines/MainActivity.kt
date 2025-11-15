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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keiichi.compose.MedGuidelinesTheme
import com.keiichi.medguidelines.data.getAppScreens
import com.keiichi.medguidelines.data.rememberIndexScreenActions
import com.keiichi.medguidelines.ui.component.AppDimensions
import com.keiichi.medguidelines.ui.component.LocalAppDimensions
import com.keiichi.medguidelines.ui.screen.GlasgowComaScaleScreen
import com.keiichi.medguidelines.ui.screen.IndexScreen
import com.keiichi.medguidelines.ui.screen.SofaScreen
import com.keiichi.medguidelines.ui.viewModel.SofaViewModel

data class ScreenRoute(
    val route: String,
    val content: @Composable (NavHostController) -> Unit
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedGuidelinesTheme(
                //enableEdgeToEdge = true
            ) {
                CompositionLocalProvider(LocalAppDimensions provides AppDimensions()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
//                            .windowInsetsPadding(
//                                WindowInsets.safeDrawing
//                            )
                        ,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    {
                        val controller = rememberNavController()
                        val sofaViewModel: SofaViewModel =
                            viewModel()
                        val appScreens = getAppScreens(sofaViewModel)
                        val indexScreenActions = rememberIndexScreenActions(navController = controller)


                        NavHost(controller, startDestination = "IndexScreen") {
                            composable("IndexScreen") {
                                ChildComposable {
                                    IndexScreen(
                                        actions = indexScreenActions
                                    )
                                }
                            }
                            appScreens.forEach { screenRoute ->
                                composable(screenRoute.route) {
                                    ChildComposable {
                                        when (screenRoute.route) {
                                            "sofa_screen" -> SofaScreen(
                                                navController = controller,
                                                viewModel = sofaViewModel
                                            )
                                            "glasgow_coma_scale_screen" -> GlasgowComaScaleScreen(
                                                navController = controller,
                                                viewModel = sofaViewModel
                                            )
                                            else -> {
                                                // For all other screens, call the original content function
                                                screenRoute.content(controller)
                                            }
                                        }
                                    }
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

