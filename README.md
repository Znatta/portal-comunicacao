# Portal de Comunicação Interna - Backend (Porto Seguro)

API REST robusta para o portal de comunicação interno. Este projeto utiliza **Spring Boot 3**, **Java 17** e uma arquitetura baseada em serviços de nuvem para agilidade de entrega (MVP).

## 🧰 Tecnologias Utilizadas

- **Java 17** (LTS)
- **Spring Boot 3**
- **Spring Security + JWT** (autenticação stateless)
- **Spring Data JPA + PostgreSQL** (persistência de dados)
- **Supabase Storage** (armazenamento de imagens)
- **Brevo** (e-mails transacionais)
- **Flyway** (versionamento de banco de dados)
- **H2 Database** (ambiente local/dev)
- **SpringDoc OpenAPI (Swagger)** (documentação interativa)
- **Lombok** (produtividade de código)
- **Jakarta Validation** (validação de beans)
- **Docker** (build e deploy)
- **NotebookLM** (análise/contextualização de documentação técnica)
- **Gemini CLI** (apoio em documentação e práticas de desenvolvimento)

## 🚀 Como Rodar Localmente

Existem duas formas de rodar o projeto:

### A. Modo "Plug-and-Play" (H2 Database)
Ideal para testar apenas a lógica de negócio sem configurar contas externas.
1. Execute a aplicação com o profile `dev-h2`:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev-h2
   ```
2. Acesse o console do banco: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:portaldb`)

### B. Modo Produção Local (Supabase + Brevo)
Necessário para testar Upload de Imagens e Envio de E-mails reais.
1. Copie o arquivo `src/main/resources/application-local.properties.example` para `src/main/resources/application-local.properties`.
2. Preencha as credenciais dos serviços externos (ver seção de Infraestrutura abaixo).
3. Rode normalmente: `./mvnw spring-boot:run`

## 🛠️ Infraestrutura e Contas Externas

Se você estiver assumindo o projeto agora, precisará configurar os seguintes serviços:

### 1. Supabase (Banco de Dados e Storage)
- Crie um projeto no Supabase (guarde a senha definida na criação, ela será sua `SPRING_DATASOURCE_PASSWORD`).
- No menu **Storage**, crie um bucket público chamado `noticias`.
- Vá em **Project Settings > Database > Connection string**: selecione a aba **JDBC** para obter sua `SPRING_DATASOURCE_URL`.
- Vá em **Project Settings > API**: obtenha a `Project URL` (sua `SUPABASE_URL`) e a `service_role` key (sua `SUPABASE_KEY`).

### 2. Brevo (Envio de E-mails)
- Crie uma conta no Brevo.
- Obtenha sua API Key V3 em `SMTP & API`.
- Valide o seu e-mail de remetente no painel do Brevo.

### 3. Variáveis de Ambiente Obrigatórias (Produção/Render)
- `SPRING_DATASOURCE_URL`: URL de conexão do Postgres.
- `SPRING_DATASOURCE_USERNAME`: Usuário do banco (ex: `postgres`).
- `SPRING_DATASOURCE_PASSWORD`: Senha do banco.
- `SUPABASE_URL` e `SUPABASE_KEY`: Credenciais para acesso ao Storage.
- `JWT_SECRET`: Chave secreta para assinatura dos tokens JWT.
- `API_CORS_ALLOWED_ORIGINS`: URLs permitidas separadas por vírgula.
- `BREVO_API_KEY`: Chave da API do Brevo.
- `BREVO_FROM_EMAIL`: E-mail de remetente validado no Brevo.

## 🚢 Estratégia de Deploy

O deploy é realizado no **Render** via **Docker**. Caso você utilize um repositório privado corporativo (como GitLab ou Azure DevOps), siga este fluxo para manter o deploy automatizado:

1. **Repositório de Deploy**: Crie um repositório público (ou privado) no **GitHub**, que servirá como espelho para o Render.
2. **Configuração no Render**: Conecte sua conta do GitHub ao Render e crie um novo **Web Service** apontando para este repositório. Selecione o Runtime como **Docker**.
3. **Fluxo de Mirroring (Dica de Produtividade)**: Caso trabalhe em dois repositórios simultaneamente, recomenda-se criar um script de automação (`.sh` ou `.ps1`) que realize o *mirror* entre eles. Exemplo de comandos essenciais:
   ```bash
   git push --mirror https://github.com/seu-usuario/seu-repo-espelho.git
   ```
4. **Continuous Deployment**: Uma vez configurado, qualquer *push* no repositório do GitHub disparará o build e deploy automático no Render através do `Dockerfile` multi-stage presente na raiz.


## 📖 Documentação da API (Swagger)

Acesse: `http://localhost:8080/swagger-ui.html`

---
Desenvolvido com ❤️ pelo **Time Academy** | **VILT Group - Breaking Boundaries**
