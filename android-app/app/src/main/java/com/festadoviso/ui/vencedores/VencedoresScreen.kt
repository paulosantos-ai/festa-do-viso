package com.festadoviso.ui.vencedores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.festadoviso.domain.model.Vencedor
import com.festadoviso.ui.theme.VisoBlue
import com.festadoviso.ui.theme.VisoGreen
import com.festadoviso.ui.theme.VisoGrey
import com.festadoviso.ui.theme.VisoOrange
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ecrã de Vencedores - lista de todos os vencedores semanais.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VencedoresScreen(
    viewModel: VencedoresViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vencedores") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VisoBlue,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.vencedores.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = VisoGrey
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Ainda não existem vencedores",
                            style = MaterialTheme.typography.titleMedium,
                            color = VisoGrey
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.vencedores) { vencedor ->
                            VencedorCard(vencedor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VencedorCard(vencedor: Vencedor) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "PT"))
    val dataSorteio = dateFormat.format(Date(vencedor.dataSorteio))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número vencedor
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = VisoOrange)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vencedor.numeroVencedor.toString(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info do vencedor
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = vencedor.vencedorNome,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vencedor.folhaNome,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VisoBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sorteio: $dataSorteio",
                    style = MaterialTheme.typography.bodySmall,
                    color = VisoGrey
                )
                Text(
                    text = "Tel: ${vencedor.vencedorContacto}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VisoGrey
                )
            }

            // Ícone troféu
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Vencedor",
                modifier = Modifier.size(32.dp),
                tint = VisoGreen
            )
        }
    }
}
