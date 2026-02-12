package com.festadoviso.domain.usecase

import com.festadoviso.data.repository.RegistoRepository
import com.festadoviso.data.repository.VencedorRepository
import javax.inject.Inject

/**
 * Use Case para registar um vencedor.
 * Valida que o número existe na folha.
 */
class RegistarVencedorUseCase @Inject constructor(
    private val vencedorRepository: VencedorRepository,
    private val registoRepository: RegistoRepository
) {
    suspend operator fun invoke(
        folhaId: Long,
        folhaNome: String,
        dataSorteio: Long,
        numeroVencedor: Int
    ): Result<Long> {
        // Verificar se o número foi vendido
        val registo = registoRepository.getRegistoByNumero(folhaId, numeroVencedor)
            ?: return Result.failure(Exception("Número $numeroVencedor não foi vendido nesta folha"))

        // Registar vencedor
        return vencedorRepository.registarVencedor(
            folhaId = folhaId,
            folhaNome = folhaNome,
            dataSorteio = dataSorteio,
            numeroVencedor = numeroVencedor,
            vencedorNome = registo.nome,
            vencedorContacto = registo.contacto
        )
    }
}
