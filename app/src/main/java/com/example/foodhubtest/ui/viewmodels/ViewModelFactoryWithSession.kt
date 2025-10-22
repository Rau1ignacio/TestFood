package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhubtest.data.repository.FoodRepository

/**
 * Fábrica especializada que sabe cómo inyectar el SessionVM
 * a los ViewModels que lo requieran.
 */
class ViewModelFactoryWithSession(
    private val repo: FoodRepository,
    private val sessionVM: SessionVM
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthVM::class.java) -> AuthVM(repo, sessionVM) as T
            modelClass.isAssignableFrom(OrderHistoryVM::class.java) -> OrderHistoryVM(repo, sessionVM) as T
            modelClass.isAssignableFrom(CartVM::class.java) -> CartVM(repo, sessionVM) as T // <-- AÑADIDO
            // Para otros ViewModels, delega a la fábrica simple.
            else -> ViewModelFactory(repo).create(modelClass)
        }
    }
}