#!/bin/bash

echo "🔍 A monitorizar build do APK no GitHub Actions..."
echo "📍 Repositório: https://github.com/paulosantos-ai/festa-do-viso"
echo ""

# Loop para verificar status
for i in {1..60}; do
    echo "⏳ Tentativa $i/60 - A verificar..."

    # Obter status da última build
    STATUS=$(gh run list --repo paulosantos-ai/festa-do-viso --limit 1 --json status,conclusion,name --jq '.[0]')

    if [ ! -z "$STATUS" ] && [ "$STATUS" != "null" ]; then
        RUN_STATUS=$(echo $STATUS | jq -r '.status')
        CONCLUSION=$(echo $STATUS | jq -r '.conclusion')
        NAME=$(echo $STATUS | jq -r '.name')

        echo "📦 Build encontrada: $NAME"
        echo "📊 Status: $RUN_STATUS"

        if [ "$RUN_STATUS" == "completed" ]; then
            if [ "$CONCLUSION" == "success" ]; then
                echo ""
                echo "✅ BUILD CONCLUÍDA COM SUCESSO!"
                echo ""
                echo "📲 Para fazer download do APK:"
                echo "1. Vai a: https://github.com/paulosantos-ai/festa-do-viso/actions"
                echo "2. Clica na build mais recente"
                echo "3. Scroll até 'Artifacts'"
                echo "4. Download 'festa-do-viso-debug-apk'"
                echo ""
                exit 0
            else
                echo ""
                echo "❌ Build falhou com status: $CONCLUSION"
                echo "🔗 Ver detalhes: https://github.com/paulosantos-ai/festa-do-viso/actions"
                exit 1
            fi
        else
            echo "⏳ Build a correr... (aguarda ~5-7 minutos)"
        fi
    else
        echo "⏳ Build ainda não começou... GitHub pode demorar 1-2 min a reconhecer o workflow"
    fi

    sleep 30
done

echo ""
echo "⏱️ Timeout após 30 minutos. Verifica manualmente:"
echo "🔗 https://github.com/paulosantos-ai/festa-do-viso/actions"
