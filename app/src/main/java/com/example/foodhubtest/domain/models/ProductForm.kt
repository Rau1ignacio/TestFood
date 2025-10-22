package com.example.foodhubtest.domain.models

import com.example.foodhubtest.core.utils.FieldError
import com.example.foodhubtest.core.utils.Validators
import com.example.foodhubtest.data.local.entities.Product

data class ProductForm(
    val id: Long = 0,
    val name: String = "",
    val price: String = "",
    val stock: String = "",
    val category: String = "",
    val available: Boolean = true,
    val photoUri: String? = null,
) {
    fun validate(): Map<String, FieldError> = buildMap {
        Validators.name(name)?.let { put("name", it) }
        Validators.price(price)?.let { put("price", it) }
        Validators.stock(stock)?.let { put("stock", it) }
    }

    val isValid: Boolean get() = validate().isEmpty()

    /** Útil al guardar después de validar */
    fun toEntity(): Product = Product(
        id = id,
        name = name.trim(),
        price = price.trim().toInt(),
        stock = stock.trim().toInt(),
        category = category.trim(),
        available = available,
        photoUri = photoUri
    )
}
