package com.festadoviso.data.local.dao

import androidx.room.*
import com.festadoviso.data.local.entity.AdminEntity

/**
 * DAO (Data Access Object) para operações de base de dados relacionadas com Administradores.
 */
@Dao
interface AdminDao {

    /**
     * Obter um admin pelo username.
     * Retorna null se não existir.
     */
    @Query("SELECT * FROM admin_users WHERE username = :username")
    suspend fun getByUsername(username: String): AdminEntity?

    /**
     * Inserir um novo administrador.
     * Retorna o ID do admin criado.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(admin: AdminEntity): Long

    /**
     * Atualizar um administrador existente.
     */
    @Update
    suspend fun update(admin: AdminEntity)

    /**
     * Atualizar o timestamp do último acesso de um admin.
     */
    @Query("UPDATE admin_users SET ultimoAcesso = :timestamp WHERE username = :username")
    suspend fun updateUltimoAcesso(username: String, timestamp: Long)

    /**
     * Verificar se existe pelo menos um admin na base de dados.
     */
    @Query("SELECT COUNT(*) FROM admin_users")
    suspend fun count(): Int
}
