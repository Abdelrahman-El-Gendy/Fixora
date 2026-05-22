package com.fixora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fixora.core.designsystem.theme.FixoraTheme
import com.fixora.feature.auth.AuthViewModel
import com.fixora.feature.auth.LoginScreen
import com.fixora.feature.auth.RegisterScreen
import com.fixora.feature.booking.BookingScreen
import com.fixora.feature.booking.BookingViewModel
import com.fixora.feature.home.HomeScreen
import com.fixora.feature.home.HomeViewModel
import com.fixora.feature.profile.ProfileScreen
import com.fixora.feature.profile.ProfileViewModel
import com.fixora.feature.provider.ProviderDetailScreen
import com.fixora.feature.provider.ProviderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixoraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FixoraNavHost()
                }
            }
        }
    }
}

object FixoraRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROVIDER_DETAIL = "provider/{providerId}"
    const val BOOKING = "booking/{providerId}"
    const val PROFILE = "profile"

    fun providerDetail(providerId: String) = "provider/$providerId"
    fun booking(providerId: String) = "booking/$providerId"
}

@Composable
fun FixoraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = FixoraRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash Screen
        composable(FixoraRoutes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(FixoraRoutes.LOGIN) {
                        popUpTo(FixoraRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(FixoraRoutes.LOGIN) {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(FixoraRoutes.HOME) {
                        popUpTo(FixoraRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(FixoraRoutes.REGISTER)
                },
                viewModel = viewModel
            )
        }

        // Register Screen
        composable(FixoraRoutes.REGISTER) {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(FixoraRoutes.HOME) {
                        popUpTo(FixoraRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // Home Screen
        composable(FixoraRoutes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                onNavigateToProvider = { providerId ->
                    navController.navigate(FixoraRoutes.providerDetail(providerId))
                },
                onNavigateToProfile = {
                    navController.navigate(FixoraRoutes.PROFILE)
                },
                viewModel = viewModel
            )
        }

        // Provider Detail Screen
        composable(
            route = FixoraRoutes.PROVIDER_DETAIL,
            arguments = listOf(navArgument("providerId") { type = NavType.StringType })
        ) {
            val viewModel: ProviderViewModel = hiltViewModel()
            ProviderDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToBooking = { providerId ->
                    navController.navigate(FixoraRoutes.booking(providerId))
                },
                viewModel = viewModel
            )
        }

        // Booking Flow Screen
        composable(
            route = FixoraRoutes.BOOKING,
            arguments = listOf(navArgument("providerId") { type = NavType.StringType })
        ) {
            val viewModel: BookingViewModel = hiltViewModel()
            BookingScreen(
                onBackClick = { navController.popBackStack() },
                onBookingSuccess = {
                    navController.navigate(FixoraRoutes.HOME) {
                        popUpTo(FixoraRoutes.HOME) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        // Profile Screen
        composable(FixoraRoutes.PROFILE) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(FixoraRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
