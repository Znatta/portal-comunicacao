# Portal de Comunicação - Backend

API REST robusta desenvolvida para o portal de comunicação interno da Porto Seguro. Este projeto segue as melhores práticas de mercado, utilizando **Spring Boot 3** e **Java 17**.

## 🚀 Tecnologias Utilizadas

- **Java 17** (LTS)
- **Spring Boot 3.5.x**
- **Spring Security** (Autenticação via JWT Stateless)
- **Spring Data JPA** (Persistência de dados)
- **Flyway** (Versionamento de Banco de Dados)
- **H2 Database** (Banco em memória para desenvolvimento local)
- **SpringDoc OpenAPI (Swagger)** (Documentação interativa)
- **Lombok** (Produtividade no código)
- **Jakarta Validation** (Validação de Beans)
- **NotebookLM**: (Análise e contextualização da documentação técnica e arquitetural)
- **Gemini CLI**: (Refinamento da documentação e auxílio na implementação de boas práticas)

## 🛠️ Como Rodar Localmente

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd portal-comunicacao
```

### 2. Configurar Propriedades Locais (⚠️ IMPORTANTE)

Para garantir a segurança, as configurações de banco de dados e segredos de segurança estão protegidas e **não devem ser enviadas ao Git**. Siga os passos abaixo para configurar seu ambiente:

1. Localize o arquivo de exemplo: `src/main/resources/application-local.properties.example`.
2. Crie uma cópia deste arquivo no mesmo diretório chamada `application-local.properties`.
3. Edite o novo arquivo `application-local.properties` e informe a sua chave secreta para geração do token:

```properties
api.security.token.secret=INFORME_AQUI_UMA_CHAVE_SECRETA_SEGURA
```

### 3. Executar a aplicação

Você pode rodar a aplicação via Maven pelo terminal:

```bash
./mvnw spring-boot:run
```

Ou diretamente pela sua IDE favorita (IntelliJ IDEA recomendada), executando a classe `PortalComunicacaoApplication`.

## 📖 Documentação da API (Swagger)

Com a aplicação rodando, acesse a documentação interativa para testar os endpoints:
🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> **Dica**: Para endpoints protegidos, utilize o botão **Authorize** no Swagger e informe o token JWT obtido no endpoint de `/api/login` (prefixo: `Bearer` ).

## 🔒 Segurança e Boas Práticas

- **Tratamento de Exceções**: A API utiliza um `GlobalExceptionHandler` para retornar erros padronizados em JSON.
- **Auditoria**: Perfis de acesso configurados (`ADMIN` e `PRODUCER`) com permissões granulares via `@PreAuthorize`.
- **Migrações**: O banco de dados é inicializado e versionado automaticamente pelo Flyway (ver pasta `db/migration`).

---

Desenvolvido com ❤️ pelo **Time Academy** | **VILT Group - Breaking Boundaries**