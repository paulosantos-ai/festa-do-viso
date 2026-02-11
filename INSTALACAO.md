# Instruções de Instalação - Festa do Viso

## ✅ O que já foi feito:

1. ✅ Todos os ficheiros foram enviados para `/festadoviso/`
2. ✅ MariaDB foi instalado e está ativo
3. ✅ PHP-FPM e PHP-MySQL foram instalados
4. ✅ Nginx está configurado

## 🔧 Passos Finais (Executar no Servidor)

### 1. Conectar ao Servidor

```bash
ssh psantos@192.168.64.2
# Password: itcenter
```

### 2. Criar a Base de Dados

Execute o script SQL que foi preparado:

```bash
sudo mysql < /tmp/setup_db.sql
```

Ou execute o script de setup:

```bash
cd /festadoviso/database
sudo ./setup.sh
```

Quando pedir a password do root do MySQL, pressione **Enter** (não tem password por padrão no MariaDB novo).

### 3. Verificar se a Base de Dados foi Criada

```bash
sudo mysql -e "SHOW DATABASES;"
sudo mysql -e "USE festa_viso; SHOW TABLES;"
```

Deverá ver:
- Base de dados: `festa_viso`
- Tabelas: `folhas`, `registos`, `vencedores`

### 4. Configurar Permissões PHP

```bash
sudo chown -R www-data:www-data /festadoviso
sudo chmod -R 755 /festadoviso
sudo chmod 750 /festadoviso/api
```

### 5. Configurar PHP-FPM para Nginx

Editar a configuração do Nginx:

```bash
sudo nano /etc/nginx/sites-available/default
```

Adicionar suporte PHP no bloco `location /festadoviso`:

```nginx
location /festadoviso {
    alias /festadoviso;
    index index.html index.php;

    location ~ \.php$ {
        include snippets/fastcgi-php.conf;
        fastcgi_pass unix:/var/run/php/php-fpm.sock;
        fastcgi_param SCRIPT_FILENAME $request_filename;
    }

    try_files $uri $uri/ /festadoviso/index.html;
}
```

Salvar e sair (Ctrl+X, Y, Enter).

### 6. Reiniciar Serviços

```bash
sudo systemctl restart php8.3-fpm
sudo systemctl restart nginx
```

### 7. Testar a Aplicação

Abrir no navegador:

```
http://192.168.64.2/festadoviso/
```

## 🧪 Testes

### Testar API de Folhas

```bash
curl http://192.168.64.2/festadoviso/api/folhas.php
```

Deverá retornar JSON com a folha padrão.

### Testar Página de Sorteio

Aceda a: `http://192.168.64.2/festadoviso/sorteio.html`

Deverá ver a "Folha 1 - Semana 1" disponível.

### Testar Administração

1. Aceda a: `http://192.168.64.2/festadoviso/admin.html`
2. Password: `admin123`
3. Deverá ver o painel de administração

## 🔍 Resolução de Problemas

### Erro: "Erro de conexão à base de dados"

Verificar credenciais no ficheiro `/festadoviso/api/config.php`:

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

### Erro 500 nas APIs

Ver logs do PHP:

```bash
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/php8.3-fpm.log
```

### API não funciona (erro 404)

Verificar se o PHP-FPM está ativo:

```bash
sudo systemctl status php8.3-fpm
```

Se não estiver, iniciar:

```bash
sudo systemctl start php8.3-fpm
sudo systemctl enable php8.3-fpm
```

## 📋 Credenciais

### Base de Dados
- **Host**: localhost
- **Nome**: festa_viso
- **Utilizador**: festa_viso_user
- **Password**: festa_viso_pass

### Administração Web
- **URL**: http://192.168.64.2/festadoviso/admin.html
- **Password**: admin123

## 📊 Estrutura da Base de Dados

```sql
-- Folhas de sorteio
folhas (id, nome, ativa, data_criacao)

-- Registos de números
registos (id, folha_id, numero, nome, contacto, data_registo)

-- Vencedores
vencedores (id, folha_id, folha_nome, data_sorteio, numero_vencedor, vencedor_nome, vencedor_contacto, data_registo)
```

## ✨ Funcionalidades

### Utilizadores
- Visualizar folhas disponíveis
- Escolher números (1-49)
- Registar nome e contacto
- Ver vencedores semanais

### Administradores
- Criar novas folhas de sorteio
- Eliminar folhas
- Ver detalhes de cada folha
- Registar resultados do Euromilhões
- Consultar estatísticas

## 🚀 Próximos Passos

1. Alterar a password de administração (ver README.md)
2. Configurar backups automáticos da base de dados
3. Testar o fluxo completo:
   - Registar alguns números
   - Criar uma nova folha
   - Registar um vencedor
4. Configurar HTTPS (opcional mas recomendado)

## 📞 Suporte

Para questões ou problemas, consulte o README.md ou contacte o administrador do sistema.
