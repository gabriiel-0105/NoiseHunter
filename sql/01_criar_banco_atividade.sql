-- Execute no MySQL Workbench ANTES de iniciar esta versao do backend.
-- Usa uma base separada para nao alterar o NoiseHunter que ja funciona.
-- Estrutura da atividade: usuarios -> dispositivos -> ocorrencias.
CREATE DATABASE IF NOT EXISTS noisehunter_atividade
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE noisehunter_atividade;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    email VARCHAR(100),
    senha VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS dispositivos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100),
    localizacao VARCHAR(100),
    status VARCHAR(30),
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS ocorrencias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo_som VARCHAR(50),
    decibeis DECIMAL(5,2),
    data_hora DATETIME,
    localizacao VARCHAR(100),
    dispositivo_id INT,
    FOREIGN KEY (dispositivo_id) REFERENCES dispositivos(id)
);

SHOW TABLES;
