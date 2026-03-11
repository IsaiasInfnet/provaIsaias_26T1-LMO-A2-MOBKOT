package dev.isaiassantos.whatsappclonetwo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.isaiassantos.whatsappclonetwo.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun inserir(usuario: Usuario)

    @Query("SELECT * FROM usuario")
    suspend fun listar(): List<Usuario>
}