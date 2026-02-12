package com.festadoviso.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para representar um utilizador Administrador.
 *
 * Os administradores têm acesso ao painel de gestão para:
 * - Criar/eliminar folhas
 * - Ver estatísticas
 * - Registar vencedores semanais
 */
@Entity(
    tableName = "admin_users",
    indices = [
        Index(value = ["username"], unique = true)
    ]
)
data class AdminEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Nome de utilizador do admin (único)
     */
    val username: String,

    /**
     * Hash bcrypt da password
     */
    val passwordHash: String,

    /**
     * Timestamp de criação da conta (em milissegundos)
     */
    val dataCriacao: Long = System.currentTimeMillis(),

    /**
     * Timestamp do último acesso (em milissegundos), null se nunca fez login
     */
    val ultimoAcesso: Long? = null
)
