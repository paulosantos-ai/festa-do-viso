package com.festadoviso.ui.vencedores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.festadoviso.domain.model.Vencedor
import com.festadoviso.domain.usecase.GetVencedoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para o ecrã de Vencedores.
 */
@HiltViewModel
class VencedoresViewModel @Inject constructor(
    private val getVencedoresUseCase: GetVencedoresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VencedoresUiState())
    val uiState: StateFlow<VencedoresUiState> = _uiState.asStateFlow()

    init {
        loadVencedores()
    }

    private fun loadVencedores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getVencedoresUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
                .collect { vencedores ->
                    _uiState.update {
                        it.copy(
                            vencedores = vencedores,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun refresh() {
        loadVencedores()
    }
}

data class VencedoresUiState(
    val vencedores: List<Vencedor> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
