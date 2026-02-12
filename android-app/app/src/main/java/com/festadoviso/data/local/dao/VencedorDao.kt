package com.festadoviso.data.local.dao

import androidx.room.*
import com.festadoviso.data.local.entity.VencedorEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operações de base de dados relacionadas com Vencedores.
 */
@Dao
interface VencedorDao {

    /**
     * Obter todos os vencedores ordenados por data de sorteio (mais recentes primeiro).
     * Retorna Flow para observar mudanças automaticamente.
     */
    @Query("SELECT * FROM vencedores ORDER BY dataSorteio DESC, dataRegisto DESC")
    fun getAll(): Flow<List<VencedorEntity>>

    /**
     * Obter vencedores de uma folha específica.
     */
    @Query("SELECT * FROM vencedores WHERE folhaId = :folhaId ORDER BY dataSorteio DESC")
    fun getByFolha(folhaId: Long): Flow<List<VencedorEntity>>

    /**
     * Obter vencedor por ID.
     */
    @Query("SELECT * FROM vencedores WHERE id = :id")
    suspend fun getById(id: Long): VencedorEntity?

    /**
     * Inserir um novo vencedor.
     * Retorna o ID do vencedor criado.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vencedor: VencedorEntity): Long

    /**
     * Eliminar um vencedor.
     */
    @Delete
    suspend fun delete(vencedor: VencedorEntity)

    /**
     * Atualizar um vencedor.
     */
    @Update
    suspend fun update(vencedor: VencedorEntity)

    /**
     * Contar o número total de vencedores registados.
     */
    @Query("SELECT COUNT(*) FROM vencedores")
    suspend fun count(): Int

    /**
     * Verificar se já existe um vencedor para uma folha numa data específica.
     * Previne duplicação acidental.
     */
    @Query("SELECT * FROM vencedores WHERE folhaId = :folhaId AND dataSorteio = :dataSorteio")
    suspend fun getByFolhaEData(folhaId: Long, dataSorteio: Long): VencedorEntity?
}
