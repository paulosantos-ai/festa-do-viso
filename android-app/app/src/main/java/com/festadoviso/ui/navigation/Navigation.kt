package com.festadoviso.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.festadoviso.ui.admin.AdminScreen
import com.festadoviso.ui.sorteio.SorteioScreen
import com.festadoviso.ui.theme.VisoBlue
import com.festadoviso.ui.vencedores.VencedoresScreen

/**
 * Sealed class para definir as rotas de navegação.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Sorteio : Screen("sorteio", "Sorteio", Icons.Default.GridOn)
    object Vencedores : Screen("vencedores", "Vencedores", Icons.Default.EmojiEvents)
    object Admin : Screen("admin", "Admin", Icons.Default.AdminPanelSettings)
}

/**
 * Composable principal de navegação com BottomNavigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestaDoVisoNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Sorteio,
        Screen.Vencedores,
        Screen.Admin
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = VisoBlue
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VisoBlue,
                            selectedTextColor = VisoBlue,
                            indicatorColor = VisoBlue.copy(alpha = 0.1f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Sorteio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Sorteio.route) {
                SorteioScreen()
            }
            composable(Screen.Vencedores.route) {
                VencedoresScreen()
            }
            composable(Screen.Admin.route) {
                AdminScreen()
            }
        }
    }
}
