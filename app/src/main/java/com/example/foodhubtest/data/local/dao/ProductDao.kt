package com.example.foodhubtest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.foodhubtest.data.local.entities.CartItem
import com.example.foodhubtest.data.local.entities.Order
import com.example.foodhubtest.data.local.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // --- Productos ---
    @Query("SELECT * FROM Product ORDER BY id DESC")
    fun observeProducts(): Flow<List<Product>>

    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM Product WHERE id = :id")
    suspend fun getById(id: Long): Product?

    // --- Carrito ---
    @Query("SELECT * FROM CartItem")
    fun observeCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM CartItem")
    suspend fun clearCart()

    @Insert
    suspend fun insertOrder(order: Order): Long
}