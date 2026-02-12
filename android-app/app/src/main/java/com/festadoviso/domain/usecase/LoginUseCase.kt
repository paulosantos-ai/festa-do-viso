package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case para fazer login como administrador.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Boolean {
        return authRepository.login(username, password)
    }
}
