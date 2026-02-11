-- Base de Dados para Festa do Viso - Sistema de Sorteio Euromilhões
-- Criação da base de dados

CREATE DATABASE IF NOT EXISTS festa_viso CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE festa_viso;

-- Tabela de Folhas de Sorteio
CREATE TABLE IF NOT EXISTS folhas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ativa TINYINT(1) DEFAULT 1,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ativa (ativa),
    INDEX idx_data_criacao (data_criacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de Registos de Números
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

-- Tabela de Vencedores
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

-- Tabela de Utilizadores Admin
CREATE TABLE IF NOT EXISTS usuarios_admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acesso TIMESTAMP NULL,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inserir folha padrão
INSERT INTO folhas (nome, ativa) VALUES ('Semana 1', 1);

-- Inserir utilizador admin padrão (password: admin123)
-- Password hash gerado com password_hash('admin123', PASSWORD_DEFAULT)
INSERT INTO usuarios_admin (username, password_hash)
VALUES ('admin', '$2y$10$YourHashHere');

-- Nota: A password hash será atualizada ao executar o script de setup
