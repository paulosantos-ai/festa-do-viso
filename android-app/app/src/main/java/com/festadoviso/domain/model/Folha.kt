package com.festadoviso.domain.model

import com.festadoviso.data.local.entity.FolhaEntity

/**
 * Domain model para Folha de Sorteio.
 * Representa os dados de uma folha na camada de domínio/UI.
 */
data class Folha(
    val id: Long,
    val nome: String,
    val ativa: Boolean,
    val dataCriacao: Long,
    val numerosOcupados: Int = 0  // Calculado dinamicamente
)

/**
 * Converter FolhaEntity (BD) para Folha (Domain).
 */
fun FolhaEntity.toDomain(numerosOcupados: Int = 0): Folha {
    return Folha(
        id = id,
        nome = nome,
        ativa = ativa,
        dataCriacao = dataCriacao,
        numerosOcupados = numerosOcupados
    )
}
