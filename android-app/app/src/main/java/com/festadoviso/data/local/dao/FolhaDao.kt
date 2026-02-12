package com.festadoviso.data.local.dao

import androidx.room.*
import com.festadoviso.data.local.entity.FolhaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operações de base de dados relacionadas com Folhas.
 */
@Dao
interface FolhaDao {

    /**
     * Obter todas as folhas ordenadas por data de criação (mais recentes primeiro).
     * Retorna Flow para observar mudanças automaticamente.
     */
    @Query("SELECT * FROM folhas ORDER BY dataCriacao DESC")
    fun getAll(): Flow<List<FolhaEntity>>

    /**
     * Obter apenas folhas ativas ordenadas por data de criação.
     */
    @Query("SELECT * FROM folhas WHERE ativa = 1 ORDER BY dataCriacao DESC")
    fun getAtivas(): Flow<List<FolhaEntity>>

    /**
     * Obter uma folha específica pelo ID.
     * Retorna null se não existir.
     */
    @Query("SELECT * FROM folhas WHERE id = :id")
    suspend fun getById(id: Long): FolhaEntity?

    /**
     * Inserir uma nova folha.
     * Retorna o ID da folha criada.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(folha: FolhaEntity): Long

    /**
     * Eliminar uma folha.
     * Todos os registos associados serão eliminados automaticamente (CASCADE).
     */
    @Delete
    suspend fun delete(folha: FolhaEntity)

    /**
     * Atualizar uma folha existente.
     */
    @Update
    suspend fun update(folha: FolhaEntity)

    /**
     * Contar o número total de folhas.
     */
    @Query("SELECT COUNT(*) FROM folhas")
    suspend fun count(): Int

    /**
     * Contar o número de folhas ativas.
     */
    @Query("SELECT COUNT(*) FROM folhas WHERE ativa = 1")
    suspend fun countAtivas(): Int
}
