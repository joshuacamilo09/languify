# API Languify - Documentação

## REST API

### Autenticação

#### Obter Sessão Atual

Retorna os dados da sessão do usuário autenticado.

**Endpoint:** `GET /auth/session`

**Headers:**

```
Authorization: Bearer {token}
```

**Resposta:** `200 OK`

```json
{
  "session": {
    "user": {
      "id": "uuid",
      "email": "string",
      "username": "string"
    }
  }
}
```

---

#### Login

Autentica um usuário com email e senha.

**Endpoint:** `POST /auth/sign-in`

**Body:**

```json
{
  "email": "string",
  "password": "string"
}
```

**Resposta:** `200 OK`

```json
{
  "token": "string"
}
```

**Nota:** Retorna resposta vazia se credenciais inválidas.

---

#### Registro

Cria uma nova conta de usuário.

**Endpoint:** `POST /auth/sign-up`

**Body:**

```json
{
  "email": "string",
  "username": "string",
  "password": "string"
}
```

**Resposta:** `200 OK`

```json
{
  "token": "string"
}
```

**Nota:** Retorna resposta vazia se email ou username já existir.

---

## WebSocket

### Conversação

Conexão WebSocket para gerenciar conversações em tempo real.

**Endpoint:** `ws://host/ws/conversation`

**Autenticação:** Token JWT via handshake

#### Mensagens do Cliente → Servidor

**Formato:**

```json
{
  "type": "string",
  "data": "object"
}
```

**Tipos:**

- `audio.chunk`: envia um pedaço de áudio em base64 (PCM16). Forma recomendada: `{"type":"audio.chunk","data":{"audio":"<base64>"}}`
- `audio.commit`: sinaliza que o buffer de áudio foi concluído e solicita geração de resposta. Payload vazio: `{"type":"audio.commit","data":{}}`
- `interrupt`: cancela a resposta atual, limpa buffer de entrada e mantém a conversa aberta. Payload vazio.
- `close`: encerra a conversa, fecha o cliente Realtime e finaliza o contexto. Payload vazio.

#### Mensagens do Servidor → Cliente

Formato idêntico (`type` + `data`, onde `data` contém apenas os campos específicos do evento):

- `conversation.initializing`: conversa criada e cliente Realtime sendo conectado. Payload vazio.
- `connection.initialized`: conexão com Realtime pronta para receber áudio. Payload vazio.
- `connection.initialization.failed`: falha na criação do contexto/conexão. Payload vazio.
- `conversation.language.detected`: idioma de origem detectado automaticamente. `{"language":"pt-BR"}`
- `conversation.transcription.delta`: atualização incremental do que foi transcrito. `{"delta":"trecho mais recente","full":"transcrição completa até agora"}`
- `conversation.translation.delta`: atualização incremental da tradução. `{"delta":"trecho traduzido","full":"tradução completa até agora"}`
- `conversation.audio.chunk`: áudio de resposta para reprodução. `{"audio":"<base64 PCM16>"}` (enviado continuamente enquanto a resposta é sintetizada).
- `conversation.response.complete`: fim de uma resposta. `{"responseId":"id-da-resposta"}` (pode ser nulo quando não retornado pelo provedor).
- `conversation.error`: erro no processamento da conversa ou do provedor. `{"error":"openai_error","message":"descrição"}` (o código varia conforme a origem).
