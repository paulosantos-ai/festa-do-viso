package com.festadoviso.data.repository

import com.festadoviso.data.local.dao.RegistoDao
import com.festadoviso.data.local.entity.RegistoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerir operações relacionadas com Registos de Números.
 */
@Singleton
class RegistoRepository @Inject constructor(
    private val registoDao: RegistoDao
) {
    /**
     * Obter todos os registos de uma folha específica.
     */
    fun getRegistosByFolha(folhaId: Long): Flow<List<RegistoEntity>> =
        registoDao.getByFolha(folhaId)

    /**
     * Verificar se um número está disponível numa folha.
     * @return true se disponível, false se ocupado
     */
    suspend fun isNumeroDisponivel(folhaId: Long, numero: Int): Boolean {
        return registoDao.getByNumero(folhaId, numero) == null
    }

    /**
     * Obter lista de números ocupados numa folha.
     */
    suspend fun getNumerosOcupados(folhaId: Long): List<Int> =
        registoDao.getNumerosOcupados(folhaId)

    /**
     * Registar um número (venda).
     * Valida dados antes de inserir.
     */
    suspend fun registarNumero(
        folhaId: Long,
        numero: Int,
        nome: String,
        contacto: String
    ): Result<Long> {
        return try {
            // Validações
            if (numero !in 1..49) {
                return Result.failure(Exception("Número deve estar entre 1 e 49"))
            }
            if (nome.trim().length < 3) {
                return Result.failure(Exception("Nome deve ter pelo menos 3 caracteres"))
            }
            if (!contacto.matches(Regex("^[0-9]{9}$"))) {
                return Result.failure(Exception("Contacto deve ter exatamente 9 dígitos"))
            }

            // Verificar disponibilidade
            if (!isNumeroDisponivel(folhaId, numero)) {
                return Result.failure(Exception("Número $numero já está ocupado"))
            }

            // Inserir registo
            val registo = RegistoEntity(
                folhaId = folhaId,
                numero = numero,
                nome = nome.trim(),
                contacto = contacto
            )
            val id = registoDao.insert(registo)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obter registo de um número específico.
     */
    suspend fun getRegistoByNumero(folhaId: Long, numero: Int): RegistoEntity? =
        registoDao.getByNumero(folhaId, numero)

    /**
     * Contar números vendidos numa folha.
     */
    suspend fun countRegistosByFolha(folhaId: Long): Int =
        registoDao.countByFolha(folhaId)

    /**
     * Contar total de números vendidos (todas as folhas).
     */
    suspend fun countTotalRegistos(): Int =
        registoDao.countTotal()

    /**
     * Obter todos os registos (para estatísticas).
     */
    fun getAllRegistos(): Flow<List<RegistoEntity>> =
        registoDao.getAll()
}
