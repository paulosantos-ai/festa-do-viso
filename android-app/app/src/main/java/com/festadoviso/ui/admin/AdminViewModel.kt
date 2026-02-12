package com.festadoviso.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.festadoviso.data.repository.FolhaRepository
import com.festadoviso.domain.model.Estatisticas
import com.festadoviso.domain.model.Folha
import com.festadoviso.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para o painel de Administração.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getEstatisticasUseCase: GetEstatisticasUseCase,
    private val criarFolhaUseCase: CriarFolhaUseCase,
    private val registarVencedorUseCase: RegistarVencedorUseCase,
    private val folhaRepository: FolhaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val success = loginUseCase(username, password)

            if (success) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        isLoading = false
                    )
                }
                loadEstatisticas()
                loadFolhas()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Credenciais inválidas"
                    )
                }
            }
        }
    }

    fun logout() {
        _uiState.update { AdminUiState() }
    }

    private fun loadEstatisticas() {
        viewModelScope.launch {
            val stats = getEstatisticasUseCase()
            _uiState.update { it.copy(estatisticas = stats) }
        }
    }

    private fun loadFolhas() {
        viewModelScope.launch {
            folhaRepository.getAllFolhas()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { folhas ->
                    _uiState.update { it.copy(folhas = folhas) }
                }
        }
    }

    fun criarFolha(nome: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                criarFolhaUseCase(nome)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Folha '$nome' criada com sucesso!"
                    )
                }
                loadEstatisticas()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun registarVencedor(
        folhaId: Long,
        folhaNome: String,
        dataSorteio: Long,
        numeroVencedor: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            registarVencedorUseCase(folhaId, folhaNome, dataSorteio, numeroVencedor)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Vencedor registado com sucesso!"
                        )
                    }
                    loadEstatisticas()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    fun toggleFolhaAtiva(folha: Folha) {
        viewModelScope.launch {
            folhaRepository.toggleAtiva(folha)
            loadEstatisticas()
        }
    }

    fun eliminarFolha(folha: Folha) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            folhaRepository.eliminarFolha(folha)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Folha eliminada"
                        )
                    }
                    loadEstatisticas()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

data class AdminUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val estatisticas: Estatisticas? = null,
    val folhas: List<Folha> = emptyList()
)
