# 🎉 Festa do Viso - Sistema de Sorteio Euromilhões

<div align="center">

![GitHub](https://img.shields.io/github/license/paulosantos-ai/festa-do-viso)
![PHP Version](https://img.shields.io/badge/PHP-7.4%2B-blue)
![MySQL](https://img.shields.io/badge/MySQL-5.7%2B-orange)

Sistema web para gestão de sorteios baseados no último número do Euromilhões, desenvolvido para angariação de fundos da Comissão de Festas do Viso.

[Demo](#-funcionalidades) • [Instalação](#-instalação-rápida) • [Documentação](#-documentação) • [Suporte](#-suporte)

</div>

---

## 📖 Sobre

Sistema completo de gestão de sorteios que permite aos participantes escolherem números de 1 a 49. O vencedor é determinado pelo último número do sorteio do Euromilhões de cada sexta-feira.

### ✨ Funcionalidades

#### 👥 Para Utilizadores
- 🎯 Escolha de números (1-49) em interface visual intuitiva
- 📝 Registo simples com nome e contacto
- 👀 Visualização de números disponíveis/ocupados em tempo real
- 🏆 Consulta de vencedores semanais
- 📱 Design responsivo (desktop, tablet, mobile)

#### 🔧 Para Administradores
- 🔐 Painel de administração com autenticação segura
- 📊 Criação e gestão de múltiplas folhas de sorteio
- 📈 Estatísticas em tempo real
- 🎲 Registo de resultados do Euromilhões
- 👑 Gestão de vencedores
- 📋 Visualização detalhada de cada folha

---

## 🚀 Instalação Rápida

### Pré-requisitos

- **Servidor Web**: Nginx ou Apache
- **PHP**: 7.4 ou superior
- **Base de Dados**: MySQL 5.7+ ou MariaDB 10+
- **Extensões PHP**: PDO, MySQL, JSON, Session

### Passos

1. **Clone o repositório**
```bash
git clone https://github.com/paulosantos-ai/festa-do-viso.git
cd festa-do-viso
```

2. **Configure a base de dados**
```bash
cd database
chmod +x setup.sh
./setup.sh
```

3. **Configure as credenciais**
Edite `api/config.php` com as credenciais da base de dados.

4. **Configure o servidor web**
- **Nginx**: Ver [INSTALACAO.md](INSTALACAO.md#5-configurar-php-fpm-para-nginx)
- **Apache**: Ativar `mod_rewrite` e `mod_php`

5. **Configure permissões**
```bash
sudo chown -R www-data:www-data .
sudo chmod -R 755 .
```

6. **Aceda à aplicação**
```
http://seu-servidor/festadoviso/
```

📚 **Guia completo**: Ver [PASSOS_FINAIS.md](PASSOS_FINAIS.md) para instruções detalhadas passo a passo.

---

## 🏗️ Arquitetura

### Stack Tecnológica

```
Frontend
├── HTML5
├── CSS3 (Design responsivo)
└── JavaScript (ES6+, Fetch API)

Backend
├── PHP 7.4+ (OOP)
├── PDO (Prepared Statements)
└── REST API

Base de Dados
├── MySQL 5.7+ / MariaDB 10+
└── InnoDB Engine
```

### Estrutura de Ficheiros

```
festa-do-viso/
├── 📄 index.html              # Página principal
├── 📄 sorteio.html           # Interface de participação
├── 📄 relatorios.html        # Vencedores semanais
├── 📄 admin.html             # Painel de administração
├── 🎨 styles.css             # Estilos CSS
├── 📜 sorteio.js             # Lógica do sorteio
├── 📜 admin.js               # Lógica da administração
├── 📜 relatorios.js          # Lógica dos relatórios
├── 📁 api/                   # Backend PHP
│   ├── config.php            # Configuração e DB
│   ├── auth.php              # Autenticação
│   ├── folhas.php            # API de folhas
│   ├── registos.php          # API de registos
│   └── vencedores.php        # API de vencedores
├── 📁 database/              # Scripts SQL
│   ├── schema.sql            # Schema da BD
│   └── setup.sh              # Script de instalação
└── 📚 docs/                  # Documentação
    ├── README.md
    ├── INSTALACAO.md
    └── PASSOS_FINAIS.md
```

---

## 💾 Base de Dados

### Schema

```sql
-- Folhas de sorteio
CREATE TABLE folhas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    ativa TINYINT(1),
    data_criacao TIMESTAMP
);

-- Registos de números
CREATE TABLE registos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    folha_id INT,
    numero INT CHECK (numero BETWEEN 1 AND 49),
    nome VARCHAR(200),
    contacto VARCHAR(20),
    data_registo TIMESTAMP,
    UNIQUE(folha_id, numero)
);

-- Vencedores
CREATE TABLE vencedores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    folha_id INT,
    folha_nome VARCHAR(100),
    data_sorteio DATE,
    numero_vencedor INT,
    vencedor_nome VARCHAR(200),
    vencedor_contacto VARCHAR(20),
    data_registo TIMESTAMP
);
```

---

## 🔒 Segurança

- ✅ **PDO com Prepared Statements** - Proteção contra SQL Injection
- ✅ **Validação de dados** - Server-side e client-side
- ✅ **Sessões PHP** - Autenticação segura com timeout
- ✅ **Password hashing** - Bcrypt para passwords de admin
- ✅ **CORS configurável** - Controlo de origem de pedidos
- ✅ **Sanitização de inputs** - XSS prevention

### Alterar Password de Administração

```bash
# Gerar novo hash
php -r "echo password_hash('nova_password', PASSWORD_DEFAULT);"

# Atualizar em api/config.php
define('ADMIN_PASSWORD_HASH', 'novo_hash_aqui');
```

---

## 📱 Screenshots

### Interface de Participação
![Sorteio](https://via.placeholder.com/800x400/3498db/ffffff?text=Grid+de+N%C3%BAmeros+1-49)

### Painel de Administração
![Admin](https://via.placeholder.com/800x400/2c3e50/ffffff?text=Painel+de+Administra%C3%A7%C3%A3o)

### Vencedores
![Vencedores](https://via.placeholder.com/800x400/27ae60/ffffff?text=Vencedores+Semanais)

---

## 🧪 Testes

### Testar API

```bash
# Listar folhas
curl http://localhost/festadoviso/api/folhas.php

# Registar número (exemplo)
curl -X POST http://localhost/festadoviso/api/registos.php \
  -H "Content-Type: application/json" \
  -d '{"folha_id":1,"numero":7,"nome":"João Silva","contacto":"912345678"}'

# Listar vencedores
curl http://localhost/festadoviso/api/vencedores.php
```

---

## 📚 Documentação

- **[INSTALACAO.md](INSTALACAO.md)** - Guia detalhado de instalação
- **[PASSOS_FINAIS.md](PASSOS_FINAIS.md)** - Checklist de configuração
- **Código comentado** - Documentação inline em todos os ficheiros

---

## 🔧 Configuração

### Credenciais Padrão

**Base de Dados:**
- Host: `localhost`
- Nome: `festa_viso`
- Utilizador: `festa_viso_user`
- Password: `festa_viso_pass`

**Administração Web:**
- URL: `/admin.html`
- Password: `admin123`

⚠️ **Importante**: Altere estas credenciais em ambiente de produção!

---

## 🐛 Resolução de Problemas

### Erro: "Erro de conexão à base de dados"
```bash
# Verificar se MySQL está ativo
sudo systemctl status mysql

# Testar credenciais
mysql -u festa_viso_user -p festa_viso
```

### Erro 500 nas APIs
```bash
# Ver logs PHP
sudo tail -f /var/log/php-fpm/error.log

# Ver logs Nginx
sudo tail -f /var/log/nginx/error.log
```

### API retorna 404
```bash
# Verificar PHP-FPM
sudo systemctl status php-fpm
sudo systemctl start php-fpm
```

---

## 📊 Roadmap

- [ ] Sistema de pagamentos integrado
- [ ] Notificações por email/SMS
- [ ] Exportação de relatórios (PDF/Excel)
- [ ] Histórico de sorteios
- [ ] Multi-idioma (PT/EN/ES)
- [ ] API pública com documentação Swagger
- [ ] Aplicação mobile (React Native)

---

## 🤝 Contribuir

Contribuições são bem-vindas! Por favor:

1. Faça fork do projeto
2. Crie um branch para a feature (`git checkout -b feature/MinhaFeature`)
3. Commit as alterações (`git commit -m 'Adicionar MinhaFeature'`)
4. Push para o branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Ver ficheiro [LICENSE](LICENSE) para mais detalhes.

---

## 👥 Autores

- **Paulo Santos** - *Desenvolvimento* - [@paulosantos-ai](https://github.com/paulosantos-ai)
- **Claude Sonnet 4.5** - *Assistência no desenvolvimento*

---

## 📞 Suporte

Para questões ou problemas:
- 📧 Email: psantos@itcenter.pt
- 🐛 Issues: [GitHub Issues](https://github.com/paulosantos-ai/festa-do-viso/issues)

---

## 🙏 Agradecimentos

- Comissão de Festas do Viso
- Comunidade de desenvolvimento PHP
- Contribuidores do projeto

---

<div align="center">

**Desenvolvido com ❤️ para a Comissão de Festas do Viso**

[⬆ Voltar ao topo](#-festa-do-viso---sistema-de-sorteio-euromilhões)

</div>
