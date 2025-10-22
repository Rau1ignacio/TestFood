package com.example.foodhubtest.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.foodhubtest.core.nav.Route
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.ui.admin.AdminListScreen
import com.example.foodhubtest.ui.admin.AdminProductFormScreen
import com.example.foodhubtest.ui.cart.CartScreen
import com.example.foodhubtest.ui.cart.OrderSummaryScreen
import com.example.foodhubtest.ui.detail.DetailScreen
import com.example.foodhubtest.ui.history.OrderHistoryScreen
import com.example.foodhubtest.ui.home.HomeScreen
import com.example.foodhubtest.ui.viewmodels.*

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    repo: FoodRepository,
    sessionVM: SessionVM,
    cartVM: CartVM
) {
    val factoryWithSession = ViewModelFactoryWithSession(repo, sessionVM)
    val factorySimple = ViewModelFactory(repo)

    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
        modifier = modifier
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                repo = repo,
                onProductClick = { productId ->
                    navController.navigate(Route.Detail.build(productId))
                }
            )
        }

        composable(Route.Cart.route) {
            CartScreen(
                vm = cartVM,
                onConfirmOrder = { orderId ->
                    navController.navigate(Route.OrderSummary.build(orderId)) {
                        popUpTo(Route.Home.route)
                    }
                }
            )
        }

        composable("history") {
            val orderHistoryVM: OrderHistoryVM = viewModel(factory = factoryWithSession)
            OrderHistoryScreen(vm = orderHistoryVM, onBack = { navController.popBackStack() })
        }

        // --- CORRECCIÓN DE LA RUTA DE ADMIN ---
        // La ruta principal 'admin_list' ahora abre la pantalla de la lista.
        composable(Route.Admin.route) {
            val adminVM: AdminVM = viewModel(factory = factorySimple)
            AdminListScreen(
                vm = adminVM,
                onAddProduct = {
                    // Navega al formulario sin pasar un ID para CREAR
                    navController.navigate(Route.AdminForm.build())
                },
                onEditProduct = { productId ->
                    // Navega al formulario pasando el ID para EDITAR
                    navController.navigate(Route.AdminForm.build(productId))
                }
            )
        }

        // --- NUEVA RUTA EXCLUSIVA PARA EL FORMULARIO ---
        composable(
            route = Route.AdminForm.route,
            arguments = listOf(navArgument("productId") {
                type = NavType.LongType
                defaultValue = 0L // 0L significa que es un producto nuevo
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId")
            val adminVM: AdminVM = viewModel(factory = factorySimple)
            // Esta llamada ahora es correcta porque le pasamos el 'productId'
            AdminProductFormScreen(
                vm = adminVM,
                productId = if (productId == 0L) null else productId,
                navBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.Detail.route,
            arguments = listOf(navArgument(Route.Detail.argName) { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong(Route.Detail.argName) ?: 0L
            // --- CORRECCIÓN AQUÍ ---
            // Le pasamos la instancia única del cartVM a la pantalla de detalle
            DetailScreen(
                repo = repo,
                cartVM = cartVM,
                id = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.OrderSummary.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: -1L
            OrderSummaryScreen(
                orderId = orderId,
                onBackToHome = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}