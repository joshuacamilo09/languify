-- Inserir usuários
INSERT INTO users(name, email, password)
VALUES 
('Alice', 'alice@email.com', 'senha123'),
('Bob', 'bob@email.com', 'senha123'),
('Carol', 'carol@email.com', 'senha123'),
('David', 'david@email.com', 'senha123');

-- Inserir chats
INSERT INTO chats(user1_id, user2_id)
VALUES 
(1, 2),
(2, 3),
(1, 4);

-- Inserir mensagens
INSERT INTO messages(chat_id, sender_id, content)
VALUES
(1, 1, 'Olá Bob! Como vai?'),
(1, 2, 'Oi Alice! Estou bem, e você?'),
(2, 2, 'Oi Carol!'),
(2, 3, 'Oi Bob, tudo certo!'),
(3, 1, 'Oi David, vamos testar a tradução?');

-- Inserir agentes
INSERT INTO agents(name, description)
VALUES
('LanguifyBot', 'Assistente que ajuda com traduções em tempo real'),
('ChatHelper', 'Assistente para suporte de chats');

-- Inserir traduções de teste
INSERT INTO translations(user_id, original_text, translated_text, source_lang, target_lang)
VALUES
(1, 'Hello World', 'Olá Mundo', 'en', 'pt'),
(2, 'How are you?', 'Como você está?', 'en', 'pt'),
(3, 'Good morning', 'Bom dia', 'en', 'pt');
