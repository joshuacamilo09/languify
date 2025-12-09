{\rtf1\ansi\ansicpg1252\cocoartf2822
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fnil\fcharset0 .AppleSystemUIFontMonospaced-Regular;}
{\colortbl;\red255\green255\blue255;\red74\green80\blue93;\red155\green162\blue177;\red184\green93\blue213;
\red197\green136\blue83;\red136\green185\blue102;}
{\*\expandedcolortbl;;\cssrgb\c36078\c38824\c43922;\cssrgb\c67059\c69804\c74902;\cssrgb\c77647\c47059\c86667;
\cssrgb\c81961\c60392\c40000;\cssrgb\c59608\c76471\c47451;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardirnatural\partightenfactor0

\f0\fs26 \cf2 -- Listar todos os usu\'e1rios\cf3 \
\cf4 SELECT\cf3  * \cf4 FROM\cf3  usr;\
\
\cf2 -- Listar todas as mensagens de um chat\cf3 \
\cf4 SELECT\cf3  m.msg_id, m.msg_content, u.usr_firstName\
\cf4 FROM\cf3  msg m\
\cf4 JOIN\cf3  usr u \cf4 ON\cf3  m.msg_usr_sender_id = u.usr_id\
\cf4 WHERE\cf3  m.msg_chat_location_id = \cf5 1\cf3 ;\
\
\cf2 -- Listar todas as tradu\'e7\'f5es de mensagens\cf3 \
\cf4 SELECT\cf3  mr.msgrec_id, mr.msg_rec_recorded_content, l.lang_name\
\cf4 FROM\cf3  msg_rec mr\
\cf4 JOIN\cf3  lang l \cf4 ON\cf3  mr.msgrec_lang_id = l.lang_id;\
\
\cf2 -- Listar chats de uma cidade espec\'edfica\cf3 \
\cf4 SELECT\cf3  cl.chat_loc_id, c.chatt_name, l.loc_city\
\cf4 FROM\cf3  chat_loc cl\
\cf4 JOIN\cf3  chatt c \cf4 ON\cf3  cl.chat_id = c.chatt_id\
\cf4 JOIN\cf3  loc l \cf4 ON\cf3  cl.chat_loc_loc_id = l.loc_id\
\cf4 WHERE\cf3  l.loc_city = \cf6 'Lisbon'\cf3 ;}