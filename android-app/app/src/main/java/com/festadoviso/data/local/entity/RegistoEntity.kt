package com.festadoviso.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para representar um Registo de Número.
 *
 * Cada registo associa um número (1-49) a um participante numa folha específica.
 * O par (folhaId, numero) é único - nenhum número pode ser vendido duas vezes na mesma folha.
 */
@Entity(
    tableName = "registos",
    foreignKeys = [
        ForeignKey(
            entity = FolhaEntity::class,
            parentColumns = ["id"],
            childColumns = ["folhaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["folhaId"]),
        Index(value = ["numero"]),
        Index(value = ["folhaId", "numero"], unique = true),
        Index(value = ["dataRegisto"])
    ]
)
data class RegistoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * ID da folha a que este registo pertence
     */
    val folhaId: Long,

    /**
     * Número escolhido pelo participante (1-49)
     */
    val numero: Int,

    /**
     * Nome completo do participante
     */
    val nome: String,

    /**
     * Contacto telefónico do participante (9 dígitos)
     */
    val contacto: String,

    /**
     * Timestamp do registo (em milissegundos)
     */
    val dataRegisto: Long = System.currentTimeMillis()
) {
    init {
        require(numero in 1..49) { "Número deve estar entre 1 e 49" }
        require(contacto.matches(Regex("^[0-9]{9}$"))) { "Contacto deve ter exatamente 9 dígitos" }
        require(nome.trim().isNotEmpty()) { "Nome não pode estar vazio" }
    }
}
