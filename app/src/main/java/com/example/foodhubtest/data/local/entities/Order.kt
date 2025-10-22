package com.example.foodhubtest.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["userId"])],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val total: Int
)