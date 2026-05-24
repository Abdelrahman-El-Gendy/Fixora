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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import com.fixora.navigation.BookingRoute
import com.fixora.navigation.HomeRoute
import com.fixora.navigation.LoginRoute
import com.fixora.navigation.ProfileRoute
import com.fixora.navigation.ProviderDetailRoute
import com.fixora.navigation.RegisterRoute
import com.fixora.navigation.SplashRoute
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

@Composable
fun FixoraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = SplashRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(LoginRoute) {
                        popUpTo<SplashRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<LoginRoute> {
            val viewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute)
                },
                viewModel = viewModel
            )
        }

        composable<RegisterRoute> {
            val viewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable<HomeRoute> {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                onNavigateToProvider = { providerId ->
                    navController.navigate(ProviderDetailRoute(providerId))
                },
                onNavigateToProfile = {
                    navController.navigate(ProfileRoute)
                },
                viewModel = viewModel
            )
        }

        composable<ProviderDetailRoute> { backStackEntry ->
            val route: ProviderDetailRoute = backStackEntry.toRoute()
            val vm = hiltViewModel<ProviderViewModel, ProviderViewModel.Factory>(
                creationCallback = { it.create(route.providerId) }
            )
            ProviderDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToBooking = { providerId ->
                    navController.navigate(BookingRoute(providerId))
                },
                viewModel = vm
            )
        }

        composable<BookingRoute> { backStackEntry ->
            val route: BookingRoute = backStackEntry.toRoute()
            val vm = hiltViewModel<BookingViewModel, BookingViewModel.Factory>(
                creationCallback = { it.create(route.providerId) }
            )
            BookingScreen(
                onBackClick = { navController.popBackStack() },
                onBookingSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<HomeRoute> { inclusive = true }
                    }
                },
                viewModel = vm
            )
        }

        composable<ProfileRoute> {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
