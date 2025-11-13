package com.example.listagamificada.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.example.listagamificada.ui.screens.auth.LoginScreen
import com.example.listagamificada.ui.screens.challenges.ChallengesScreen
import com.example.listagamificada.ui.screens.onboarding.OnboardingScreen
import com.example.listagamificada.ui.screens.profile.EditProfileScreen
import com.example.listagamificada.ui.screens.profile.ProfileScreen
import com.example.listagamificada.ui.screens.quotes.QuoteScreen
import com.example.listagamificada.ui.screens.ranking.RankingScreen
import com.example.listagamificada.ui.screens.settings.SettingsScreen
import com.example.listagamificada.ui.screens.splash.SplashScreen
import com.example.listagamificada.ui.screens.tasks.TaskEditorScreen
import com.example.listagamificada.ui.screens.tasks.TaskListScreen
import com.example.listagamificada.viewmodel.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Onboarding : Screen("onboarding", "Onboarding")
    object Login : Screen("login", "Login")
    object Tasks : Screen("tasks", "Tarefas", Icons.Default.Home)
    object Quotes : Screen("quotes", "Frases", Icons.Default.Favorite)
    object Challenges : Screen("challenges", "Desafios", Icons.Default.ThumbUp)
    object Ranking : Screen("ranking", "Ranking", Icons.Default.Star)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Editor : Screen("editor", "Editor de Tarefas")
    object Settings : Screen("settings", "Configurações", Icons.Default.Settings)
    object EditProfile : Screen("edit_profile", "Editar Perfil")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(factory: ViewModelFactory) {
    val navController = rememberAnimatedNavController()
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val onboardingViewModel: OnboardingViewModel = viewModel(factory = factory)

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                val destination = if (onboardingViewModel.hasOnboardingBeenShown()) {
                    if (authViewModel.currentUser() == null) Screen.Login.route else Screen.Tasks.route
                } else {
                    Screen.Onboarding.route
                }
                navController.navigate(destination) { popUpTo(Screen.Splash.route) { inclusive = true } }
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onOnboardingComplete = {
                onboardingViewModel.setOnboardingShown()
                navController.navigate(Screen.Login.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(factory = factory, onLoginSuccess = {
                navController.navigate(Screen.Tasks.route) { popUpTo(Screen.Login.route) { inclusive = true } }
            })
        }
        composable(Screen.Tasks.route) {
            val mainViewModel: MainViewModel = viewModel(factory = factory)
            TaskListScreen(
                mainViewModel = mainViewModel, 
                authViewModel = authViewModel, 
                onOpenEditor = { id ->
                    navController.navigate("${Screen.Editor.route}?taskId=$id")
                },
                navController = navController
            )
        }
        composable(Screen.Challenges.route) {
            ChallengesScreen(factory = factory, navController = navController)
        }
        composable(Screen.Ranking.route) {
            RankingScreen(factory = factory, navController = navController)
        }
        composable(Screen.Quotes.route) {
            val quoteViewModel: QuoteViewModel = viewModel(factory = factory)
            QuoteScreen(quoteViewModel = quoteViewModel, navController = navController)
        }
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(factory = factory)
            val activity = LocalContext.current as ComponentActivity
            val themeViewModel: ThemeViewModel = viewModel(viewModelStoreOwner = activity, factory = factory)
            ProfileScreen(
                authViewModel = authViewModel, 
                profileViewModel = profileViewModel, 
                onLogout = {
                    themeViewModel.setTheme(Theme.DEFAULT)
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } }
                },
                navController = navController
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(factory = factory, onNavigateBack = { navController.popBackStack() }, navController = navController)
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
        composable(Screen.EditProfile.route) {
            EditProfileScreen(factory = factory, onNavigateBack = { navController.popBackStack() })
        }
    }
}