package com.example.proyectoandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoandroid.data.ShoppingItem
import com.example.proyectoandroid.data.ShoppingItemDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingItemDao: ShoppingItemDao
) : ViewModel() {

    val shoppingItems: Flow<List<ShoppingItem>> = shoppingItemDao.getAllItems()

    fun addItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemDao.insert(item)
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingItemDao.delete(item)
        }
    }
}