package dev.isaiassantos.whatsappclonetwo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.isaiassantos.whatsappclonetwo.data.local.database.AppDatabase
import dev.isaiassantos.whatsappclonetwo.data.local.entity.Usuario
import dev.isaiassantos.whatsappclonetwo.data.repository.AuthRepository
import dev.isaiassantos.whatsappclonetwo.ui.state.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val repository: AuthRepository,
    private val database: AppDatabase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)

            result.onSuccess { uid ->
                // Salva no banco local se necessário (Cache)
                withContext(Dispatchers.IO) {
                    database.usuarioDao().inserir(
                        Usuario(nome = "Usuário Logado", email = email)
                    )
                }
                _authState.value = AuthState.Success(uid)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Erro ao autenticar")
            }
        }
    }

    fun isUserLogged(): Boolean = repository.isUserLogged()
}