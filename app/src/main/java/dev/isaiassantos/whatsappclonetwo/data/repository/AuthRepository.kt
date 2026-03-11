package dev.isaiassantos.whatsappclonetwo.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
        private val auth: FirebaseAuth
    ) {

        suspend fun login(email: String, password: String): Result<String> {
            return try {

                val result = auth
                    .signInWithEmailAndPassword(email, password)
                    .await()

                val user = result.user

                if (user != null) {
                    Result.success(user.uid)
                } else {
                    Result.failure(Exception("Usuário inválido"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        fun isUserLogged(): Boolean = auth.currentUser != null

        fun logout() = auth.signOut()
    }