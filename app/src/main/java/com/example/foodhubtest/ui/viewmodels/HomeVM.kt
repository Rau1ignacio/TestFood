package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import kotlinx.coroutines.flow.*

data class HomeState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Todos"
)

class HomeVM(repo: FoodRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Todos")

    val state: StateFlow<HomeState> = combine(
        _searchQuery,
        _selectedCategory,
        repo.products()
    ) { query, category, products ->
        val searchedProducts = if (query.isBlank()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }

        val categorizedProducts = if (category == "Todos") {
            searchedProducts
        } else {
            searchedProducts.filter { it.category.equals(category, ignoreCase = true) }
        }

        HomeState(
            products = categorizedProducts,
            searchQuery = query,
            selectedCategory = category
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}