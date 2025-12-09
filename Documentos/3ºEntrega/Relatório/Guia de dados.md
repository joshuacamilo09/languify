# Guia de Dados – Languify

## 1. Tabela: `lang` (Idiomas)
**Descrição:** Armazena os idiomas disponíveis na aplicação.  

| Campo      | Tipo        | Restrição | Descrição                     |
|-----------|------------|-----------|--------------------------------|
| lang_id   | SERIAL     | PK        | Identificador único do idioma |
| lang_name | VARCHAR(40)| NOT NULL  | Nome do idioma (ex.: Português, Inglês) |

---

## 2. Tabela: `country` (Países)
**Descrição:** Armazena os países disponíveis na aplicação.  

| Campo     | Tipo       | Restrição | Descrição                   |
|----------|------------|-----------|-----------------------------|
| cty_id   | SERIAL     | PK        | Identificador único do país |
| cty_name | VARCHAR(30)| NOT NULL  | Nome do país                |

---

## 3. Tabela: `loc` (Localizações / Cidades)
**Descrição:** Armazena as cidades disponíveis, cada cidade pertence a um país.  

| Campo      | Tipo         | Restrição                 | Descrição                     |
|-----------|--------------|---------------------------|--------------------------------|
| loc_id    | SERIAL       | PK                        | Identificador único da cidade |
| loc_city  | VARCHAR(30)  | NOT NULL                  | Nome da cidade                |
| loc_cty_id| INT          | FK → country(cty_id)      | País da cidade                |

---

## 4. Tabela: `usr` (Usuários)
**Descrição:** Armazena os dados dos usuários da aplicação.  

| Campo                 | Tipo        | Restrição                   | Descrição                       |
|----------------------|------------|-----------------------------|--------------------------------|
| usr_id               | SERIAL     | PK                          | Identificador único do usuário |
| usr_firstName        | VARCHAR(60)| NOT NULL                   | Primeiro nome                  |
| usr_lastName         | VARCHAR(60)|                             | Último nome                    |
| usr_email            | VARCHAR(60)|                             | Email do usuário               |
| usr_nativeLanguage_id| INT        | FK → lang(lang_id) NOT NULL | Idioma nativo do usuário       |

---

## 5. Tabela: `chatt` (Chats)
**Descrição:** Armazena os chats criados pelos usuários.  

| Campo             | Tipo       | Restrição | Descrição                  |
|------------------|------------|-----------|----------------------------|
| chatt_id         | SERIAL     | PK        | Identificador único do chat|
| chatt_name       | VARCHAR(40)|           | Nome do chat               |
| chat_creationdate| DATE       | NOT NULL  | Data de criação do chat    |

---

## 6. Tabela: `agnt_context` (Contexto do Chat)
**Descrição:** Armazena contextos que podem ser aplicados a conversas (ex.: Negócios, Educação).  

| Campo              | Tipo        | Restrição | Descrição                       |
|-------------------|------------|-----------|--------------------------------|
| agnt_context_id    | SERIAL     | PK        | Identificador único do contexto|
| agnt_context_name  | VARCHAR(40)| NOT NULL  | Nome do contexto               |

---

## 7. Tabela: `chat_loc` (Localização de Chats)
**Descrição:** Relaciona um chat com uma localização e, opcionalmente, com um contexto.  

| Campo                    | Tipo | Restrição                         | Descrição                             |
|--------------------------|------|-----------------------------------|--------------------------------------|
| chat_loc_id              | SERIAL | PK                             | Identificador único                  |
| chat_id                  | INT    | FK → chatt(chatt_id) NOT NULL  | Chat associado                        |
| chat_loc_agnt_context_id | INT    | FK → agnt_context(agnt_context_id) | Contexto do chat (opcional)        |
| chat_loc_loc_id          | INT    | FK → loc(loc_id) NOT NULL       | Localização do chat                   |

---

## 8. Tabela: `msg` (Mensagens)
**Descrição:** Armazena as mensagens enviadas nos chats.  

| Campo                | Tipo      | Restrição                     | Descrição                        |
|---------------------|-----------|-------------------------------|---------------------------------|
| msg_id               | SERIAL   | PK                            | Identificador da mensagem       |
| msg_chat_location_id | INT      | FK → chat_loc(chat_loc_id) NOT NULL | Chat/Localização da mensagem  |
| msg_usr_sender_id    | INT      | FK → usr(usr_id) NOT NULL     | Usuário que enviou a mensagem  |
| msg_content          | TEXT     | NOT NULL                      | Conteúdo da mensagem original  |
| msg_sendedAt         | TIMESTAMP| DEFAULT CURRENT_TIMESTAMP     | Data e hora do envio            |

---

## 9. Tabela: `msg_rec` (Mensagens Gravadas / Traduções)
**Descrição:** Armazena o conteúdo traduzido das mensagens.  

| Campo                 | Tipo | Restrição                       | Descrição                              |
|----------------------|------|---------------------------------|---------------------------------------|
| msgrec_id            | SERIAL | PK                             | Identificador da tradução              |
| msgrec_msg_id        | INT    | FK → msg(msg_id) NOT NULL      | Mensagem original                      |
| msgrec_lang_id       | INT    | FK → lang(lang_id) NOT NULL    | Idioma da tradução                      |
| msg_rec_recorded_content | TEXT | NOT NULL                     | Conteúdo da mensagem traduzida         |

---

### Observações
- Todas as relações **FK (Foreign Key)** garantem a integridade referencial entre as tabelas.  
- As tabelas `chat_loc` e `msg_rec` permitem **multi-localização e multi-idioma**, suportando o núcleo de chats do Languify.  
- O guia é útil para desenvolvedores, testers e para documentação oficial do projeto no GitHub.