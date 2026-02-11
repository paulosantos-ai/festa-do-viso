# 🚀 Passos Finais - Instalação Rápida

Execute estes comandos no servidor para concluir a instalação.

## 📝 Passo a Passo

### 1️⃣ Conectar ao Servidor

```bash
ssh psantos@192.168.64.2
```
**Password:** `itcenter`

---

### 2️⃣ Configurar PHP no Nginx (AUTOMÁTICO)

Execute o script preparado:

```bash
/tmp/setup_php.sh
```

Este script irá:
- ✅ Fazer backup da configuração atual
- ✅ Aplicar configuração com suporte PHP
- ✅ Testar se a configuração está correta
- ✅ Reiniciar Nginx e PHP-FPM
- ✅ Se houver erro, restaura o backup automaticamente

**Nota:** Vai pedir a password `itcenter` para executar comandos sudo.

---

### 3️⃣ Criar a Base de Dados

```bash
sudo mysql < /tmp/setup_db.sql
```

Isto irá criar:
- ✅ Base de dados `festa_viso`
- ✅ Utilizador `festa_viso_user`
- ✅ Tabelas: folhas, registos, vencedores
- ✅ Folha inicial "Semana 1"

**Nota:** Se pedir password do MySQL, pressione **Enter** (não tem password).

---

### 4️⃣ Verificar se a Base de Dados foi Criada

```bash
sudo mysql -e "SHOW DATABASES;"
```

Deverá ver `festa_viso` na lista.

```bash
sudo mysql -e "USE festa_viso; SHOW TABLES;"
```

Deverá ver as tabelas: `folhas`, `registos`, `vencedores`

---

### 5️⃣ Configurar Permissões

```bash
sudo chown -R www-data:www-data /festadoviso
sudo chmod -R 755 /festadoviso
```

---

### 6️⃣ Testar a Aplicação

#### Testar API:
```bash
curl http://192.168.64.2/festadoviso/api/folhas.php
```

**Resultado esperado:** JSON com a folha "Semana 1"

```json
{
  "success": true,
  "folhas": [
    {
      "id": "1",
      "nome": "Semana 1",
      "ativa": "1",
      "data_criacao": "...",
      "numeros_ocupados": "0"
    }
  ]
}
```

#### Testar no Navegador:

Abra: **http://192.168.64.2/festadoviso/**

Deverá ver a página principal da Festa do Viso.

---

## ✅ Verificação Final

### Testar Participação no Sorteio

1. Aceda a: http://192.168.64.2/festadoviso/sorteio.html
2. Deverá ver "Folha 1 - Semana 1" no seletor
3. Deverá ver os números de 1 a 49 em verde (disponíveis)
4. Clique num número
5. Preencha nome e contacto
6. Registe

**Se funcionar:** ✅ Sistema está operacional!

### Testar Administração

1. Aceda a: http://192.168.64.2/festadoviso/admin.html
2. Password: `admin123`
3. Deverá ver:
   - Estatísticas (1 folha ativa, 0/1 números vendidos)
   - Lista de folhas
   - Formulário para registar vencedor

---

## 🔧 Se algo não funcionar

### Problema: API retorna erro 404

**Solução:**
```bash
sudo systemctl status php8.3-fpm
sudo systemctl start php8.3-fpm
sudo systemctl enable php8.3-fpm
```

### Problema: Erro de conexão à base de dados

**Solução:** Verificar credenciais
```bash
sudo nano /festadoviso/api/config.php
```

Confirmar:
```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'festa_viso');
define('DB_USER', 'festa_viso_user');
define('DB_PASS', 'festa_viso_pass');
```

### Problema: Erro 500 nas APIs

**Ver logs:**
```bash
sudo tail -f /var/log/nginx/error.log
```

---

## 📋 Resumo Rápido (Comandos em Sequência)

Se quiser executar tudo de uma vez:

```bash
# No servidor (conectado via SSH)
/tmp/setup_php.sh
sudo mysql < /tmp/setup_db.sql
sudo mysql -e "SHOW DATABASES;"
sudo chown -R www-data:www-data /festadoviso
sudo chmod -R 755 /festadoviso
curl http://192.168.64.2/festadoviso/api/folhas.php
```

Se todos os comandos executarem sem erro, está pronto! 🎉

---

## 🎯 Próximos Passos (Opcional)

1. Alterar password de administração (ver README.md)
2. Testar fluxo completo (registar números, criar folhas, registar vencedor)
3. Configurar backup automático da base de dados

---

## 📞 Credenciais

### Base de Dados
- Host: `localhost`
- Nome: `festa_viso`
- Utilizador: `festa_viso_user`
- Password: `festa_viso_pass`

### Admin Web
- URL: http://192.168.64.2/festadoviso/admin.html
- Password: `admin123`

---

**Boa sorte com o sorteio da Festa do Viso! 🎉**
