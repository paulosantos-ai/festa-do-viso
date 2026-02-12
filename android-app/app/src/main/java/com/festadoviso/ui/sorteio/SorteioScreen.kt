package com.festadoviso.ui.sorteio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.festadoviso.ui.theme.VisoBlue
import com.festadoviso.ui.theme.VisoGreen
import com.festadoviso.ui.theme.VisoGrey
import com.festadoviso.ui.theme.VisoRed

/**
 * Ecrã principal de Sorteio onde os participantes escolhem números.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SorteioScreen(
    viewModel: SorteioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // Mostrar diálogo de registo quando um número é selecionado
    LaunchedEffect(uiState.numeroSelecionado) {
        if (uiState.numeroSelecionado != null) {
            showDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festa do Viso - Sorteio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VisoBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Seletor de folha
            if (uiState.folhas.isNotEmpty()) {
                FolhaSelector(
                    folhas = uiState.folhas,
                    folhaSelecionada = uiState.folhaSelecionada,
                    onFolhaSelected = { viewModel.selecionarFolha(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info da folha
                uiState.folhaSelecionada?.let { folha ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Disponíveis: ${49 - uiState.numerosOcupados.size}/49",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Vendidos: ${uiState.numerosOcupados.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = VisoGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grid de números 1-49
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items((1..49).toList()) { numero ->
                        NumeroButton(
                            numero = numero,
                            isOcupado = uiState.numerosOcupados.contains(numero),
                            onClick = { viewModel.selecionarNumero(numero) }
                        )
                    }
                }
            } else {
                // Sem folhas ativas
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Não existem folhas ativas no momento",
                        style = MaterialTheme.typography.titleMedium,
                        color = VisoGrey
                    )
                }
            }
        }

        // Diálogo de registo
        if (showDialog && uiState.numeroSelecionado != null) {
            RegistoDialog(
                numero = uiState.numeroSelecionado!!,
                onDismiss = {
                    showDialog = false
                    viewModel.limparSelecao()
                },
                onConfirm = { nome, contacto ->
                    viewModel.registarNumero(nome, contacto)
                    showDialog = false
                },
                isLoading = uiState.isLoading
            )
        }

        // Snackbar de sucesso/erro
        uiState.successMessage?.let { message ->
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(2000)
                viewModel.clearMessages()
            }
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = VisoGreen
            ) {
                Text(message)
            }
        }

        uiState.error?.let { error ->
            LaunchedEffect(error) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearMessages()
            }
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = VisoRed
            ) {
                Text(error)
            }
        }
    }
}

@Composable
fun NumeroButton(
    numero: Int,
    isOcupado: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = { if (!isOcupado) onClick() },
        modifier = Modifier
            .aspectRatio(1f)
            .size(48.dp),
        enabled = !isOcupado,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOcupado) VisoGrey else VisoBlue,
            disabledContainerColor = VisoGrey
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isOcupado) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Ocupado",
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = numero.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolhaSelector(
    folhas: List<com.festadoviso.domain.model.Folha>,
    folhaSelecionada: com.festadoviso.domain.model.Folha?,
    onFolhaSelected: (com.festadoviso.domain.model.Folha) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = folhaSelecionada?.nome ?: "Selecione uma folha",
            onValueChange = {},
            readOnly = true,
            label = { Text("Folha de Sorteio") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            folhas.forEach { folha ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(folha.nome, fontWeight = FontWeight.Bold)
                            Text(
                                "${folha.numerosOcupados}/49 ocupados",
                                style = MaterialTheme.typography.bodySmall,
                                color = VisoGrey
                            )
                        }
                    },
                    onClick = {
                        onFolhaSelected(folha)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RegistoDialog(
    numero: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean
) {
    var nome by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registar Número $numero") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contacto,
                    onValueChange = { if (it.length <= 9) contacto = it },
                    label = { Text("Contacto (9 dígitos)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nome, contacto) },
                enabled = !isLoading && nome.length >= 3 && contacto.length == 9
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}
