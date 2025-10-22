package com.example.foodhubtest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.foodhubtest.data.local.dao.OrderDao
import com.example.foodhubtest.data.local.dao.ProductDao
import com.example.foodhubtest.data.local.dao.UserDao
import com.example.foodhubtest.data.local.entities.CartItem
import com.example.foodhubtest.data.local.entities.Order
import com.example.foodhubtest.data.local.entities.Product
import com.example.foodhubtest.data.local.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, CartItem::class, Order::class, User::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun build(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodhub.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Clase privada para manejar la creación de la BD
    private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val userDao = database.userDao()
                    // Usuario Administrador
                    userDao.insert(
                        User(
                            name = "Admin1",
                            email = "admin@gmail.com",
                            passwordHash = "admin123",
                            role = "ADMIN"
                        )
                    )
                    // Usuario Cliente
                    userDao.insert(
                        User(
                            name = "Cliente1",
                            email = "cliente@gmail.com",
                            passwordHash = "cliente123",
                            role = "CLIENT"
                        )
                    )
                }
            }
        }
    }
}