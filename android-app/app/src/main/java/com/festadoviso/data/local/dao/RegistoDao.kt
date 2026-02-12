package com.festadoviso.data.local.dao

import androidx.room.*
import com.festadoviso.data.local.entity.RegistoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operações de base de dados relacionadas com Registos.
 */
@Dao
interface RegistoDao {

    /**
     * Obter todos os registos de uma folha específica, ordenados por número.
     * Retorna Flow para observar mudanças automaticamente.
     */
    @Query("SELECT * FROM registos WHERE folhaId = :folhaId ORDER BY numero ASC")
    fun getByFolha(folhaId: Long): Flow<List<RegistoEntity>>

    /**
     * Verificar se um número específico já está ocupado numa folha.
     * Retorna null se estiver disponível.
     */
    @Query("SELECT * FROM registos WHERE folhaId = :folhaId AND numero = :numero")
    suspend fun getByNumero(folhaId: Long, numero: Int): RegistoEntity?

    /**
     * Contar quantos números foram vendidos numa folha.
     */
    @Query("SELECT COUNT(*) FROM registos WHERE folhaId = :folhaId")
    suspend fun countByFolha(folhaId: Long): Int

    /**
     * Obter lista de números ocupados numa folha.
     * Útil para mostrar grid de disponibilidade.
     */
    @Query("SELECT numero FROM registos WHERE folhaId = :folhaId")
    suspend fun getNumerosOcupados(folhaId: Long): List<Int>

    /**
     * Inserir um novo registo (venda de número).
     * Retorna o ID do registo criado.
     * Lança exceção se o número já estiver ocupado (UNIQUE constraint).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(registo: RegistoEntity): Long

    /**
     * Eliminar um registo.
     */
    @Delete
    suspend fun delete(registo: RegistoEntity)

    /**
     * Obter todos os registos (útil para estatísticas globais).
     */
    @Query("SELECT * FROM registos ORDER BY dataRegisto DESC")
    fun getAll(): Flow<List<RegistoEntity>>

    /**
     * Contar o total de números vendidos em todas as folhas.
     */
    @Query("SELECT COUNT(*) FROM registos")
    suspend fun countTotal(): Int
}
