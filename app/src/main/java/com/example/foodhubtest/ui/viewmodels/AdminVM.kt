package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.repository.FoodRepository
import com.example.foodhubtest.domain.models.ProductForm
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminVM(private val repo: FoodRepository) : ViewModel() {

    // --- ESTADO PARA LA LISTA DE PRODUCTOS ---
    val products: StateFlow<List<Product>> = repo.products()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- ESTADO PARA EL FORMULARIO (Crear/Editar) ---
    private val _formState = MutableStateFlow(ProductForm())
    val formState = _formState.asStateFlow()

    // --- ESTADO PARA EL DIÁLOGO DE BORRADO ---
    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete = _productToDelete.asStateFlow()

    fun onFormChange(form: ProductForm) {
        _formState.value = form
    }

    // --- LÓGICA DE BORRADO ---
    fun onDeleteTriggered(product: Product) {
        _productToDelete.value = product
    }

    fun onDeleteConfirmed() {
        _productToDelete.value?.let { product ->
            viewModelScope.launch {
                repo.delete(product) // <-- ESTO AHORA FUNCIONARÁ
                _productToDelete.value = null
            }
        }
    }

    fun onDeleteCancelled() {
        _productToDelete.value = null
    }

    // --- LÓGICA PARA EDITAR ---
    fun loadProductForEdit(productId: Long) {
        viewModelScope.launch {
            val product = repo.getProduct(productId)
            if (product != null) {
                _formState.value = ProductForm(
                    id = product.id,
                    name = product.name,
                    price = product.price.toString(),
                    stock = product.stock.toString(),
                    category = product.category,
                    available = product.available,
                    photoUri = product.photoUri
                )
            }
        }
    }

    // --- LÓGICA PARA GUARDAR (NUEVO O ACTUALIZADO) ---
    fun saveOrUpdateProduct(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_formState.value.isValid) {
                val productEntity = _formState.value.toEntity()
                if (productEntity.id == 0L) {
                    repo.insert(productEntity)
                } else {
                    repo.update(productEntity) // <-- ESTO AHORA FUNCIONARÁ
                }
                onSuccess()
                clearForm()
            }
        }
    }

    fun clearForm() {
        _formState.value = ProductForm()
    }
}