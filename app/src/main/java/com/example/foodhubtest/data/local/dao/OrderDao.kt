package com.example.foodhubtest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.foodhubtest.data.local.entities.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insert(order: Order): Long

    @Query("SELECT * FROM `Order` WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersForUser(userId: Long): Flow<List<Order>>
}