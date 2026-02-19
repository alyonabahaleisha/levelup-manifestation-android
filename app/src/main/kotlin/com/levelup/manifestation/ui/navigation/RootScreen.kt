package com.levelup.manifestation.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.levelup.manifestation.ui.screens.affirmations.AffirmationsScreen
import com.levelup.manifestation.ui.screens.dreamlife.DreamLifeScreen
import com.levelup.manifestation.ui.screens.reprogram.ReprogramScreen
import kotlinx.serialization.Serializable

// ── Routes ────────────────────────────────────────────────────────────────────

@Serializable object Affirmations
@Serializable object Reprogram
@Serializable object DreamLife

// ── Root screen with bottom nav ───────────────────────────────────────────────

@Composable
fun RootScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Affirmations") },
                    label = { Text("Affirmations") },
                    selected = currentDestination?.hasRoute<Affirmations>() == true,
                    onClick = { navController.navigate(Affirmations) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "Reprogram") },
                    label = { Text("Reprogram") },
                    selected = currentDestination?.hasRoute<Reprogram>() == true,
                    onClick = { navController.navigate(Reprogram) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Stars, contentDescription = "Dream Life") },
                    label = { Text("Dream Life") },
                    selected = currentDestination?.hasRoute<DreamLife>() == true,
                    onClick = { navController.navigate(DreamLife) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Affirmations,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Affirmations> { AffirmationsScreen() }
            composable<Reprogram> { ReprogramScreen() }
            composable<DreamLife> { DreamLifeScreen() }
        }
    }
}
