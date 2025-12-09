{\rtf1\ansi\ansicpg1252\cocoartf2822
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fnil\fcharset0 .AppleSystemUIFontMonospaced-Regular;}
{\colortbl;\red255\green255\blue255;\red74\green80\blue93;\red155\green162\blue177;\red184\green93\blue213;
\red136\green185\blue102;\red197\green136\blue83;}
{\*\expandedcolortbl;;\cssrgb\c36078\c38824\c43922;\cssrgb\c67059\c69804\c74902;\cssrgb\c77647\c47059\c86667;
\cssrgb\c59608\c76471\c47451;\cssrgb\c81961\c60392\c40000;}
\paperw11900\paperh16840\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx560\tx1120\tx1680\tx2240\tx2800\tx3360\tx3920\tx4480\tx5040\tx5600\tx6160\tx6720\pardirnatural\partightenfactor0

\f0\fs26 \cf2 -- Linguagens\cf3 \
\cf4 INSERT INTO\cf3  lang (lang_name) \cf4 VALUES\cf3  \
(\cf5 'English'\cf3 ), \
(\cf5 'Portuguese'\cf3 ), \
(\cf5 'Spanish'\cf3 );\
\
\cf2 -- Pa\'edses\cf3 \
\cf4 INSERT INTO\cf3  country (cty_name) \cf4 VALUES\cf3  \
(\cf5 'Portugal'\cf3 ), \
(\cf5 'Spain'\cf3 ), \
(\cf5 'USA'\cf3 );\
\
\cf2 -- Localiza\'e7\'f5es (cidades)\cf3 \
\cf4 INSERT INTO\cf3  loc (loc_city, loc_cty_id) \cf4 VALUES\cf3 \
(\cf5 'Lisbon'\cf3 , \cf6 1\cf3 ),\
(\cf5 'Porto'\cf3 , \cf6 1\cf3 ),\
(\cf5 'Madrid'\cf3 , \cf6 2\cf3 ),\
(\cf5 'New York'\cf3 , \cf6 3\cf3 );\
\
\cf2 -- Usu\'e1rios\cf3 \
\cf4 INSERT INTO\cf3  usr (usr_firstName, usr_lastName, usr_email, usr_nativeLanguage_id) \cf4 VALUES\cf3 \
(\cf5 'Joshua'\cf3 , \cf5 'Camilo'\cf3 , \cf5 'joshua@example.com'\cf3 , \cf6 2\cf3 ),\
(\cf5 'Carlos'\cf3 , \cf5 'Lima'\cf3 , \cf5 'carlos@example.com'\cf3 , \cf6 2\cf3 ),\
(\cf5 'Henrique'\cf3 , \cf5 'Krause'\cf3 , \cf5 'henrique@example.com'\cf3 , \cf6 1\cf3 );\
\
\cf2 -- Chats\cf3 \
\cf4 INSERT INTO\cf3  chatt (chatt_name, chat_creationdate) \cf4 VALUES\cf3 \
(\cf5 'Chat 1'\cf3 , CURRENT_DATE),\
(\cf5 'Chat 2'\cf3 , CURRENT_DATE);\
\
\cf2 -- Agentes de Contexto\cf3 \
\cf4 INSERT INTO\cf3  agnt_context (agnt_context_name) \cf4 VALUES\cf3 \
(\cf5 'Tourism'\cf3 ),\
(\cf5 'Education'\cf3 ),\
(\cf5 'Business'\cf3 );\
\
\cf2 -- Chat Localiza\'e7\'f5es\cf3 \
\cf4 INSERT INTO\cf3  chat_loc (chat_id, chat_loc_agnt_context_id, chat_loc_loc_id) \cf4 VALUES\cf3 \
(\cf6 1\cf3 , \cf6 1\cf3 , \cf6 1\cf3 ),\
(\cf6 1\cf3 , \cf6 2\cf3 , \cf6 2\cf3 ),\
(\cf6 2\cf3 , \cf6 3\cf3 , \cf6 3\cf3 );\
\
\cf2 -- Mensagens\cf3 \
\cf4 INSERT INTO\cf3  msg (msg_chat_location_id, msg_usr_sender_id, msg_content) \cf4 VALUES\cf3 \
(\cf6 1\cf3 , \cf6 1\cf3 , \cf5 'Hello!'\cf3 ),\
(\cf6 1\cf3 , \cf6 2\cf3 , \cf5 'Hi, how are you?'\cf3 ),\
(\cf6 2\cf3 , \cf6 3\cf3 , \cf5 'Buenos d\'edas!'\cf3 );\
\
\cf2 -- Mensagens traduzidas\cf3 \
\cf4 INSERT INTO\cf3  msg_rec (msgrec_msg_id, msgrec_lang_id, msg_rec_recorded_content) \cf4 VALUES\cf3 \
(\cf6 1\cf3 , \cf6 1\cf3 , \cf5 'Hello!'\cf3 ),\
(\cf6 2\cf3 , \cf6 1\cf3 , \cf5 'Hi, how are you?'\cf3 ),\
(\cf6 3\cf3 , \cf6 2\cf3 , \cf5 'Bom dia!'\cf3 );}