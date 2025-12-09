-- ======================================================
-- 3. Queries de Exemplo
-- ======================================================

-- Listar todos os usuários
SELECT * FROM usr;

-- Listar todas as mensagens de um chat
SELECT m.msg_id, m.msg_content, u.usr_firstName
FROM msg m
JOIN usr u ON m.msg_usr_sender_id = u.usr_id
WHERE m.msg_chat_location_id = 1;

-- Listar todas as traduções de mensagens
SELECT mr.msgrec_id, mr.msg_rec_recorded_content, l.lang_name
FROM msg_rec mr
JOIN lang l ON mr.msgrec_lang_id = l.lang_id;

-- Listar chats de uma cidade específica
SELECT cl.chat_loc_id, c.chatt_name, l.loc_city
FROM chat_loc cl
JOIN chatt c ON cl.chat_id = c.chatt_id
JOIN loc l ON cl.chat_loc_loc_id = l.loc_id
WHERE l.loc_city = 'Lisbon';
