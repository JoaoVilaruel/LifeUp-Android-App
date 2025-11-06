package com.example.listagamificada.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.example.listagamificada.ui.screens.auth.LoginScreen
import com.example.listagamificada.ui.screens.profile.ProfileScreen
import com.example.listagamificada.ui.screens.quotes.QuoteScreen
import com.example.listagamificada.ui.screens.ranking.RankingScreen
import com.example.listagamificada.ui.screens.shop.ShopScreen
import com.example.listagamificada.ui.screens.splash.SplashScreen
import com.example.listagamificada.ui.screens.tasks.TaskEditorScreen
import com.example.listagamificada.ui.screens.tasks.TaskListScreen
import com.example.listagamificada.viewmodel.AuthViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Tasks : Screen("tasks", "Tarefas", Icons.Default.Home)
    object Quotes : Screen("quotes", "Frases", Icons.Default.Favorite)
    object Ranking : Screen("ranking", "Ranking", Icons.Default.Star)
    object Shop : Screen("shop", "Loja", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Editor : Screen("editor", "Editor")
}

val bottomNavItems = listOf(Screen.Tasks, Screen.Quotes, Screen.Ranking, Screen.Shop, Screen.Profile)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun navGraph(factory: ViewModelProvider.Factory) {
    val navController = rememberAnimatedNavController()
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    val darkCharcoal = Color(0xFF1A1A2E)
    val neonPink = Color(0xFFE94560)
    val offWhite = Color(0xFFF0F0F0)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

    Scaffold(
        containerColor = darkCharcoal,
        topBar = {
            if (showBottomBar) {
                val currentScreen = bottomNavItems.find { it.route == currentDestination?.route }
                TopAppBar(
                    title = { Text(currentScreen?.label ?: "", color = offWhite) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.Transparent) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.label) } },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = neonPink,
                                selectedTextColor = neonPink,
                                unselectedIconColor = offWhite.copy(alpha = 0.6f),
                                unselectedTextColor = offWhite.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.Tasks.route) {
                FloatingActionButton(
                    onClick = { navController.navigate("${Screen.Editor.route}/") },
                    containerColor = neonPink,
                    contentColor = darkCharcoal
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
                }
            }
        }
    ) { innerPadding ->
        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onTimeout = {
                    val destination = if (authViewModel.currentUser() == null) Screen.Login.route else Screen.Tasks.route
                    navController.navigate(destination) { popUpTo(Screen.Splash.route) { inclusive = true } }
                })
            }
            composable(Screen.Login.route) {
                LoginScreen(factory = factory, onLoginSuccess = {
                    navController.navigate(Screen.Tasks.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                })
            }
            composable(Screen.Tasks.route) {
                TaskListScreen(factory = factory, onOpenEditor = { id ->
                    navController.navigate("${Screen.Editor.route}/${id ?: ""}")
                })
            }
            composable(Screen.Quotes.route) { QuoteScreen(factory = factory) }
            composable(Screen.Ranking.route) { RankingScreen(factory = factory) }
            composable(Screen.Shop.route) { ShopScreen(factory = factory) }
            composable(Screen.Profile.route) {
                ProfileScreen(factory = factory, onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                })
            }
            composable("${Screen.Editor.route}/{taskId}") { backStackEntry ->
                val taskId = backStackEntry?.arguments?.getString("taskId")
                TaskEditorScreen(factory = factory, taskId = taskId, onSaved = { navController.popBackStack() })
            }
        }
    }
}
