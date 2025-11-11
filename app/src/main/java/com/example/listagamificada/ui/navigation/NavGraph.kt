package com.example.listagamificada.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.example.listagamificada.ui.screens.auth.LoginScreen
import com.example.listagamificada.ui.screens.challenges.ChallengesScreen
import com.example.listagamificada.ui.screens.profile.ProfileScreen
import com.example.listagamificada.ui.screens.quotes.QuoteScreen
import com.example.listagamificada.ui.screens.ranking.RankingScreen
import com.example.listagamificada.ui.screens.shop.ShopScreen
import com.example.listagamificada.ui.screens.splash.SplashScreen
import com.example.listagamificada.ui.screens.tasks.TaskEditorScreen
import com.example.listagamificada.ui.screens.tasks.TaskListScreen
import com.example.listagamificada.viewmodel.AuthViewModel
import com.example.listagamificada.viewmodel.MainViewModel
import com.example.listagamificada.viewmodel.ViewModelFactory

sealed class Screen(val route: String, val label: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Tasks : Screen("tasks", "Tarefas", Icons.Default.Home)
    object Quotes : Screen("quotes", "Frases", Icons.Default.Favorite)
    object Challenges : Screen("challenges", "Desafios", Icons.Default.ThumbUp)
    object Ranking : Screen("ranking", "Ranking", Icons.Default.Star)
    object Shop : Screen("shop", "Loja", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Editor : Screen("editor", "Editor")
}

val bottomNavItems = listOf(Screen.Tasks, Screen.Quotes, Screen.Challenges, Screen.Ranking, Screen.Shop, Screen.Profile)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun NavGraph(factory: ViewModelFactory) {
    val navController = rememberAnimatedNavController()
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val mainViewModel: MainViewModel = viewModel(factory = factory)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.route?.startsWith(screen.route) == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(visible = currentDestination?.route == Screen.Tasks.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.Editor.route) }) { 
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
                TaskListScreen(mainViewModel = mainViewModel, authViewModel = authViewModel, onOpenEditor = { id ->
                    navController.navigate("${Screen.Editor.route}?taskId=$id")
                })
            }
            composable(Screen.Challenges.route) { ChallengesScreen(factory = factory) } // New Composable
            composable(Screen.Ranking.route) { RankingScreen(factory = factory) }
            composable(Screen.Shop.route) { ShopScreen(factory = factory) }
            composable(Screen.Quotes.route) { QuoteScreen(factory = factory) }
            composable(Screen.Profile.route) {
                ProfileScreen(mainViewModel = mainViewModel, authViewModel = authViewModel, onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                })
            }
            composable(
                route = "${Screen.Editor.route}?taskId={taskId}",
                arguments = listOf(navArgument("taskId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                TaskEditorScreen(factory = factory, taskId = taskId, onSaved = { navController.popBackStack() })
            }
        }
    }
}