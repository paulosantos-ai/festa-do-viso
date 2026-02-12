package com.festadoviso.data.repository

import com.festadoviso.data.local.dao.FolhaDao
import com.festadoviso.data.local.entity.FolhaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerir operações relacionadas com Folhas.
 * Abstrai a fonte de dados (Room) da camada de domínio.
 */
@Singleton
class FolhaRepository @Inject constructor(
    private val folhaDao: FolhaDao
) {
    /**
     * Obter todas as folhas como Flow (observável).
     */
    fun getAllFolhas(): Flow<List<FolhaEntity>> = folhaDao.getAll()

    /**
     * Obter apenas folhas ativas.
     */
    fun getFolhasAtivas(): Flow<List<FolhaEntity>> = folhaDao.getAtivas()

    /**
     * Obter uma folha por ID.
     */
    suspend fun getFolhaById(id: Long): FolhaEntity? = folhaDao.getById(id)

    /**
     * Criar uma nova folha.
     * @return ID da folha criada
     */
    suspend fun criarFolha(nome: String): Long {
        val folha = FolhaEntity(nome = nome.trim(), ativa = true)
        return folhaDao.insert(folha)
    }

    /**
     * Eliminar uma folha.
     * Valida que não é a última folha.
     */
    suspend fun eliminarFolha(folha: FolhaEntity): Result<Unit> {
        return try {
            val totalFolhas = folhaDao.count()
            if (totalFolhas <= 1) {
                Result.failure(Exception("Não pode eliminar a única folha existente"))
            } else {
                folhaDao.delete(folha)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ativar/desativar uma folha.
     */
    suspend fun toggleAtiva(folha: FolhaEntity) {
        val updated = folha.copy(ativa = !folha.ativa)
        folhaDao.update(updated)
    }

    /**
     * Contar total de folhas.
     */
    suspend fun countFolhas(): Int = folhaDao.count()

    /**
     * Contar folhas ativas.
     */
    suspend fun countFolhasAtivas(): Int = folhaDao.countAtivas()
}
