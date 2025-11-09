\# 📘 Relatório de Projeto — Languify



\## 1. Guiões (User Flows)



Os guiões descrevem como os usuários interagem com o Languify em diferentes contextos de uso. O foco é oferecer uma experiência fluida de comunicação multilíngue em tempo real.



\### Guião 1 — Tradução em tempo real

\*\*Objetivo:\*\* Permitir que dois usuários conversem em idiomas diferentes, com tradução automática instantânea.



\*\*Fluxo:\*\*

1\. O usuário faz login com a conta Google.

2\. Seleciona o idioma nativo e o idioma de destino.

3\. Entra em um chat com outro usuário.

4\. Cada mensagem é traduzida em tempo real antes de ser exibida.

5\. O usuário pode visualizar a mensagem original e a traduzida.



\### Guião 2 — Chat entre dois usuários

\*\*Objetivo:\*\* Oferecer uma experiência de conversa fluida com suporte a WebSocket.



\*\*Fluxo:\*\*

1\. O usuário entra no aplicativo e autentica-se (JWT + Google OAuth2).

2\. Seleciona um contato disponível.

3\. O WebSocket é aberto e mensagens são trocadas em tempo real.

4\. O chat exibe mensagens, status online e histórico armazenado no banco de dados.



\### Guião 3 — Agente Contextual

\*\*Objetivo:\*\* Assistir o usuário durante conversas ou navegação, oferecendo sugestões contextuais baseadas no conteúdo.



\*\*Fluxo:\*\*

1\. O usuário inicia uma conversa.

2\. O agente monitora o contexto do diálogo.

3\. Sugere traduções alternativas, expressões culturais ou respostas automatizadas.

4\. O usuário pode aceitar ou ignorar a sugestão.



\### Guião 4 — Mapa Interativo

\*\*Objetivo:\*\* Permitir que o usuário explore locais e traduza informações contextuais de mapas em tempo real.



\*\*Fluxo:\*\*

1\. O usuário abre o mapa.

2\. Seleciona um local ou recebe recomendações baseadas em idioma.

3\. O sistema exibe informações traduzidas dinamicamente.

4\. O agente contextual pode sugerir frases úteis relacionadas ao local.



---



\## 2. Personas



\### Persona 1 — Sofia (Estudante de línguas)

\- \*\*Idade:\*\* 22 anos  

\- \*\*Objetivo:\*\* Aprender idiomas praticando com falantes nativos.  

\- \*\*Motivação:\*\* Aperfeiçoar a fluência através de conversas naturais.  

\- \*\*Cenário:\*\* Usa o chat e a tradução em tempo real para praticar espanhol com colegas.



\### Persona 2 — Marco (Profissional remoto)

\- \*\*Idade:\*\* 34 anos  

\- \*\*Objetivo:\*\* Comunicar-se com clientes de outros países.  

\- \*\*Motivação:\*\* Evitar barreiras linguísticas em reuniões e chats.  

\- \*\*Cenário:\*\* Usa o Languify como ferramenta de trabalho, integrando com o agente contextual para obter respostas rápidas.



\### Persona 3 — Aiko (Turista)

\- \*\*Idade:\*\* 28 anos  

\- \*\*Objetivo:\*\* Entender informações locais durante viagens.  

\- \*\*Motivação:\*\* Explorar lugares sem depender de guias turísticos.  

\- \*\*Cenário:\*\* Usa o mapa interativo e a tradução em tempo real para ler placas, menus e recomendações locais.



---



\## 3. Diagrama de Classes (Esboço Conceitual)



```mermaid

classDiagram

&nbsp;   class User {

&nbsp;       Long id

&nbsp;       String name

&nbsp;       String email

&nbsp;       String language

&nbsp;       String profileImage

&nbsp;   }



&nbsp;   class Chat {

&nbsp;       Long id

&nbsp;       User sender

&nbsp;       User receiver

&nbsp;       List~Message~ messages

&nbsp;   }



&nbsp;   class Message {

&nbsp;       Long id

&nbsp;       String content

&nbsp;       String translatedContent

&nbsp;       LocalDateTime timestamp

&nbsp;   }



&nbsp;   class Translation {

&nbsp;       Long id

&nbsp;       String sourceLanguage

&nbsp;       String targetLanguage

&nbsp;       String originalText

&nbsp;       String translatedText

&nbsp;   }



&nbsp;   class AgentContext {

&nbsp;       Long id

&nbsp;       String contextType

&nbsp;       String suggestion

&nbsp;   }



&nbsp;   User "1" -- "many" Chat : participates

&nbsp;   Chat "1" -- "many" Message : contains

&nbsp;   Message "1" --> "1" Translation : uses

&nbsp;   User "1" --> "many" AgentContext : receives



