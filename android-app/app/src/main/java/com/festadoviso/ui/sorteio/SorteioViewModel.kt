package com.festadoviso.ui.sorteio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.festadoviso.domain.model.Folha
import com.festadoviso.domain.usecase.GetFolhasAtivasUseCase
import com.festadoviso.domain.usecase.GetNumerosOcupadosUseCase
import com.festadoviso.domain.usecase.RegistarNumeroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para o ecrã de Sorteio (participação).
 */
@HiltViewModel
class SorteioViewModel @Inject constructor(
    private val getFolhasAtivasUseCase: GetFolhasAtivasUseCase,
    private val getNumerosOcupadosUseCase: GetNumerosOcupadosUseCase,
    private val registarNumeroUseCase: RegistarNumeroUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SorteioUiState())
    val uiState: StateFlow<SorteioUiState> = _uiState.asStateFlow()

    init {
        loadFolhasAtivas()
    }

    private fun loadFolhasAtivas() {
        viewModelScope.launch {
            getFolhasAtivasUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { folhas ->
                    _uiState.update {
                        it.copy(
                            folhas = folhas,
                            folhaSelecionada = folhas.firstOrNull()
                        )
                    }
                    // Carregar números ocupados da primeira folha
                    folhas.firstOrNull()?.let { folha ->
                        loadNumerosOcupados(folha.id)
                    }
                }
        }
    }

    fun selecionarFolha(folha: Folha) {
        _uiState.update { it.copy(folhaSelecionada = folha) }
        loadNumerosOcupados(folha.id)
    }

    private fun loadNumerosOcupados(folhaId: Long) {
        viewModelScope.launch {
            val ocupados = getNumerosOcupadosUseCase(folhaId)
            _uiState.update { it.copy(numerosOcupados = ocupados.toSet()) }
        }
    }

    fun selecionarNumero(numero: Int) {
        val ocupado = _uiState.value.numerosOcupados.contains(numero)
        if (!ocupado) {
            _uiState.update { it.copy(numeroSelecionado = numero) }
        }
    }

    fun limparSelecao() {
        _uiState.update { it.copy(numeroSelecionado = null) }
    }

    fun registarNumero(nome: String, contacto: String) {
        val state = _uiState.value
        val folha = state.folhaSelecionada
        val numero = state.numeroSelecionado

        if (folha == null || numero == null) {
            _uiState.update { it.copy(error = "Selecione uma folha e um número") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            registarNumeroUseCase(folha.id, numero, nome, contacto)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            numeroSelecionado = null,
                            successMessage = "Número $numero registado com sucesso!"
                        )
                    }
                    // Recarregar números ocupados
                    loadNumerosOcupados(folha.id)
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

data class SorteioUiState(
    val folhas: List<Folha> = emptyList(),
    val folhaSelecionada: Folha? = null,
    val numerosOcupados: Set<Int> = emptySet(),
    val numeroSelecionado: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
