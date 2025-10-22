package com.example.foodhubtest.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String, // Nunca guardes contraseñas en texto plano
    val role: String = "CLIENT" // Por defecto, todos son clientes
)