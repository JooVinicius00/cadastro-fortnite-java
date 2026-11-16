-- Execute manualmente no MySQL 8, antes de usar o cadastro.
-- Nao apaga banco, tabelas ou registros existentes.
CREATE DATABASE IF NOT EXISTS fortnite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fortnite;
CREATE TABLE IF NOT EXISTS usuario (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    vida INT NOT NULL DEFAULT 100,
    escudo INT NOT NULL DEFAULT 100,
    picareta MEDIUMBLOB NULL,
    skin MEDIUMBLOB NULL,
    CHECK (vida BETWEEN 0 AND 100),
    CHECK (escudo BETWEEN 0 AND 100)
);
-- Configure um usuario local com SELECT, INSERT, UPDATE e DELETE nesta tabela.
-- A criacao do usuario e a senha ficam sob sua configuracao local do MySQL.
