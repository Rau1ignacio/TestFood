package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.Order
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.*

data class OrderHistoryState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true
)

class OrderHistoryVM(
    repo: FoodRepository,
    sessionVM: SessionVM
) : ViewModel() {

    val state: StateFlow<OrderHistoryState> = sessionVM.state.flatMapLatest { sessionState ->
        sessionState.loggedInUser?.let { user ->
            // Ahora esta llamada funciona porque la función existe en el Repositorio
            repo.getOrdersForUser(user.id).map { orders ->
                OrderHistoryState(orders = orders, isLoading = false)
            }
        } ?: flowOf(OrderHistoryState(isLoading = false)) // Si no hay usuario, lista vacía
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrderHistoryState()
    )
}