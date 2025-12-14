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

- Atualmente não há mensagens do cliente implementadas
