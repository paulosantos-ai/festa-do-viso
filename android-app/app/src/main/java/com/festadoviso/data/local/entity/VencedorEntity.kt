package com.festadoviso.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para representar um Vencedor de Sorteio.
 *
 * Cada sexta-feira é sorteado o Euromilhões e o último número (1-49)
 * determina o vencedor da folha ativa dessa semana.
 */
@Entity(
    tableName = "vencedores",
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
        Index(value = ["dataSorteio"]),
        Index(value = ["dataRegisto"])
    ]
)
data class VencedorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * ID da folha vencedora
     */
    val folhaId: Long,

    /**
     * Nome da folha (desnormalizado para facilitar queries)
     */
    val folhaNome: String,

    /**
     * Data do sorteio do Euromilhões (timestamp em milissegundos)
     */
    val dataSorteio: Long,

    /**
     * Número vencedor (último número do Euromilhões, entre 1-49)
     */
    val numeroVencedor: Int,

    /**
     * Nome do vencedor (participante que comprou o número vencedor)
     */
    val vencedorNome: String,

    /**
     * Contacto do vencedor
     */
    val vencedorContacto: String,

    /**
     * Timestamp do registo do vencedor na app (em milissegundos)
     */
    val dataRegisto: Long = System.currentTimeMillis()
) {
    init {
        require(numeroVencedor in 1..49) { "Número vencedor deve estar entre 1 e 49" }
    }
}
