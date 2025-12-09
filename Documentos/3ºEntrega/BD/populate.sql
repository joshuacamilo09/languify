-- Linguagens
INSERT INTO lang (lang_name) VALUES 
('English'), 
('Portuguese'), 
('Spanish');

-- Países
INSERT INTO country (cty_name) VALUES 
('Portugal'), 
('Spain'), 
('USA');

-- Localizações (cidades)
INSERT INTO loc (loc_city, loc_cty_id) VALUES
('Lisbon', 1),
('Porto', 1),
('Madrid', 2),
('New York', 3);

-- Usuários
INSERT INTO usr (usr_firstName, usr_lastName, usr_email, usr_nativeLanguage_id) VALUES
('Joshua', 'Camilo', 'joshua@example.com', 2),
('Carlos', 'Lima', 'carlos@example.com', 2),
('Henrique', 'Krause', 'henrique@example.com', 1);

-- Chats
INSERT INTO chatt (chatt_name, chat_creationdate) VALUES
('Chat 1', CURRENT_DATE),
('Chat 2', CURRENT_DATE);

-- Agentes de Contexto
INSERT INTO agnt_context (agnt_context_name) VALUES
('Tourism'),
('Education'),
('Business');

-- Chat Localizações
INSERT INTO chat_loc (chat_id, chat_loc_agnt_context_id, chat_loc_loc_id) VALUES
(1, 1, 1),
(1, 2, 2),
(2, 3, 3);

-- Mensagens
INSERT INTO msg (msg_chat_location_id, msg_usr_sender_id, msg_content) VALUES
(1, 1, 'Hello!'),
(1, 2, 'Hi, how are you?'),
(2, 3, 'Buenos días!');

-- Mensagens traduzidas
INSERT INTO msg_rec (msgrec_msg_id, msgrec_lang_id, msg_rec_recorded_content) VALUES
(1, 1, 'Hello!'),
(2, 1, 'Hi, how are you?'),
(3, 2, 'Bom dia!');
