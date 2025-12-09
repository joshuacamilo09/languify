CREATE TABLE lang (
    lang_id SERIAL PRIMARY KEY,
    lang_name VARCHAR(40) NOT NULL
);

CREATE TABLE country (
    cty_id SERIAL PRIMARY KEY,
    cty_name VARCHAR(30) NOT NULL
);

CREATE TABLE loc (
    loc_id SERIAL PRIMARY KEY,
    loc_city VARCHAR(30) NOT NULL,
    loc_cty_id INT NOT NULL REFERENCES country(cty_id)
);

CREATE TABLE usr (
    usr_id SERIAL PRIMARY KEY,
    usr_firstName VARCHAR(60) NOT NULL,
    usr_lastName VARCHAR(60),
    usr_email VARCHAR(60),
    usr_nativeLanguage_id INT NOT NULL REFERENCES lang(lang_id)
);

CREATE TABLE chatt (
    chatt_id SERIAL PRIMARY KEY,
    chatt_name VARCHAR(40),
    chat_creationdate DATE NOT NULL
);

CREATE TABLE agnt_context (
    agnt_context_id SERIAL PRIMARY KEY,
    agnt_context_name VARCHAR(40) NOT NULL
);

CREATE TABLE chat_loc (
    chat_loc_id SERIAL PRIMARY KEY,
    chat_id INT NOT NULL REFERENCES chatt(chatt_id),
    chat_loc_agnt_context_id INT REFERENCES agnt_context(agnt_context_id),
    chat_loc_loc_id INT NOT NULL REFERENCES loc(loc_id)
);

CREATE TABLE msg (
    msg_id SERIAL PRIMARY KEY,
    msg_chat_location_id INT NOT NULL REFERENCES chat_loc(chat_loc_id),
    msg_usr_sender_id INT NOT NULL REFERENCES usr(usr_id),
    msg_content TEXT NOT NULL,
    msg_sendedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE msg_rec (
    msgrec_id SERIAL PRIMARY KEY,
    msgrec_msg_id INT NOT NULL REFERENCES msg(msg_id),
    msgrec_lang_id INT NOT NULL REFERENCES lang(lang_id),
    msg_rec_recorded_content TEXT NOT NULL
);
    msgrec_lang_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  lang(lang_id),\
    msg_rec_recorded_content TEXT \cf2 NOT NULL\cf3 \
);}
