package com.festadoviso.domain.model

import com.festadoviso.data.local.entity.VencedorEntity

/**
 * Domain model para Vencedor de Sorteio.
 * Representa o vencedor de uma folha numa data específica.
 */
data class Vencedor(
    val id: Long,
    val folhaId: Long,
    val folhaNome: String,
    val dataSorteio: Long,
    val numeroVencedor: Int,
    val vencedorNome: String,
    val vencedorContacto: String,
    val dataRegisto: Long
)

/**
 * Converter VencedorEntity (BD) para Vencedor (Domain).
 */
fun VencedorEntity.toDomain(): Vencedor {
    return Vencedor(
        id = id,
        folhaId = folhaId,
        folhaNome = folhaNome,
        dataSorteio = dataSorteio,
        numeroVencedor = numeroVencedor,
        vencedorNome = vencedorNome,
        vencedorContacto = vencedorContacto,
        dataRegisto = dataRegisto
    )
}
