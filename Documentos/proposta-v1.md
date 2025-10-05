# Relatório de Projeto – **Languify**

## 1. Proposta Inicial

O projeto propõe o desenvolvimento de uma aplicação móvel inovadora que permite **conversação e tradução em tempo real**, promovendo a comunicação natural entre pessoas de diferentes idiomas.

---

## 2. Nome do Projeto

**Languify**

---

## 3. Enquadramento do Projeto

### Ideia

O utilizador fala na sua língua nativa e a aplicação traduz automaticamente a fala para o idioma-alvo selecionado, permitindo uma comunicação fluida e imediata.

### Contexto

Vivemos numa era globalizada, em que viajar, estudar e trabalhar no estrangeiro é cada vez mais comum. Apesar dos avanços tecnológicos, **as barreiras linguísticas** continuam a dificultar a comunicação entre culturas.

Existem diversas aplicações de tradução, mas a maioria **não oferece uma experiência de conversação natural**, com **voz e contexto em tempo real**.
O **Languify** surge para preencher essa lacuna, combinando:

- **Utilidade imediata:** tradução instantânea da fala.
- **Experiência enriquecida:** histórico de conversas, mapa interativo e agente contextual inteligente.

### Objetivos

Facilitar a comunicação entre pessoas de diferentes nacionalidades de forma **rápida, natural e intuitiva**.

### Público-Alvo

O Languify destina-se principalmente a:

- **Turistas** que visitam países cuja língua não dominam.
- **Residentes estrangeiros** que necessitam comunicar no dia-a-dia.
- **Profissionais internacionais** que participam em reuniões, conferências ou viagens de trabalho.

De modo geral, a aplicação é voltada para **qualquer utilizador que deseje ultrapassar barreiras linguísticas** em situações práticas e quotidianas.

### Aplicações Semelhantes

Atualmente, **não existe uma aplicação que una tradução em tempo real, interação por voz e contexto conversacional** da forma que o Languify propõe.

---

## 4. Versão Preliminar (Guiões de Teste – em discussão)

### Guião 1 — Core: Traduzir fala em tempo real

**Objetivo:** traduzir a fala do utilizador e reproduzir em texto e voz.
**Pré-condições:** app instalada; microfone autorizado; internet ativa; idiomas origem/alvo definidos.

**Passos**

1. Abrir a aplicação.
2. Ver o ecrã inicial (mapa com pins).
3. Tocar no botão “Falar” (ou segurar, conforme UI).
4. Dizer uma frase na língua de origem.
5. Soltar o botão/encerrar captação.

**Resultado esperado**

- A transcrição aparece quase em tempo real.
- A tradução surge em texto no idioma-alvo.
- O áudio TTS da tradução é reproduzido.
- A conversa é registada no histórico com data, idiomas e (se autorizado) localização.

---

### Guião 2 — Consultar histórico de transcrições

**Objetivo:** rever conversas anteriores em lista minimalista.
**Pré-condições:** existir pelo menos uma conversa guardada.

**Passos**

1. No ecrã inicial, tocar no botão “Histórico” no footer.
2. Ver a lista de conversas (últimas no topo), com metadados mínimos (data, línguas, local opcional).
3. Tocar numa conversa.
4. Ler transcrição original e tradução; navegar entre mensagens se houver várias.

**Resultado esperado**

- A lista carrega rapidamente e mantém ordenação por data.
- O detalhe mostra texto original e traduzido de forma legível.
- Existe ação para voltar à lista sem perda de posição.

---

### Guião 3 — Ver conversas no mapa

**Objetivo:** localizar visualmente conversas passadas.
**Pré-condições:** localização permitida em conversas anteriores ou geotags salvas.

**Passos**

1. No ecrã inicial (mapa), visualizar pins correspondentes a conversas.
2. Fazer zoom/arrastar o mapa para explorar diferentes regiões.
3. Tocar num pin.
4. Ver um cartão/resumo com data, línguas e excerto.
5. Tocar em “Abrir conversa” no cartão.

**Resultado esperado**

- Pins exibidos nas posições corretas.
- Cartão aparece com resumo consistente.
- Ao abrir, o app navega para o detalhe da conversa no histórico.
- Retorno ao mapa preserva o zoom/posição anterior.

---

## 5. Casos de Utilização

1. **Tradução em tempo real** (caso principal)
2. **Histórico de conversas**
3. **Mapa interativo de conversas**

---

## 6. Plano de Trabalho

**Backend:** Henrique Krause, Joshua Camilo, Carlos Lima
**Frontend:** Henrique Krause, Joshua Camilo, Carlos Lima
**Relatório:** Henrique Krause, Joshua Camilo, Carlos Lima

---

## 7. Project Charter e WBS

### Nome do Projeto

**Conversação + Tradução em Tempo Real**

### Objetivo do Projeto

Desenvolver uma aplicação móvel capaz de **traduzir fala em tempo real**, **registar interações** e **fornecer contexto adicional**, facilitando a comunicação entre falantes de diferentes línguas.

### Escopo

- Tradução de voz em tempo real (texto e áudio).
- Registro de histórico de conversas com metadados (data, localização, idiomas).
- Visualização das conversas num mapa global.
- Agente contextual com sugestões linguísticas e culturais.

### Stakeholders

- **Equipa de Desenvolvimento:** Joshua Camilo, Henrique Krausse, Carlos Lima
- **Utilizadores Finais:** turistas, residentes estrangeiros e profissionais internacionais
- **Orientador:** Prof. Pedro Rosa (PDV)

### Principais Entregáveis

- Aplicação funcional de tradução em tempo real
- Histórico de chats com metadados
- Mapa de conversas interativo
- Agente contextual
- Relatório final e documentação técnica
- Poster promocional da aplicação
- Vídeo demonstrativo (máx. 2 minutos)

### Riscos

- Limitações na precisão da tradução automática
- Dependência de conexão estável à internet

### Restrições

- Primeira entrega: **28 de setembro de 2025**
- Duração máxima do vídeo: **2 minutos**
- Plataforma-alvo: **Android**

### WBS (Work Breakdown Structure)

O WBS do projeto foi desenvolvido no Figma e está disponível em:
🔗 [https://paper-framer-18782285.figma.site/](https://paper-framer-18782285.figma.site/)

---

## 8. Requisitos

### 8.1 Funcionais

- Tradução em tempo real
- Seleção de idiomas
- Histórico de conversas
- Agente contextual
- Interface intuitiva
- Notificações e alertas

### 8.2 Não Funcionais

**Desempenho:**
A tradução deve ocorrer com **latência mínima**.

**Compatibilidade:**
Aplicação destinada a **dispositivos móveis Android**.

**Segurança e Privacidade:**

- Armazenamento seguro de dados (histórico, localização, preferências).
- Possibilidade de o utilizador **desativar a recolha de localização**.

**Confiabilidade:**
A aplicação deve funcionar mesmo com **conexão instável**, utilizando cache temporário.

**Usabilidade:**
Interface **acessível e intuitiva**, adaptada a diferentes perfis e idades, com **suporte multilíngue**.

**Escalabilidade:**
Capacidade de suportar **múltiplos utilizadores simultaneamente** sem perda de desempenho.

---

## 9. Modelo de Domínio

O modelo de domínio foi elaborado no Figma e está disponível em:
🔗 [https://calm-sheep-51083484.figma.site/](https://calm-sheep-51083484.figma.site/)

---

## 10. Mockups e Interface (em discussão)

Os mockups da interface foram criados no Figma e podem ser consultados em:
🔗 [https://www.figma.com/design/4QeVgYX8BKdHH4e2BxCzAs/Languify?m=auto&t=c9Xi7ZrSi3K2aGxm-1](https://www.figma.com/design/4QeVgYX8BKdHH4e2BxCzAs/Languify?m=auto&t=c9Xi7ZrSi3K2aGxm-1)

---

## 11. Planificação (Gráfico de Gantt)

A planificação do projeto foi desenvolvida no ClickUp e pode ser consultada em:
🔗 [https://sharing.clickup.com/90151678051/g/h/2kyqaw33-615/eae2709196bb020](https://sharing.clickup.com/90151678051/g/h/2kyqaw33-615/eae2709196bb020)

---

## 12. Poster da Aplicação (em discussão)

O poster foi elaborado no Figma e será disponibilizado em breve:
🔗 [https://www.figma.com/design/4QeVgYX8BKdHH4e2BxCzAs/Languify?m=auto&t=c9Xi7ZrSi3K2aGxm-1](https://www.figma.com/design/4QeVgYX8BKdHH4e2BxCzAs/Languify?m=auto&t=c9Xi7ZrSi3K2aGxm-1)
