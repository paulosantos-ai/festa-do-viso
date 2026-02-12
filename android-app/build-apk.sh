#!/bin/bash

# Script para compilar APK de debug da app Festa do Viso

echo "================================"
echo "Festa do Viso - Build APK"
echo "================================"
echo ""

# Verificar se Gradle está disponível
if [ ! -f "./gradlew" ]; then
    echo "❌ Erro: gradlew não encontrado!"
    echo "Execute este script na raiz do projeto Android."
    exit 1
fi

echo "🔧 A compilar APK de debug..."
echo ""

# Dar permissões de execução ao gradlew
chmod +x ./gradlew

# Compilar APK
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ APK compilado com sucesso!"
    echo ""
    echo "📦 Localização do APK:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📱 Para instalar no dispositivo:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
else
    echo ""
    echo "❌ Erro ao compilar APK!"
    exit 1
fi
