package com.fixora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.fixora.core.designsystem.theme.FixoraTheme
import com.fixora.feature.auth.AuthViewModel
import com.fixora.feature.auth.login.LoginScreen
import com.fixora.feature.auth.onboarding.OnboardingScreen
import com.fixora.feature.auth.register.RegisterScreen
import com.fixora.feature.booking.booking.BookingScreen
import com.fixora.feature.booking.booking.BookingViewModel
import com.fixora.feature.home.home.HomeScreen
import com.fixora.feature.home.home.HomeViewModel
import com.fixora.feature.profile.profile.ProfileScreen
import com.fixora.feature.profile.profile.ProfileViewModel
import com.fixora.feature.provider.dashboard.ProviderDashboardScreen
import com.fixora.feature.provider.dashboard.ProviderDashboardViewModel
import com.fixora.feature.provider.details.ProviderDetailScreen
import com.fixora.feature.provider.details.ProviderViewModel
import com.fixora.feature.provider.list.ProviderListScreen
import com.fixora.feature.provider.list.ProviderListViewModel
import com.fixora.navigation.BookingRoute
import com.fixora.navigation.HomeRoute
import com.fixora.navigation.LoginRoute
import com.fixora.navigation.OnboardingRoute
import com.fixora.navigation.ProfileRoute
import com.fixora.navigation.ProviderDashboardRoute
import com.fixora.navigation.ProviderDetailRoute
import com.fixora.navigation.ProviderListRoute
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
    viewModel: MainViewModel = hiltViewModel(),
    startDestination: Any = SplashRoute
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onSplashFinished = {
                    val state = uiState
                    if (state is MainUiState.Success) {
                        val destination: Any = when {
                            state.shouldShowOnboarding -> OnboardingRoute
                            state.isAuthenticated -> HomeRoute
                            else -> LoginRoute
                        }
                        navController.navigate(destination) { popUpTo<SplashRoute> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<OnboardingRoute> {
            OnboardingScreen(
                onOnboardingFinished = {
                    navController.navigate(LoginRoute) {
                        popUpTo<OnboardingRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<LoginRoute> {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute)
                },
                viewModel = authViewModel
            )
        }

        composable<RegisterRoute> {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<RegisterRoute> { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(LoginRoute)
                },
                viewModel = authViewModel
            )
        }

        composable<HomeRoute> {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                onNavigateToProvider = { providerId ->
                    navController.navigate(ProviderDetailRoute(providerId))
                },
                onNavigateToProfile = {
                    navController.navigate(ProfileRoute)
                },
                viewModel = homeViewModel
            )
        }

        composable<ProviderListRoute> {
            val providerListViewModel: ProviderListViewModel = hiltViewModel()
            ProviderListScreen(
                onBackClick = { navController.popBackStack() },
                onProviderClick = { providerId ->
                    navController.navigate(ProviderDetailRoute(providerId))
                },
                onBookClick = { providerId ->
                    navController.navigate(BookingRoute(providerId))
                },
                viewModel = providerListViewModel
            )
        }

        composable<ProviderDashboardRoute> {
            val providerDashboardViewModel: ProviderDashboardViewModel = hiltViewModel()
            ProviderDashboardScreen(
                onSwitchToClient = {
                    navController.navigate(HomeRoute) {
                        popUpTo(ProviderDashboardRoute) { inclusive = true }
                    }
                },
                onViewHistory = { /* TODO */ },
                viewModel = providerDashboardViewModel
            )
        }

        composable<ProviderDetailRoute> { backStackEntry ->
            val route: ProviderDetailRoute = backStackEntry.toRoute()
            val providerViewModel: ProviderViewModel = hiltViewModel(
                creationCallback = { factory: ProviderViewModel.Factory ->
                    factory.create(route.providerId)
                }
            )
            ProviderDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToBooking = { providerId ->
                    navController.navigate(BookingRoute(providerId))
                },
                viewModel = providerViewModel
            )
        }

        composable<BookingRoute> { backStackEntry ->
            val route: BookingRoute = backStackEntry.toRoute()
            val bookingViewModel: BookingViewModel = hiltViewModel(
                creationCallback = { factory: BookingViewModel.Factory ->
                    factory.create(route.providerId)
                }
            )
            BookingScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBookingSuccess = {
                    navController.popBackStack(HomeRoute, inclusive = false)
                },
                viewModel = bookingViewModel
            )
        }

        composable<ProfileRoute> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                },
                viewModel = profileViewModel
            )
        }
    }
}
