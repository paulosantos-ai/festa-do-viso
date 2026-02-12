package com.festadoviso.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.festadoviso.domain.model.Folha
import com.festadoviso.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ecrã de Administração - login e painel de gestão.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.isAuthenticated) {
        LoginScreen(
            onLogin = { username, password -> viewModel.login(username, password) },
            isLoading = uiState.isLoading,
            error = uiState.error
        )
    } else {
        AdminPanelScreen(
            uiState = uiState,
            onLogout = { viewModel.logout() },
            onCriarFolha = { viewModel.criarFolha(it) },
            onRegistarVencedor = { folha, data, numero ->
                viewModel.registarVencedor(folha.id, folha.nome, data, numero)
            },
            onToggleFolha = { viewModel.toggleFolhaAtiva(it) },
            onEliminarFolha = { viewModel.eliminarFolha(it) },
            onClearMessages = { viewModel.clearMessages() }
        )
    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    isLoading: Boolean,
    error: String?
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = VisoBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Painel de Administração",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    }
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = VisoRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onLogin(username, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = VisoBlue)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Entrar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    uiState: AdminUiState,
    onLogout: () -> Unit,
    onCriarFolha: (String) -> Unit,
    onRegistarVencedor: (Folha, Long, Int) -> Unit,
    onToggleFolha: (Folha) -> Unit,
    onEliminarFolha: (Folha) -> Unit,
    onClearMessages: () -> Unit
) {
    var showCriarFolhaDialog by remember { mutableStateOf(false) }
    var showRegistarVencedorDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administração") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VisoBlue,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sair",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showRegistarVencedorDialog = true },
                    containerColor = VisoGreen,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Registar Vencedor")
                }
                FloatingActionButton(
                    onClick = { showCriarFolhaDialog = true },
                    containerColor = VisoBlue
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Folha")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estatísticas
            item {
                uiState.estatisticas?.let { stats ->
                    EstatisticasCard(stats)
                }
            }

            // Lista de folhas
            item {
                Text(
                    "Folhas de Sorteio",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(uiState.folhas) { folha ->
                FolhaAdminCard(
                    folha = folha,
                    onToggle = { onToggleFolha(folha) },
                    onEliminar = { onEliminarFolha(folha) }
                )
            }
        }
    }

    // Diálogos
    if (showCriarFolhaDialog) {
        CriarFolhaDialog(
            onDismiss = { showCriarFolhaDialog = false },
            onConfirm = {
                onCriarFolha(it)
                showCriarFolhaDialog = false
            }
        )
    }

    if (showRegistarVencedorDialog) {
        RegistarVencedorDialog(
            folhas = uiState.folhas.filter { it.ativa },
            onDismiss = { showRegistarVencedorDialog = false },
            onConfirm = { folha, data, numero ->
                onRegistarVencedor(folha, data, numero)
                showRegistarVencedorDialog = false
            }
        )
    }

    // Mensagens
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            onClearMessages()
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            onClearMessages()
        }
    }
}

@Composable
fun EstatisticasCard(stats: com.festadoviso.domain.model.Estatisticas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = VisoBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Estatísticas",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Folhas", stats.totalFolhas.toString())
                StatItem("Vendidos", stats.numerosVendidos.toString())
                StatItem("Vencedores", stats.totalVencedores.toString())
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
        )
    }
}

@Composable
fun FolhaAdminCard(
    folha: Folha,
    onToggle: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    folha.nome,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "${folha.numerosOcupados}/49 números vendidos",
                    style = MaterialTheme.typography.bodySmall,
                    color = VisoGrey
                )
            }

            Row {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (folha.ativa) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (folha.ativa) "Desativar" else "Ativar",
                        tint = if (folha.ativa) VisoGreen else VisoGrey
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = VisoRed
                    )
                }
            }
        }
    }
}

@Composable
fun CriarFolhaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nome by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Folha de Sorteio") },
        text = {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da Folha") },
                placeholder = { Text("Ex: Semana 2, Páscoa 2024") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nome) },
                enabled = nome.trim().length >= 3
            ) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistarVencedorDialog(
    folhas: List<Folha>,
    onDismiss: () -> Unit,
    onConfirm: (Folha, Long, Int) -> Unit
) {
    var folhaSelecionada by remember { mutableStateOf<Folha?>(folhas.firstOrNull()) }
    var numeroVencedor by remember { mutableStateOf("") }
    var dataSorteio by remember { mutableStateOf(System.currentTimeMillis()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registar Vencedor") },
        text = {
            Column {
                // Selector de folha
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = folhaSelecionada?.nome ?: "Selecione",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Folha") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        folhas.forEach { folha ->
                            DropdownMenuItem(
                                text = { Text(folha.nome) },
                                onClick = {
                                    folhaSelecionada = folha
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = numeroVencedor,
                    onValueChange = { if (it.length <= 2) numeroVencedor = it },
                    label = { Text("Número Vencedor (1-49)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    folhaSelecionada?.let { folha ->
                        numeroVencedor.toIntOrNull()?.let { numero ->
                            if (numero in 1..49) {
                                onConfirm(folha, dataSorteio, numero)
                            }
                        }
                    }
                },
                enabled = folhaSelecionada != null &&
                        numeroVencedor.toIntOrNull() in 1..49
            ) {
                Text("Registar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
