# Instruções de Compilação e Instalação

## Pré-requisitos

1. **Android Studio** instalado (recomendado: Hedgehog 2023.1.1+)
2. **JDK 17** instalado
3. **Android SDK** com Android 14 (API 34)

## Opção 1: Compilar no Android Studio (Recomendado)

### Passo 1: Abrir o Projeto
1. Abrir Android Studio
2. File → Open
3. Selecionar a pasta `android-app`
4. Aguardar sincronização do Gradle (pode demorar alguns minutos na primeira vez)

### Passo 2: Compilar
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Aguardar compilação (3-5 minutos)
3. Quando terminar, clicar em "locate" na notificação
4. O APK estará em: `app/build/outputs/apk/debug/app-debug.apk`

### Passo 3: Instalar no Dispositivo

**Via USB:**
1. Ativar "Modo de Programador" no Android:
   - Definições → Acerca do telefone
   - Tocar 7 vezes em "Número de compilação"
2. Ativar "Depuração USB":
   - Definições → Sistema → Opções do programador → Depuração USB
3. Ligar dispositivo ao computador via USB
4. No Android Studio: Run → Run 'app'

**Via Ficheiro APK:**
1. Copiar `app-debug.apk` para o dispositivo
2. No dispositivo, abrir Ficheiros ou Gestor de Ficheiros
3. Tocar no ficheiro APK
4. Se necessário, permitir instalação de fontes desconhecidas
5. Instalar

## Opção 2: Compilar via Linha de Comandos

### No macOS/Linux:

```bash
cd android-app

# Dar permissões ao script Gradle
chmod +x gradlew

# Compilar APK de debug
./gradlew assembleDebug

# APK estará em: app/build/outputs/apk/debug/app-debug.apk
```

### Script Auxiliar:

```bash
cd android-app
chmod +x build-apk.sh
./build-apk.sh
```

## Opção 3: Compilar APK de Produção (Release)

**IMPORTANTE:** Para APK de produção, é necessário um keystore para assinar a app.

### Criar Keystore (primeira vez):

```bash
keytool -genkey -v -keystore festa-viso-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias festa-viso
```

Responder às questões (nome, organização, etc.) e definir uma password.

### Configurar build.gradle.kts:

Adicionar ao ficheiro `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../festa-viso-keystore.jks")
            storePassword = "SUA_PASSWORD"
            keyAlias = "festa-viso"
            keyPassword = "SUA_PASSWORD"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... resto da configuração
        }
    }
}
```

### Compilar:

```bash
./gradlew assembleRelease
```

APK estará em: `app/build/outputs/apk/release/app-release.apk`

## Verificar se a App Funciona

Após instalar a app no dispositivo:

1. ✅ Abrir app "Festa do Viso"
2. ✅ Ver ecrã de Sorteio com grid de números 1-49
3. ✅ Selecionar uma folha no dropdown
4. ✅ Tocar num número e preencher nome/contacto
5. ✅ Ir ao separador "Vencedores" (deve estar vazio)
6. ✅ Ir ao separador "Admin"
7. ✅ Fazer login (admin / admin123)
8. ✅ Ver estatísticas
9. ✅ Criar nova folha
10. ✅ Notificação: Verificar se está agendada (sextas 22h)

## Resolução de Problemas

### Erro: "SDK location not found"
**Solução:** Criar ficheiro `local.properties` na raiz do projeto:
```
sdk.dir=/Users/SEUNOME/Library/Android/sdk
```

### Erro: "Gradle sync failed"
**Solução:**
1. File → Invalidate Caches
2. Restart Android Studio
3. Aguardar nova sincronização

### Erro ao compilar: "Could not resolve dependencies"
**Solução:**
1. Verificar ligação à internet
2. Build → Clean Project
3. Build → Rebuild Project

### App fecha ao abrir (crash)
**Solução:**
1. Verificar logs no Logcat (Android Studio)
2. Verificar se permissões estão concedidas
3. Reinstalar app

### Notificações não aparecem
**Solução:**
1. Definições → Apps → Festa do Viso → Notificações
2. Verificar se estão ativadas
3. Android 13+: Permissão de notificações deve ser concedida

## Tamanho do APK

- **Debug**: ~8-10 MB
- **Release** (com ProGuard): ~5-7 MB

## Performance

A primeira abertura pode demorar 2-3 segundos (inicialização da base de dados).
Aberturas seguintes são instantâneas.

## Questões Frequentes

**P: Posso instalar em tablets?**
R: Sim, a app é responsiva e funciona em tablets e smartphones.

**P: Funciona sem internet?**
R: Sim, é 100% offline. Não precisa de ligação à internet.

**P: Os dados ficam guardados?**
R: Sim, todos os dados ficam guardados localmente no dispositivo.

**P: Posso mudar a password do admin?**
R: Sim, mas requer editar o código e recompilar a app.

**P: Quantos números posso vender?**
R: 49 números por folha. Pode criar folhas ilimitadas.

---

Em caso de dúvidas ou problemas, criar issue no GitHub.
