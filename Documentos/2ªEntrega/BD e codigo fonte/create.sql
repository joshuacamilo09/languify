-- Tabela de idiomas
CREATE TABLE Language (
    id SERIAL PRIMARY KEY,
    receiverLanguage VARCHAR(50) NOT NULL UNIQUE
    receptorLanguage VARCHAR(50) NOT NULL UNIQUE
);

-- Tabela de usuários
CREATE TABLE "User" (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(100),
    lastName VARCHAR(100),
    image BYTEA,
    nativeLanguage_id INTEGER REFERENCES Language(id),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de chats
CREATE TABLE Chat (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    summary VARCHAR(500),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL REFERENCES "User"(id) ON DELETE CASCADE,
    recptorLanguage_id INTEGER REFERENCES Language(id),
    receiverLanguage_id INTEGER REFERENCES Language(id)
);

-- Tabela de mensagens
CREATE TABLE message_Record (
    id SERIAL PRIMARY KEY,
    chat_id INTEGER NOT NULL REFERENCES Chat(id) ON DELETE CASCADE,
    content VARCHAR(1000),
    translated_content_id VARCHAR(1000), NOT NULL REFERENCES translation_lang
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de tradução de idiomas
CREATE TABLE translation_lang (
    id SERIAL PRIMARY KEY,
    nativeLanguage_id INTEGER NOT NULL REFERENCES Language(id),
    receiverLanguage_id INTEGER NOT NULL REFERENCES Language(id),
    translatedContent VARCHAR(1000)
);

-- Tabela de localização do chat
CREATE TABLE location (
    id SERIAL PRIMARY KEY,
    chat_id INTEGER NOT NULL REFERENCES Chat(id) ON DELETE CASCADE,
    city VARCHAR(100),
    country VARCHAR(100)
);

-- Tabela de agente contextual
CREATE TABLE agente_contextual (
    id SERIAL PRIMARY KEY,
    chat_id INTEGER NOT NULL REFERENCES Chat(id) ON DELETE CASCADE,
    contextType VARCHAR(100),
    suggestion_msg VARCHAR(1000)
);
