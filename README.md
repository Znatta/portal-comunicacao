# Portal de Comunicação - Backend

API REST robusta desenvolvida para o portal de comunicação interno da Porto Seguro. Este projeto segue as melhores práticas de mercado, utilizando **Spring Boot 3** e **Java 17**.

## 🚀 Tecnologias Utilizadas

- **Java 17** (LTS)
- **Spring Boot 3.5.x**
- **Spring Security & Java JWT** (Autenticação Stateless)
- **Spring Data JPA & PostgreSQL** (Persistência no Supabase)
- **Supabase Storage** (Armazenamento de imagens)
- **Brevo (antigo Sendinblue)** (Disparo de e-mails do newsletter)
- **Flyway** (Versionamento de Banco de Dados)
- **H2 Database** (Banco em memória para testes locais)
- **SpringDoc OpenAPI (Swagger)** (Documentação interativa)
- **Lombok** (Produtividade no código)
- **Jakarta Validation** (Validação de Beans)
- **Docker** (Empacotamento e Deploy)
- **NotebookLM**: (Análise e contextualização da documentação técnica e arquitetural)
- **Gemini CLI**: Refinamento de documentação, auxílio em boas práticas e suporte a comandos de Git.

## 🛠️ Como Rodar Localmente

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd portal-de-comunicacao-porto
```

### 2. Configurar Variáveis de Ambiente

O projeto utiliza o Supabase para Banco de Dados/Storage e Brevo para disparos de e-mail. Certifique-se de configurar as seguintes variáveis no seu ambiente ou no arquivo `application-local.properties`:

```properties
# Banco de Dados (PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:6543/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<sua-senha>

# Supabase (Storage e API)
SUPABASE_URL=https://<id-projeto>.supabase.co
SUPABASE_KEY=<sua-chave-anon-ou-service-role>

# Segurança
JWT_SECRET=<sua-chave-secreta-para-tokens>

# Brevo (E-mail Marketing / Transacional)
BREVO_API_KEY=<sua-api-key-v3>
BREVO_FROM_EMAIL=<seu-email-validado-no-brevo>
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
