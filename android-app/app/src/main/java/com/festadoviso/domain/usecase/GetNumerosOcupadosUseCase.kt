package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.RegistoRepository
import javax.inject.Inject

/**
 * Use Case para obter lista de números ocupados numa folha.
 */
class GetNumerosOcupadosUseCase @Inject constructor(
    private val registoRepository: RegistoRepository
) {
    suspend operator fun invoke(folhaId: Long): List<Int> {
        return registoRepository.getNumerosOcupados(folhaId)
    }
}
