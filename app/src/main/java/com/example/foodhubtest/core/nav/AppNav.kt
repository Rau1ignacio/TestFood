package com.example.foodhubtest.core.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.ui.auth.LoginScreen
import com.example.foodhubtest.ui.auth.RegisterScreen
import com.example.foodhubtest.ui.main.MainScreen
import com.example.foodhubtest.ui.viewmodels.AuthVM
import com.example.foodhubtest.ui.viewmodels.SessionVM
import com.example.foodhubtest.ui.viewmodels.ViewModelFactoryWithSession

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Cart : Route("cart")
    data object Detail : Route("detail/{id}") {
        fun build(id: Long) = "detail/$id"
        val argName = "id"
    }
    data object OrderSummary : Route("order_summary/{orderId}") {
        fun build(orderId: Long) = "order_summary/$orderId"
    }
    data object Admin : Route("admin_list")
    data object AdminForm : Route("admin_form?productId={productId}") {
        fun build(productId: Long? = null): String {
            return if (productId != null) "admin_form?productId=$productId" else "admin_form"
        }
    }
}

@Composable
fun AppNav(repo: FoodRepository) {
    val navController = rememberNavController()
    val sessionVM: SessionVM = viewModel()
    val viewModelFactory = ViewModelFactoryWithSession(repo, sessionVM)
    val authVM: AuthVM = viewModel(factory = viewModelFactory)
    val authState by authVM.state.collectAsState()

    LaunchedEffect(authState.loginSuccess) {
        if (authState.loginSuccess) {
            navController.navigate("main_flow") {
                popUpTo("auth_flow") { inclusive = true }
            }
            authVM.onNavigationDone()
        }
    }

    NavHost(navController = navController, startDestination = "auth_flow") {
        navigation(startDestination = "login", route = "auth_flow") {
            composable("login") {
                LoginScreen(
                    state = authState,
                    onFormChange = { authVM.onFormChange(it) },
                    onLoginClick = { authVM.login() },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    state = authState,
                    onFormChange = { authVM.onFormChange(it) },
                    onRegisterClick = { authVM.register() },
                    onNavigateToLogin = {
                        navController.popBackStack()
                        authVM.onNavigationDone()
                    }
                )

                LaunchedEffect(authState.registrationSuccess) {
                    if (authState.registrationSuccess) {
                        navController.popBackStack()
                        authVM.onNavigationDone()
                    }
                }
            }
        }
        composable("main_flow") {
            MainScreen(
                repo = repo,
                sessionVM = sessionVM,
                onLogout = {
                    sessionVM.onLogout()
                    navController.navigate("auth_flow") {
                        popUpTo("main_flow") { inclusive = true }
                    }
                }
            )
        }
    }
}