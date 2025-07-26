package com.bekisma.adlamfulfulde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.bekisma.adlamfulfulde.navigation.AppNavigation
import com.bekisma.adlamfulfulde.ui.theme.AdlamFulfuldeTheme
import com.bekisma.adlamfulfulde.data.MenuItem

class MainActivity : ComponentActivity() {
    private lateinit var proManager: ProManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        proManager = ProManager(this)
        

        setContent {
            val themeManager = remember { ThemeManager(this) }
            val navController = rememberNavController()

            AdlamFulfuldeTheme {
                AppNavigation(
                    navController = navController,
                    onNavigation = { item ->
                        handleNavigation(navController, item)
                    },
                    themeManager = themeManager,
                    proManager = proManager
                )
            }
        }
    }

    private fun handleNavigation(navController: androidx.navigation.NavController, item: MenuItem) {
        try {
            
            // Navigate to destination
            navController.navigate(item.destination)
        } catch (e: Exception) {
            // Fallback to direct navigation if there's any error
            navController.navigate(item.destination)
        }
    }
}
