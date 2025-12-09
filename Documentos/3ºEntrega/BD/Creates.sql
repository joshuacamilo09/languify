{\rtf1\ansi\ansicpg1252\cocoartf2822
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fnil\fcharset0 .AppleSystemUIFontMonospaced-Regular;}
{\colortbl;\red255\green255\blue255;\red184\green93\blue213;\red155\green162\blue177;\red197\green136\blue83;
}
{\*\expandedcolortbl;;\cssrgb\c77647\c47059\c86667;\cssrgb\c67059\c69804\c74902;\cssrgb\c81961\c60392\c40000;
}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardirnatural\partightenfactor0

\f0\fs26 \cf2 CREATE TABLE\cf3  lang (\
    lang_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    lang_name \cf4 VARCHAR\cf3 (\cf4 40\cf3 ) \cf2 NOT NULL\cf3 \
);\
\
\cf2 CREATE TABLE\cf3  country (\
    cty_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    cty_name \cf4 VARCHAR\cf3 (\cf4 30\cf3 ) \cf2 NOT NULL\cf3 \
);\
\
\cf2 CREATE TABLE\cf3  loc (\
    loc_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    loc_city \cf4 VARCHAR\cf3 (\cf4 30\cf3 ) \cf2 NOT NULL\cf3 ,\
    loc_cty_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  country(cty_id)\
);\
\
\cf2 CREATE TABLE\cf3  usr (\
    usr_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    usr_firstName \cf4 VARCHAR\cf3 (\cf4 60\cf3 ) \cf2 NOT NULL\cf3 ,\
    usr_lastName \cf4 VARCHAR\cf3 (\cf4 60\cf3 ),\
    usr_email \cf4 VARCHAR\cf3 (\cf4 60\cf3 ),\
    usr_nativeLanguage_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  lang(lang_id)\
);\
\
\cf2 CREATE TABLE\cf3  chatt (\
    chatt_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    chatt_name \cf4 VARCHAR\cf3 (\cf4 40\cf3 ),\
    chat_creationdate \cf4 DATE\cf3  \cf2 NOT NULL\cf3 \
);\
\
\cf2 CREATE TABLE\cf3  agnt_context (\
    agnt_context_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    agnt_context_name \cf4 VARCHAR\cf3 (\cf4 40\cf3 ) \cf2 NOT NULL\cf3 \
);\
\
\cf2 CREATE TABLE\cf3  chat_loc (\
    chat_loc_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    chat_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  chatt(chatt_id),\
    chat_loc_agnt_context_id \cf4 INT\cf3  \cf2 REFERENCES\cf3  agnt_context(agnt_context_id),\
    chat_loc_loc_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  loc(loc_id)\
);\
\
\cf2 CREATE TABLE\cf3  msg (\
    msg_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    msg_chat_location_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  chat_loc(chat_loc_id),\
    msg_usr_sender_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  usr(usr_id),\
    msg_content TEXT \cf2 NOT NULL\cf3 ,\
    msg_sendedAt \cf4 TIMESTAMP\cf3  \cf2 DEFAULT\cf3  CURRENT_TIMESTAMP\
);\
\
\cf2 CREATE TABLE\cf3  msg_rec (\
    msgrec_id SERIAL \cf2 PRIMARY KEY\cf3 ,\
    msgrec_msg_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  msg(msg_id),\
    msgrec_lang_id \cf4 INT\cf3  \cf2 NOT NULL\cf3  \cf2 REFERENCES\cf3  lang(lang_id),\
    msg_rec_recorded_content TEXT \cf2 NOT NULL\cf3 \
);}