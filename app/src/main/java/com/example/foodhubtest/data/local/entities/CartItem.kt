package com.example.foodhubtest.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["productId"])
data class CartItem(
    val productId: Long,
    val quantity: Int
)