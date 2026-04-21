package com.dongnh.bubblepickerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val COMPOSE_DEMO = "compose-demo"
    const val LEGACY_DEMO = "legacy-xml-demo"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    BubblePickerDemoApp()
                }
            }
        }
    }
}

@Composable
private fun BubblePickerDemoApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.COMPOSE_DEMO) {
        composable(Routes.COMPOSE_DEMO) {
            ComposeDemoScreen(onOpenLegacy = { navController.navigate(Routes.LEGACY_DEMO) })
        }
        composable(Routes.LEGACY_DEMO) {
            LegacyDemoScreen(onBack = { navController.popBackStack() })
        }
    }
}
