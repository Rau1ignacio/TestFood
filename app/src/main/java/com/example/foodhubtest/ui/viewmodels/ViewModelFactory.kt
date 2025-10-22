package com.example.foodhubtest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodhubtest.data.repository.FoodRepository

class ViewModelFactory(private val repo: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AdminVM::class.java) -> AdminVM(repo) as T
            modelClass.isAssignableFrom(HomeVM::class.java) -> HomeVM(repo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class provided to ViewModelFactory: ${modelClass.name}")
        }
    }
}