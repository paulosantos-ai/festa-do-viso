package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.VencedorRepository
import com.festadoviso.domain.model.Vencedor
import com.festadoviso.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use Case para obter todos os vencedores.
 */
class GetVencedoresUseCase @Inject constructor(
    private val vencedorRepository: VencedorRepository
) {
    operator fun invoke(): Flow<List<Vencedor>> {
        return vencedorRepository.getAllVencedores().map { vencedores ->
            vencedores.map { it.toDomain() }
        }
    }
}
