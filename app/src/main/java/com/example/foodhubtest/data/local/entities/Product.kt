package com.example.foodhubtest.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Int,
    val stock: Int,
    val category: String,
    val available: Boolean,
    val photoUri: String? = null
)