-- Todas as mensagens de um chat específico
SELECT * FROM messages WHERE chat_id = 1;

-- Última mensagem de cada chat
SELECT chat_id, MAX(sent_at) AS last_message_time
FROM messages
GROUP BY chat_id;

-- Listar todos os chats de um usuário
SELECT c.id AS chat_id, u1.name AS user1, u2.name AS user2
FROM chats c
JOIN users u1 ON c.user1_id = u1.id
JOIN users u2 ON c.user2_id = u2.id
WHERE c.user1_id = 1 OR c.user2_id = 1;

-- Listar traduções de um usuário
SELECT original_text, translated_text, source_lang, target_lang, created_at
FROM translations
WHERE user_id = 1;

-- Visão geral dos chats
SELECT * FROM chat_overview;
