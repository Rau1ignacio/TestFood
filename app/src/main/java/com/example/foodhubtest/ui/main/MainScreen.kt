package com.example.foodhubtest.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodhubtest.core.nav.Route
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.ui.viewmodels.CartVM
import com.example.foodhubtest.ui.viewmodels.SessionVM
import com.example.foodhubtest.ui.viewmodels.ViewModelFactoryWithSession

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repo: FoodRepository,
    sessionVM: SessionVM,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val factoryWithSession = ViewModelFactoryWithSession(repo, sessionVM)
    val cartVM: CartVM = viewModel(factory = factoryWithSession)

    val cartState by cartVM.cartState.collectAsState()
    val sessionState by sessionVM.state.collectAsState()
    val currentUser = sessionState.loggedInUser

    val bottomNavItems = listOf(
        BottomNavItem("Home", Route.Home.route, Icons.Default.Home),
        BottomNavItem("Carrito", Route.Cart.route, Icons.Default.ShoppingCart),
        BottomNavItem("Admin", Route.Admin.route, Icons.Default.AdminPanelSettings)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Hub") },
                actions = {
                    // --- TEXTO DE DEBUG AÑADIDO ---
                    // Esto te mostrará el usuario actual. Bórralo cuando todo funcione.
                    currentUser?.let { user ->
                        Text(
                            text = "Rol: ${user.role}",
                            modifier = Modifier.padding(end = 8.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.History, contentDescription = "Historial de Pedidos")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    val isAdminRoute = item.route == Route.Admin.route
                    // Lógica para ocultar la pestaña de Admin si el usuario no es "ADMIN"
                    if (isAdminRoute && currentUser?.role != "ADMIN") {
                        // No se renderiza nada
                    } else {
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(item.label) },
                            icon = { /* ... (código del BadgedBox no cambia) */ }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        MainNavGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            repo = repo,
            sessionVM = sessionVM,
            cartVM = cartVM
        )
    }
}