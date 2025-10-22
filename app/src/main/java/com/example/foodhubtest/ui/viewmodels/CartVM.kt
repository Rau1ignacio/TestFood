package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.CartItem
import com.example.foodhubtest.data.local.entities.Order
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartState(
    val items: List<Pair<Product, CartItem>> = emptyList(),
    val total: Int = 0
)

class CartVM(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM // <-- INYECTAMOS EL SESSION VM
) : ViewModel() {

    val cartState: StateFlow<CartState> = repo.products()
        .combine(repo.getCartItems()) { products, cartItems ->
            val itemsWithDetails = cartItems.mapNotNull { cartItem ->
                products.find { it.id == cartItem.productId }?.let { product ->
                    Pair(product, cartItem)
                }
            }
            val total = itemsWithDetails.sumOf { (product, cartItem) ->
                product.price * cartItem.quantity
            }
            CartState(items = itemsWithDetails, total = total)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartState()
        )

    fun addToCart(productId: Long) {
        viewModelScope.launch {
            val currentQuantity = cartState.value.items
                .find { it.first.id == productId }?.second?.quantity ?: 0
            repo.addToCart(CartItem(productId, currentQuantity + 1))
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repo.removeFromCart(cartItem)
        }
    }

    fun confirmOrder(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val total = cartState.value.total
            val userId = sessionVM.state.value.loggedInUser?.id

            // Solo procede si hay un total y un usuario logueado
            if (total > 0 && userId != null) {
                // --- CORREGIDO: Pasamos el userId al crear la orden ---
                val newOrderId = repo.insertOrder(Order(userId = userId, total = total))
                repo.clearCart()
                onSuccess(newOrderId)
            }
        }
    }
}