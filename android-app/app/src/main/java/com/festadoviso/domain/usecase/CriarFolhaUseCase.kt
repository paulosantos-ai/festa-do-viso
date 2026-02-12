package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.FolhaRepository
import javax.inject.Inject

/**
 * Use Case para criar uma nova folha de sorteio.
 */
class CriarFolhaUseCase @Inject constructor(
    private val folhaRepository: FolhaRepository
) {
    suspend operator fun invoke(nome: String): Long {
        if (nome.trim().length < 3) {
            throw IllegalArgumentException("Nome deve ter pelo menos 3 caracteres")
        }
        return folhaRepository.criarFolha(nome)
    }
}
