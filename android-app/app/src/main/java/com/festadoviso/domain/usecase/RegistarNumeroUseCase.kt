package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.RegistoRepository
import javax.inject.Inject

/**
 * Use Case para registar um número (venda).
 */
class RegistarNumeroUseCase @Inject constructor(
    private val registoRepository: RegistoRepository
) {
    suspend operator fun invoke(
        folhaId: Long,
        numero: Int,
        nome: String,
        contacto: String
    ): Result<Long> {
        return registoRepository.registarNumero(folhaId, numero, nome, contacto)
    }
}
