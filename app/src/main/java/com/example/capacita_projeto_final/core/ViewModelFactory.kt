package com.example.capacita_projeto_final.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory<VM : ViewModel>(
    private val createViewModel: () -> VM,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = createViewModel() as T
}
