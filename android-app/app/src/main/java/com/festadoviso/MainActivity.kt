package com.festadoviso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataManager = DataManager(this)
        dataManager.inicializarDados()

        setContent {
            FestaDoVisoTheme {
                AppNavigation(dataManager)
            }
        }
    }
}

@Composable
fun FestaDoVisoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2196F3),
            secondary = Color(0xFF4CAF50),
            tertiary = Color(0xFFFF9800)
        ),
        content = content
    )
}

@Composable
fun AppNavigation(dataManager: DataManager) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.GridOn, null) },
                    label = { Text("Sorteio") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate("sorteio") {
                            popUpTo("sorteio") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EmojiEvents, null) },
                    label = { Text("Vencedores") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("vencedores") {
                            popUpTo("sorteio")
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AdminPanelSettings, null) },
                    label = { Text("Admin") },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate("admin") {
                            popUpTo("sorteio")
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "sorteio",
            modifier = Modifier.padding(padding)
        ) {
            composable("sorteio") { SorteioScreen(dataManager) }
            composable("vencedores") { VencedoresScreen(dataManager) }
            composable("admin") { AdminScreen(dataManager) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SorteioScreen(dataManager: DataManager) {
    var folhas by remember { mutableStateOf(dataManager.getFolhasAtivas()) }
    var folhaSelecionada by remember { mutableStateOf(folhas.firstOrNull()) }
    var numerosOcupados by remember { mutableStateOf(setOf<Int>()) }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(folhaSelecionada) {
        folhaSelecionada?.let {
            numerosOcupados = dataManager.getNumerosOcupados(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festa do Viso - Sorteio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
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
            // Selector de folha
            if (folhas.isNotEmpty()) {
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
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
                                            "${numerosOcupados.size}/49 ocupados",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                },
                                onClick = {
                                    folhaSelecionada = folha
                                    numerosOcupados = dataManager.getNumerosOcupados(folha.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info
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
                            "Disponíveis: ${49 - numerosOcupados.size}/49",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Vendidos: ${numerosOcupados.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grid números
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items((1..49).toList()) { numero ->
                        val isOcupado = numerosOcupados.contains(numero)
                        Button(
                            onClick = {
                                if (!isOcupado) {
                                    selectedNumber = numero
                                    showDialog = true
                                }
                            },
                            modifier = Modifier.aspectRatio(1f),
                            enabled = !isOcupado,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOcupado) Color.Gray else Color(0xFF2196F3),
                                disabledContainerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isOcupado) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    numero.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Não existem folhas ativas", color = Color.Gray)
                }
            }
        }

        // Dialog registo
        if (showDialog && selectedNumber != null && folhaSelecionada != null) {
            var nome by remember { mutableStateOf("") }
            var contacto by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Registar Número $selectedNumber") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text("Nome Completo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = contacto,
                            onValueChange = { if (it.length <= 9) contacto = it },
                            label = { Text("Contacto (9 dígitos)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val sucesso = dataManager.registarNumero(
                                folhaSelecionada!!.id,
                                selectedNumber!!,
                                nome,
                                contacto
                            )
                            if (sucesso) {
                                numerosOcupados = dataManager.getNumerosOcupados(folhaSelecionada!!.id)
                                showSuccessMessage = true
                                errorMessage = null
                            } else {
                                errorMessage = "Número já ocupado"
                            }
                            showDialog = false
                        },
                        enabled = nome.length >= 3 && contacto.length == 9
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Snackbar
        if (showSuccessMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSuccessMessage = false
            }
        }

        if (errorMessage != null) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                errorMessage = null
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VencedoresScreen(dataManager: DataManager) {
    var vencedores by remember { mutableStateOf(dataManager.getVencedores()) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "PT"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vencedores") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { vencedores = dataManager.getVencedores() }) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (vencedores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ainda não existem vencedores", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vencedores) { vencedor ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                modifier = Modifier.size(60.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFF9800)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        vencedor.numeroVencedor.toString(),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    vencedor.vencedorNome,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    vencedor.folhaNome,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF2196F3)
                                )
                                Text(
                                    "Sorteio: ${dateFormat.format(Date(vencedor.dataSorteio))}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    "Tel: ${vencedor.vencedorContacto}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Icon(
                                Icons.Default.EmojiEvents,
                                null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(dataManager: DataManager) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (!isLoggedIn) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Administração") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2196F3),
                        titleContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF2196F3)
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
                            leadingIcon = { Icon(Icons.Default.Person, null) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, null) }
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMessage!!, color = Color.Red)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (dataManager.verificarLogin(username, password)) {
                                    isLoggedIn = true
                                    errorMessage = null
                                } else {
                                    errorMessage = "Credenciais inválidas"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Entrar")
                        }
                    }
                }
            }
        }
    } else {
        AdminPanelScreen(dataManager, onLogout = { isLoggedIn = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(dataManager: DataManager, onLogout: () -> Unit) {
    var folhas by remember { mutableStateOf(dataManager.getFolhas()) }
    var showCriarFolhaDialog by remember { mutableStateOf(false) }
    var showRegistarVencedorDialog by remember { mutableStateOf(false) }
    var totalNumerosVendidos by remember { mutableStateOf(dataManager.getRegistos().size) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administração") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Sair", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showRegistarVencedorDialog = true },
                    containerColor = Color(0xFF4CAF50),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.EmojiEvents, "Registar Vencedor")
                }
                FloatingActionButton(
                    onClick = { showCriarFolhaDialog = true },
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.Add, "Nova Folha")
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3)
                    )
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    folhas.size.toString(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text("Folhas", color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    totalNumerosVendidos.toString(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text("Vendidos", color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    dataManager.getVencedores().size.toString(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text("Vencedores", color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Folhas de Sorteio",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            items(folhas) { folha ->
                Card(
                    modifier = Modifier.fillMaxWidth()
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
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "${dataManager.getNumerosOcupados(folha.id).size}/49 números",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                dataManager.toggleFolhaAtiva(folha.id)
                                folhas = dataManager.getFolhas()
                            }) {
                                Icon(
                                    if (folha.ativa) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    null,
                                    tint = if (folha.ativa) Color(0xFF4CAF50)
                                    else Color.Gray
                                )
                            }
                            IconButton(onClick = {
                                if (folhas.size > 1) {
                                    dataManager.eliminarFolha(folha.id)
                                    folhas = dataManager.getFolhas()
                                    totalNumerosVendidos = dataManager.getRegistos().size
                                }
                            }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showCriarFolhaDialog) {
        var nome by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCriarFolhaDialog = false },
            title = { Text("Nova Folha") },
            text = {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da Folha") },
                    placeholder = { Text("Ex: Semana 2") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dataManager.adicionarFolha(nome)
                        folhas = dataManager.getFolhas()
                        showCriarFolhaDialog = false
                    },
                    enabled = nome.length >= 3
                ) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCriarFolhaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showRegistarVencedorDialog) {
        val folhasAtivas = dataManager.getFolhasAtivas()
        var folhaSelecionada by remember { mutableStateOf(folhasAtivas.firstOrNull()) }
        var numeroVencedor by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showRegistarVencedorDialog = false },
            title = { Text("Registar Vencedor") },
            text = {
                Column {
                    if (folhasAtivas.isNotEmpty()) {
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
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                folhasAtivas.forEach { folha ->
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
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (errorMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMsg!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        Text("Não existem folhas ativas")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val numero = numeroVencedor.toIntOrNull()
                        if (numero != null && numero in 1..49 && folhaSelecionada != null) {
                            val sucesso = dataManager.registarVencedor(
                                folhaSelecionada!!.id,
                                folhaSelecionada!!.nome,
                                numero
                            )
                            if (sucesso) {
                                showRegistarVencedorDialog = false
                                errorMsg = null
                            } else {
                                errorMsg = "Número não foi vendido"
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
                TextButton(onClick = { showRegistarVencedorDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
