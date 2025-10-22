package com.example.foodhubtest.core.utils

data class FieldError(val message: String)

object Validators {

    fun name(v: String): FieldError? {
        val s = v.trim()
        return if (s.isEmpty()) FieldError("El nombre es obligatorio") else null
    }

    fun price(v: String): FieldError? {
        val value = v.trim().toIntOrNull() ?: return FieldError("Precio inválido")
        return if (value <= 0) FieldError("El precio debe ser mayor a 0") else null
    }

    fun stock(v: String): FieldError? {
        val value = v.trim().toIntOrNull() ?: return FieldError("Stock inválido")
        return if (value < 0) FieldError("Stock no puede ser negativo") else null
    }

    fun email(v: String): FieldError? {
        val s = v.trim()
        return when {
            s.isEmpty() -> FieldError("El correo es obligatorio")
            !s.contains("@") || !s.contains(".") -> FieldError("El formato del correo es inválido")
            else -> null
        }
    }

    fun password(v: String): FieldError? {
        return if (v.length < 6) FieldError("La contraseña debe tener al menos 6 caracteres") else null
    }
}