package com.festadoviso.domain.model

import com.festadoviso.data.local.entity.RegistoEntity

/**
 * Domain model para Registo de Número.
 * Representa a venda de um número a um participante.
 */
data class Registo(
    val id: Long,
    val folhaId: Long,
    val numero: Int,
    val nome: String,
    val contacto: String,
    val dataRegisto: Long
)

/**
 * Converter RegistoEntity (BD) para Registo (Domain).
 */
fun RegistoEntity.toDomain(): Registo {
    return Registo(
        id = id,
        folhaId = folhaId,
        numero = numero,
        nome = nome,
        contacto = contacto,
        dataRegisto = dataRegisto
    )
}
