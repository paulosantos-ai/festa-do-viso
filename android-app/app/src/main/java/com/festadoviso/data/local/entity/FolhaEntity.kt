package com.festadoviso.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room para representar uma Folha de Sorteio.
 *
 * Cada folha contém 49 números (1-49) que podem ser vendidos aos participantes.
 * Apenas folhas ativas podem receber novos registos.
 */
@Entity(
    tableName = "folhas",
    indices = [
        Index(value = ["ativa"]),
        Index(value = ["dataCriacao"])
    ]
)
data class FolhaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * Nome da folha (ex: "Semana 1", "Páscoa 2024")
     */
    val nome: String,

    /**
     * Indica se a folha está ativa (pode receber novos registos)
     */
    val ativa: Boolean = true,

    /**
     * Timestamp de criação da folha (em milissegundos)
     */
    val dataCriacao: Long = System.currentTimeMillis()
)
