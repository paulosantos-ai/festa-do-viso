#!/bin/bash

echo "╔════════════════════════════════════════════════════════╗"
echo "║   Festa do Viso - Corrigir Password de Administração  ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""

# Hash correto para 'admin123'
HASH='$2y$10$ON.M2AMIpe8QEt2TsN20OeOvj8PfMFh5D0V2UncINNQIv6AEUecjy'

echo "📝 Passo 1: Fazer backup do config.php..."
sudo cp /festadoviso/api/config.php /festadoviso/api/config.php.backup.$(date +%Y%m%d_%H%M%S)
if [ $? -eq 0 ]; then
    echo "   ✅ Backup criado com sucesso"
else
    echo "   ❌ Erro ao criar backup"
    exit 1
fi

echo ""
echo "🔧 Passo 2: Atualizar hash da password..."

# Criar novo config.php com hash correto
sudo sed -i.old "s|define('ADMIN_PASSWORD_HASH', '.*'); // admin123|define('ADMIN_PASSWORD_HASH', '$HASH'); // admin123|g" /festadoviso/api/config.php

if [ $? -eq 0 ]; then
    echo "   ✅ Hash atualizado"
else
    echo "   ❌ Erro ao atualizar"
    exit 1
fi

echo ""
echo "🧪 Passo 3: Testar login..."

# Testar login via API
RESULT=$(curl -s -X POST http://localhost/festadoviso/api/auth.php \
  -H 'Content-Type: application/json' \
  -d '{"action":"login","password":"admin123"}')

echo "   Resposta da API: $RESULT"
echo ""

# Verificar se login foi bem sucedido
if echo "$RESULT" | grep -q '"success":true'; then
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║              ✅ SUCESSO! Login Funcionando             ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo ""
    echo "📋 Credenciais de Administração:"
    echo "   🌐 URL: http://192.168.64.2/festadoviso/admin.html"
    echo "   🔑 Password: admin123"
    echo ""
    echo "💡 Dica: Altere a password em produção!"
    echo "   Ver: /festadoviso/README.md (secção Segurança)"
    echo ""
else
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║                ❌ ERRO: Login Falhou                   ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo ""
    echo "🔍 Diagnóstico:"
    echo ""
    
    # Verificar se a base de dados existe
    echo "   Verificando base de dados..."
    if sudo mysql -e "USE festa_viso;" 2>/dev/null; then
        echo "   ✅ Base de dados existe"
    else
        echo "   ❌ Base de dados não existe!"
        echo "   📝 Execute: sudo mysql < /tmp/setup_db.sql"
    fi
    
    # Verificar se PHP-FPM está ativo
    echo ""
    echo "   Verificando PHP-FPM..."
    if systemctl is-active --quiet php8.3-fpm; then
        echo "   ✅ PHP-FPM está ativo"
    else
        echo "   ❌ PHP-FPM não está ativo!"
        echo "   📝 Execute: sudo systemctl start php8.3-fpm"
    fi
    
    # Verificar logs
    echo ""
    echo "   📋 Últimas linhas do log de erros:"
    sudo tail -5 /var/log/nginx/error.log 2>/dev/null || echo "   (sem logs)"
    
    echo ""
    echo "🔄 Restaurando backup..."
    sudo cp /festadoviso/api/config.php.old /festadoviso/api/config.php
    echo "   ✅ Backup restaurado"
fi

echo ""
echo "════════════════════════════════════════════════════════"
