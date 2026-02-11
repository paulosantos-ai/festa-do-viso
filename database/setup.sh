#!/bin/bash

# Script de Setup da Base de Dados - Festa do Viso
# Este script configura a base de dados MySQL e o utilizador

echo "=== Setup da Base de Dados - Festa do Viso ==="
echo ""

# Variáveis
DB_NAME="festa_viso"
DB_USER="festa_viso_user"
DB_PASS="festa_viso_pass"
ADMIN_PASS="admin123"

# Solicitar password do root do MySQL
echo "Por favor, introduza a password do root do MySQL:"
read -s MYSQL_ROOT_PASSWORD

# Criar base de dados e utilizador
echo ""
echo "Criando base de dados e utilizador..."

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<MYSQL_SCRIPT
-- Criar base de dados
CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Criar utilizador
CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';

-- Conceder permissões
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;

-- Usar a base de dados
USE ${DB_NAME};

-- Criar tabelas
CREATE TABLE IF NOT EXISTS folhas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ativa TINYINT(1) DEFAULT 1,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ativa (ativa),
    INDEX idx_data_criacao (data_criacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS registos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    folha_id INT NOT NULL,
    numero INT NOT NULL CHECK (numero BETWEEN 1 AND 49),
    nome VARCHAR(200) NOT NULL,
    contacto VARCHAR(20) NOT NULL,
    data_registo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (folha_id) REFERENCES folhas(id) ON DELETE CASCADE,
    UNIQUE KEY unique_numero_folha (folha_id, numero),
    INDEX idx_folha_id (folha_id),
    INDEX idx_numero (numero),
    INDEX idx_data_registo (data_registo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vencedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    folha_id INT NOT NULL,
    folha_nome VARCHAR(100) NOT NULL,
    data_sorteio DATE NOT NULL,
    numero_vencedor INT NOT NULL CHECK (numero_vencedor BETWEEN 1 AND 49),
    vencedor_nome VARCHAR(200) NOT NULL,
    vencedor_contacto VARCHAR(20) NOT NULL,
    data_registo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (folha_id) REFERENCES folhas(id) ON DELETE CASCADE,
    INDEX idx_folha_id (folha_id),
    INDEX idx_data_sorteio (data_sorteio),
    INDEX idx_data_registo (data_registo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inserir folha padrão se não existir
INSERT IGNORE INTO folhas (id, nome, ativa) VALUES (1, 'Semana 1', 1);

MYSQL_SCRIPT

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Base de dados configurada com sucesso!"
    echo ""
    echo "Detalhes da configuração:"
    echo "  Base de Dados: ${DB_NAME}"
    echo "  Utilizador: ${DB_USER}"
    echo "  Password: ${DB_PASS}"
    echo "  Password Admin: ${ADMIN_PASS}"
    echo ""
    echo "⚠️  IMPORTANTE: Atualize o ficheiro api/config.php com as credenciais acima."
else
    echo ""
    echo "❌ Erro ao configurar a base de dados!"
    exit 1
fi
