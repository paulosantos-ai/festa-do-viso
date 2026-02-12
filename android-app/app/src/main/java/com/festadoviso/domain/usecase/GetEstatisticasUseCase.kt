package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.FolhaRepository
import com.festadoviso.data.repository.RegistoRepository
import com.festadoviso.data.repository.VencedorRepository
import com.festadoviso.domain.model.Estatisticas
import javax.inject.Inject

/**
 * Use Case para obter estatísticas globais do sistema.
 */
class GetEstatisticasUseCase @Inject constructor(
    private val folhaRepository: FolhaRepository,
    private val registoRepository: RegistoRepository,
    private val vencedorRepository: VencedorRepository
) {
    suspend operator fun invoke(): Estatisticas {
        val totalFolhas = folhaRepository.countFolhas()
        val folhasAtivas = folhaRepository.countFolhasAtivas()
        val numerosVendidos = registoRepository.countTotalRegistos()
        val numerosDisponiveis = (totalFolhas * 49) - numerosVendidos
        val totalVencedores = vencedorRepository.countVencedores()

        return Estatisticas(
            totalFolhas = totalFolhas,
            folhasAtivas = folhasAtivas,
            numerosVendidos = numerosVendidos,
            numerosDisponiveis = numerosDisponiveis,
            totalVencedores = totalVencedores
        )
    }
}
