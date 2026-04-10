# Diretrizes de Desenvolvimento - Portal de Comunicação

Este arquivo contém instruções e padrões específicos para a evolução desta aplicação.

## Pendências de Implementação (Documentação OpenAPI/Swagger)

- **Documentação de Respostas de Erro**: Adicionar as anotações `@ApiResponses` e `@ApiResponse` nos métodos dos Controllers para documentar explicitamente os cenários de erro (400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found).
- **Enriquecimento de Metadados**: Adicionar informações de contato, versão da API e uma descrição detalhada no cabeçalho da documentação OpenAPI.
- **Descrições de Parâmetros**: Melhorar a documentação dos parâmetros de paginação (`Pageable`) e filtros de busca para que fiquem claros para os consumidores da API.
