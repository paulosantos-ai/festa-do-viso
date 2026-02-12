package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.FolhaRepository
import com.festadoviso.data.repository.RegistoRepository
import com.festadoviso.domain.model.Folha
import com.festadoviso.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use Case para obter folhas ativas com número de ocupados.
 */
class GetFolhasAtivasUseCase @Inject constructor(
    private val folhaRepository: FolhaRepository,
    private val registoRepository: RegistoRepository
) {
    operator fun invoke(): Flow<List<Folha>> {
        return folhaRepository.getFolhasAtivas().map { folhas ->
            folhas.map { folha ->
                val numerosOcupados = registoRepository.countRegistosByFolha(folha.id)
                folha.toDomain(numerosOcupados)
            }
        }
    }
}
