# Festa do Viso - App Android

Aplicação Android nativa para o sistema de sorteios da Festa do Viso, baseado nos números do Euromilhões.

## Características

✅ **100% Offline** - Funciona sem ligação à internet
✅ **Base de Dados Local** - SQLite via Room Database
✅ **Interface Moderna** - Jetpack Compose + Material Design 3
✅ **Notificações** - Lembretes todas as sextas-feiras às 22h
✅ **Segurança** - Passwords com hash BCrypt
✅ **Arquitetura Limpa** - MVVM + Clean Architecture

## Funcionalidades

### Para Participantes
- Escolher números (1-49) em folhas de sorteio ativas
- Ver números disponíveis e ocupados em tempo real
- Registar nome e contacto para cada número
- Consultar lista de vencedores semanais

### Para Administradores
- Login seguro (username: `admin`, password: `admin123`)
- Criar e gerir folhas de sorteio
- Registar vencedores semanais
- Ver estatísticas (folhas, números vendidos, vencedores)
- Ativar/desativar folhas
- Eliminar folhas (proteção: não pode eliminar a última)

### Notificações
- Notificação automática todas as sextas-feiras às 22h
- Lembrete do sorteio do Euromilhões
- Funciona em background com WorkManager

## Tecnologias

- **Linguagem**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Base de Dados**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose
- **Notifications**: WorkManager
- **Password Hash**: BCrypt (jBCrypt)
- **Architecture**: MVVM + Clean Architecture

## Estrutura do Projeto

```
app/src/main/java/com/festadoviso/
├── data/
│   ├── local/
│   │   ├── entity/          # Entidades Room (FolhaEntity, RegistoEntity, etc.)
│   │   ├── dao/             # DAOs (FolhaDao, RegistoDao, etc.)
│   │   └── AppDatabase.kt   # Configuração Room
│   └── repository/          # Repositories (FolhaRepository, etc.)
├── domain/
│   ├── model/               # Domain Models (Folha, Registo, Vencedor)
│   └── usecase/             # Use Cases (GetFolhasAtivasUseCase, etc.)
├── di/                      # Módulos Hilt
├── ui/
│   ├── theme/               # Tema e cores
│   ├── navigation/          # Sistema de navegação
│   ├── sorteio/             # Ecrã de sorteio
│   ├── vencedores/          # Ecrã de vencedores
│   ├── admin/               # Painel de administração
│   └── MainActivity.kt
├── workers/                 # WorkManager workers
├── util/                    # Utilitários
└── FestaDoVisoApp.kt        # Application class
```

## Requisitos

- **Android Studio**: Hedgehog (2023.1.1) ou superior
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.22
- **Gradle**: 8.2

## Como Instalar

### Opção 1: Instalar APK (Mais Rápido)

1. Transferir o ficheiro `festa-do-viso.apk`
2. No dispositivo Android, ir a **Definições** → **Segurança**
3. Ativar **Fontes desconhecidas** ou **Instalar apps desconhecidas**
4. Abrir o ficheiro APK e instalar
5. Abrir a app "Festa do Viso"

### Opção 2: Compilar no Android Studio

1. Clonar o repositório:
```bash
git clone https://github.com/paulosantos-ai/festa-do-viso.git
cd festa-do-viso/android-app
```

2. Abrir o projeto no Android Studio

3. Sincronizar dependências Gradle (Build → Sync Project with Gradle Files)

4. Ligar dispositivo Android via USB ou iniciar emulador

5. Compilar e executar:
```bash
./gradlew assembleDebug
# ou no Android Studio: Run → Run 'app'
```

## Como Usar

### 1. Participar no Sorteio

1. Abrir a app
2. Ir ao separador **"Sorteio"**
3. Selecionar folha ativa
4. Tocar num número disponível (cinzento = ocupado)
5. Preencher nome completo e contacto (9 dígitos)
6. Confirmar registo

### 2. Ver Vencedores

1. Ir ao separador **"Vencedores"**
2. Ver lista de todos os vencedores
3. Info: nome, contacto, folha, data do sorteio, número vencedor

### 3. Administração

1. Ir ao separador **"Admin"**
2. Fazer login (admin / admin123)
3. Ver estatísticas globais
4. **Criar Nova Folha**: botão azul (+)
5. **Registar Vencedor**: botão verde (troféu)
6. **Gerir Folhas**: ativar/desativar ou eliminar

## Base de Dados

### Tabelas

#### `folhas`
- id, nome, ativa, dataCriacao
- Representa folhas de sorteio (cada uma com 49 números)

#### `registos`
- id, folhaId, numero, nome, contacto, dataRegisto
- Registo de números vendidos (1 número = 1 participante)

#### `vencedores`
- id, folhaId, folhaNome, dataSorteio, numeroVencedor, vencedorNome, vencedorContacto, dataRegisto
- Histórico de vencedores semanais

#### `admin_users`
- id, username, passwordHash, dataCriacao, ultimoAcesso
- Utilizadores administradores

### Dados Iniciais (Seed Data)

- **Admin padrão**: username `admin`, password `admin123` (hash BCrypt)
- **Folha padrão**: "Semana 1" (ativa)

## Permissões

A app solicita as seguintes permissões:

- `POST_NOTIFICATIONS` - Enviar notificações (Android 13+)
- `SCHEDULE_EXACT_ALARM` - Agendar notificações precisas

## Notificações

As notificações são agendadas automaticamente quando a app é instalada:

- **Frequência**: Todas as sextas-feiras
- **Hora**: 22:00
- **Conteúdo**: "Sorteio Euromilhões Hoje! Não se esqueça! O sorteio é hoje às 22h. Boa sorte!"
- **Condição**: Apenas se existirem folhas ativas

Para desativar notificações:
1. Definições Android → Apps → Festa do Viso → Notificações
2. Desativar "Lembretes de Sorteio"

## Desenvolvimento

### Compilar APK de Produção

```bash
./gradlew assembleRelease
```

O APK estará em: `app/build/outputs/apk/release/app-release.apk`

### Executar Testes

```bash
./gradlew test
```

## Segurança

- Passwords armazenadas com hash BCrypt (custo 10)
- Validação de input em todos os formulários
- Prepared statements (Room) para prevenir SQL injection
- ProGuard rules para ofuscar código em produção

## Suporte

Para bugs ou questões:
- GitHub Issues: https://github.com/paulosantos-ai/festa-do-viso/issues
- Email: (adicionar email de suporte)

## Licença

MIT License - ver ficheiro LICENSE

## Créditos

Desenvolvido para a Comissão de Festas do Viso.

---

**Versão**: 1.0
**Data**: 2024
**Plataforma**: Android 7.0+
