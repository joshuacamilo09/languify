# ORDEM DO PROJETO (Parte do BackEnd)


## O ideal é progredir por camadas de dependência — ou seja, construir de baixo para cima:

### Ordem-Estado

1. Models e Repositories -> Já está
2. Configurações base	SecurityConfig, ApplicationConfig, JwtService, etc.. ->	Já está
3. Auth (Google + JWT) ->	Já está
4. UserController e UserService	->	Já está
5. Chat (WebSockets)	Configurar STOMP + endpoints de chat -> Compila 100% mas não está testado
6. TranslationService -> 	
7. AgentContextual ->
8. Testes e documentação ->
