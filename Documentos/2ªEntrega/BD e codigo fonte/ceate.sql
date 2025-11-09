-- Criar base de dados
CREATE DATABASE languify_db;

-- Conectar na BD criada
\c languify_db;

-- =========================
-- TABELAS PRINCIPAIS
-- =========================

-- Tabela de usuários
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de chats
CREATE TABLE chats (
    id SERIAL PRIMARY KEY,
    user1_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de mensagens
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    chat_id INT NOT NULL REFERENCES chats(id) ON DELETE CASCADE,
    sender_id INT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de agentes (ex: assistente contextual)
CREATE TABLE agents (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de traduções
CREATE TABLE translations (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    original_text TEXT NOT NULL,
    translated_text TEXT NOT NULL,
    source_lang VARCHAR(10),
    target_lang VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- VIEWS
-- =========================

-- Visão geral dos chats com última mensagem
CREATE VIEW chat_overview AS
SELECT c.id AS chat_id,
       u1.name AS user1,
       u2.name AS user2,
       MAX(m.sent_at) AS last_message_time
FROM chats c
JOIN users u1 ON c.user1_id = u1.id
JOIN users u2 ON c.user2_id = u2.id
LEFT JOIN messages m ON m.chat_id = c.id
GROUP BY c.id, u1.name, u2.name;

-- =========================
-- STORED PROCEDURES
-- =========================

-- Adicionar mensagem
CREATE OR REPLACE FUNCTION add_message(chat INT, sender INT, msg TEXT)
RETURNS VOID AS $$
BEGIN
    INSERT INTO messages(chat_id, sender_id, content)
    VALUES(chat, sender, msg);
END;
$$ LANGUAGE plpgsql;

-- Adicionar tradução
CREATE OR REPLACE FUNCTION add_translation(user INT, original TEXT, translated TEXT, source_lang VARCHAR, target_lang VARCHAR)
RETURNS VOID AS $$
BEGIN
    INSERT INTO translations(user_id, original_text, translated_text, source_lang, target_lang)
    VALUES(user, original, translated, source_lang, target_lang);
END;
$$ LANGUAGE plpgsql;
