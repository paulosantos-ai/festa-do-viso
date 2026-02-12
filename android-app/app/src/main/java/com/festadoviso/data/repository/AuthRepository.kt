package com.festadoviso.data.repository

import com.festadoviso.data.local.dao.AdminDao
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerir autenticação de administradores.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val adminDao: AdminDao
) {
    /**
     * Fazer login com username e password.
     * @return true se as credenciais estiverem corretas, false caso contrário
     */
    suspend fun login(username: String, password: String): Boolean {
        val admin = adminDao.getByUsername(username) ?: return false

        // Verificar password com BCrypt
        val passwordCorrect = BCrypt.checkpw(password, admin.passwordHash)

        if (passwordCorrect) {
            // Atualizar timestamp do último acesso
            adminDao.updateUltimoAcesso(username, System.currentTimeMillis())
        }

        return passwordCorrect
    }

    /**
     * Verificar se existe pelo menos um admin.
     * Útil para validar seed data.
     */
    suspend fun hasAdmin(): Boolean {
        return adminDao.count() > 0
    }
}
