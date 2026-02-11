# Festa do Viso - Sistema de Sorteio Euromilhões

Sistema web para gestão de sorteios baseados no último número do Euromilhões, desenvolvido para angariação de fundos da Comissão de Festas do Viso.

## 📋 Requisitos

- Servidor web (Apache/Nginx)
- PHP 7.4 ou superior
- MySQL/MariaDB 5.7 ou superior
- Extensões PHP necessárias:
  - php-mysql (PDO)
  - php-json
  - php-session

## 🚀 Instalação

### 1. Configurar Base de Dados

```bash
cd /festadoviso/database
chmod +x setup.sh
./setup.sh
```

O script irá:
- Criar a base de dados `festa_viso`
- Criar o utilizador `festa_viso_user`
- Criar as tabelas necessárias
- Inserir dados iniciais

**Credenciais padrão:**
- Utilizador BD: `festa_viso_user`
- Password BD: `festa_viso_pass`
- Password Admin: `admin123`

### 2. Configurar API

Edite o ficheiro `api/config.php` e confirme as credenciais da base de dados:

```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'festa_viso');
define('DB_USER', 'festa_viso_user');
define('DB_PASS', 'festa_viso_pass');
```

### 3. Configurar Servidor Web

#### Nginx

O servidor já deve estar configurado com o location `/festadoviso` apontando para o diretório da aplicação.

Verifique se o PHP-FPM está ativo:
```bash
sudo systemctl status php-fpm
```

#### Apache

Se usar Apache, certifique-se de que `mod_rewrite` está ativado:
```bash
sudo a2enmod rewrite
sudo systemctl restart apache2
```

### 4. Permissões

```bash
sudo chown -R www-data:www-data /festadoviso
sudo chmod -R 755 /festadoviso
sudo chmod 750 /festadoviso/api
```

## 📁 Estrutura de Ficheiros

```
/festadoviso/
├── index.html              # Página principal
├── sorteio.html           # Interface de participação
├── relatorios.html        # Vencedores semanais
├── admin.html             # Painel de administração
├── styles.css             # Estilos CSS
├── sorteio.js             # JavaScript do sorteio
├── admin.js               # JavaScript da administração
├── relatorios.js          # JavaScript dos relatórios
├── api/                   # Backend PHP
│   ├── config.php         # Configuração e conexão BD
│   ├── auth.php           # Autenticação
│   ├── folhas.php         # Gestão de folhas
│   ├── registos.php       # Gestão de registos
│   └── vencedores.php     # Gestão de vencedores
├── database/              # Scripts de base de dados
│   ├── schema.sql         # Schema da BD
│   └── setup.sh           # Script de instalação
└── README.md              # Este ficheiro
```

## 🎮 Como Usar

### Para Utilizadores

1. Aceda a `http://192.168.64.2/festadoviso/`
2. Clique em "Participar no Sorteio"
3. Selecione uma folha disponível
4. Escolha um número de 1 a 49
5. Preencha o nome e contacto móvel
6. Aguarde o sorteio de sexta-feira!

### Para Administradores

1. Aceda a `http://192.168.64.2/festadoviso/admin.html`
2. Introduza a password: `admin123`
3. Opções disponíveis:
   - Criar novas folhas de sorteio
   - Ver detalhes de cada folha
   - Registar resultados do Euromilhões
   - Consultar estatísticas

## 🏆 Gestão de Sorteios

### Registar Vencedor

1. Aceda ao painel de administração
2. Selecione a folha do sorteio
3. Introduza a data do sorteio (sexta-feira)
4. Introduza o último número do Euromilhões (1-49)
5. O sistema irá:
   - Verificar se o número foi vendido
   - Registar o vencedor
   - Atualizar as estatísticas

### Consultar Vencedores

Os vencedores são publicados automaticamente na página "Vencedores" após o registo.

## 🔒 Segurança

### Alterar Password do Admin

Edite o ficheiro `api/config.php`:

```php
// Gerar novo hash (execute num terminal PHP):
php -r "echo password_hash('nova_password', PASSWORD_DEFAULT);"

// Atualize a constante:
define('ADMIN_PASSWORD_HASH', 'novo_hash_aqui');
```

### Proteção da API

- Autenticação obrigatória para operações administrativas
- Validação de dados no servidor
- Proteção contra SQL Injection (PDO com prepared statements)
- Sanitização de inputs
- Sessões seguras com timeout

## 🔧 Troubleshooting

### Erro de Conexão à Base de Dados

```bash
# Verificar se MySQL está ativo
sudo systemctl status mysql

# Verificar credenciais
mysql -u festa_viso_user -p festa_viso
```

### Erro 500 nas APIs

```bash
# Verificar logs do PHP
sudo tail -f /var/log/php-fpm/error.log

# Verificar logs do Nginx
sudo tail -f /var/log/nginx/error.log
```

### Permissões

```bash
# Restaurar permissões corretas
sudo chown -R www-data:www-data /festadoviso
sudo chmod -R 755 /festadoviso
```

## 📊 Base de Dados

### Backup

```bash
mysqldump -u festa_viso_user -p festa_viso > backup_$(date +%Y%m%d).sql
```

### Restore

```bash
mysql -u festa_viso_user -p festa_viso < backup_20240211.sql
```

## 🆘 Suporte

Para problemas ou questões, contacte o administrador do sistema.

## 📝 Licença

© 2024 Comissão de Festas do Viso - Todos os direitos reservados.
