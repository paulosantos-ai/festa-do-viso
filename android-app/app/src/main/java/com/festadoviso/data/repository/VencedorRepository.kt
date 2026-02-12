package com.festadoviso.data.repository

import com.festadoviso.data.local.dao.VencedorDao
import com.festadoviso.data.local.entity.VencedorEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerir operações relacionadas com Vencedores.
 */
@Singleton
class VencedorRepository @Inject constructor(
    private val vencedorDao: VencedorDao
) {
    /**
     * Obter todos os vencedores ordenados por data de sorteio.
     */
    fun getAllVencedores(): Flow<List<VencedorEntity>> =
        vencedorDao.getAll()

    /**
     * Obter vencedores de uma folha específica.
     */
    fun getVencedoresByFolha(folhaId: Long): Flow<List<VencedorEntity>> =
        vencedorDao.getByFolha(folhaId)

    /**
     * Registar um vencedor.
     * Valida que o número está entre 1-49.
     */
    suspend fun registarVencedor(
        folhaId: Long,
        folhaNome: String,
        dataSorteio: Long,
        numeroVencedor: Int,
        vencedorNome: String,
        vencedorContacto: String
    ): Result<Long> {
        return try {
            // Validação
            if (numeroVencedor !in 1..49) {
                return Result.failure(Exception("Número vencedor deve estar entre 1 e 49"))
            }

            // Verificar duplicação
            val existente = vencedorDao.getByFolhaEData(folhaId, dataSorteio)
            if (existente != null) {
                return Result.failure(
                    Exception("Já existe um vencedor registado para esta folha nesta data")
                )
            }

            // Inserir vencedor
            val vencedor = VencedorEntity(
                folhaId = folhaId,
                folhaNome = folhaNome,
                dataSorteio = dataSorteio,
                numeroVencedor = numeroVencedor,
                vencedorNome = vencedorNome,
                vencedorContacto = vencedorContacto
            )
            val id = vencedorDao.insert(vencedor)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Contar total de vencedores registados.
     */
    suspend fun countVencedores(): Int =
        vencedorDao.count()

    /**
     * Eliminar um vencedor.
     */
    suspend fun eliminarVencedor(vencedor: VencedorEntity) {
        vencedorDao.delete(vencedor)
    }
}
