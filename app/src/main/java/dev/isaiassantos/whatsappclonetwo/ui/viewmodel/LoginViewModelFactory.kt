package dev.isaiassantos.whatsappclonetwo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.isaiassantos.whatsappclonetwo.data.local.database.AppDatabase
import dev.isaiassantos.whatsappclonetwo.data.repository.AuthRepository

class LoginViewModelFactory(
    private val repository: AuthRepository,
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository, database) as T
    }
}