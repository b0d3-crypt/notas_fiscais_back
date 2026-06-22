# Notas Fiscais — Backend (API)

API REST para **gestão de notas fiscais e despesas**, com autenticação JWT, upload de
comprovantes (PDF/imagem), controle de acesso por papel (ADMIN/USER) e busca de endereço
por CEP. Construída com **Spring Boot 3** e **arquitetura hexagonal** (ports & adapters).

> Frontend (Angular) que consome esta API: **[notas_fiscais](https://github.com/b0d3-crypt/notas_fiscais)**

---

## Índice
- [Stack](#stack)
- [Pré-requisitos](#pré-requisitos)
- [Passo a passo (do zero ao login)](#passo-a-passo-do-zero-ao-login)
- [Credenciais de acesso já criadas](#credenciais-de-acesso-já-criadas)
- [Configuração (`application.properties`)](#configuração-applicationproperties)
- [Como rodar](#como-rodar)
- [Endpoints da API](#endpoints-da-api)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Banco de dados](#banco-de-dados)
- [Testes](#testes)
- [Problemas comuns (FAQ)](#problemas-comuns-faq)

---

## Stack

| Camada            | Tecnologia                                  |
|-------------------|---------------------------------------------|
| Linguagem         | **Java 17**                                 |
| Framework         | Spring Boot 3.3.4 (Web, Data JPA, Security) |
| Banco de dados    | PostgreSQL                                  |
| Migrations        | Flyway                                      |
| Autenticação      | JWT (jjwt 0.11.5)                           |
| Mapeamento DTO    | MapStruct 1.5.5                             |
| Boilerplate       | Lombok                                      |
| Documentação API  | springdoc-openapi (Swagger UI)             |
| Build             | Maven                                       |

---

## Pré-requisitos

Instale antes de começar:

| Ferramenta   | Versão        | Observação |
|--------------|---------------|------------|
| **JDK 17**   | 17.x          | **Obrigatório.** O projeto **não compila com Java 8/11**. Confira com `java -version`. |
| **Maven**    | 3.6+          | `mvn -v`. O projeto não inclui Maven Wrapper (`mvnw`). |
| **PostgreSQL** | 13+ (testado em 16) | Servidor rodando em `localhost:5432`. |

> ⚠️ **Atenção à versão do Java.** Se o seu sistema usa Java 8 por padrão, defina o JDK 17
> apenas para este projeto antes de rodar o Maven:
> ```bash
> export JAVA_HOME=/caminho/para/jdk-17     # ex.: ~/.sdkman/candidates/java/17.0.19-tem
> export PATH=$JAVA_HOME/bin:$PATH
> java -version                              # deve mostrar "17"
> ```

---

## Passo a passo (do zero ao login)

### 1. Clonar o repositório
```bash
git clone https://github.com/b0d3-crypt/notas_fiscais_back.git
cd notas_fiscais_back
```

### 2. Criar o banco de dados PostgreSQL
A aplicação espera um banco chamado `notas_fiscais`. As **tabelas e os dados iniciais
(usuários admin e comum) são criados automaticamente pelo Flyway** na primeira execução —
você só precisa criar o banco vazio:

```bash
# Usando o usuário "postgres" com senha "1234" (padrão do projeto)
createdb -h localhost -U postgres notas_fiscais

# Alternativa via psql:
psql -h localhost -U postgres -c "CREATE DATABASE notas_fiscais;"
```

> Se o seu PostgreSQL usa **outro usuário/senha/porta**, ajuste o
> [`application.properties`](src/main/resources/application.properties) — veja a
> seção [Configuração](#configuração-applicationproperties).

### 3. (Opcional) Ajustar a pasta de upload
Os comprovantes enviados são salvos em disco. O caminho padrão é
`/home/danilo/Imagens/notas` (a pasta é criada automaticamente se não existir).
Se preferir outro local, altere `upload.dir` no `application.properties`.

### 4. Rodar a aplicação
```bash
mvn spring-boot:run
```
Na primeira execução o **Flyway aplica as migrations** (`V1` schema → `V2` seed →
`V3` correção de senhas) e a API sobe em **http://localhost:8080**.

### 5. Pronto — faça login
A API já vem com **dois usuários cadastrados** (veja abaixo). Use a tela de login do
frontend ou teste direto via cURL:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@notas.com","password":"admin123"}'
```
Resposta esperada (200): um JSON com `token`, `nmPessoa`, `role`, etc.

---

## Credenciais de acesso já criadas

O seed (`V2` + `V3`) cria automaticamente dois usuários. **Não é preciso cadastrar
nada manualmente** — basta logar:

| Papel  | E-mail (login)     | Senha      | `role` |
|--------|--------------------|------------|--------|
| **Administrador** | `admin@notas.com` | `admin123` | `0` (ADMIN) |
| **Usuário comum** | `user@notas.com`  | `user123`  | `1` (USER)  |

- **ADMIN** pode listar/criar usuários e editar/excluir qualquer despesa.
- **USER** acessa apenas o próprio perfil e gerencia as próprias despesas.

> O login é feito pelo **e-mail** (não pelo CPF). A senha trafega em texto no corpo da
> requisição e é validada contra um hash **BCrypt** no banco.

---

## Configuração (`application.properties`)

Arquivo: [`src/main/resources/application.properties`](src/main/resources/application.properties)

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/notas_fiscais
spring.datasource.username=postgres
spring.datasource.password=1234

# Flyway (mesmas credenciais do datasource)
spring.flyway.url=jdbc:postgresql://localhost:5432/notas_fiscais
spring.flyway.user=postgres
spring.flyway.password=1234

# JWT
jwt.secret=...               # chave de assinatura do token
jwt.expiration=86400000      # validade do token em ms (24h)

# Upload de arquivos
upload.dir=/home/danilo/Imagens/notas   # <-- ajuste para o seu ambiente
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=2MB

# CORS (origem do frontend)
cors.allowed-origins=http://localhost:4200

# Porta do servidor
server.port=8080
```

| O que mudar          | Quando                                              |
|----------------------|-----------------------------------------------------|
| `username`/`password`| Seu PostgreSQL não usa `postgres`/`1234`.           |
| `url` (porta/host)   | Banco em outra porta/máquina.                       |
| `upload.dir`         | Quase sempre — o padrão aponta para um home específico. |
| `cors.allowed-origins` | O frontend rodar em outra URL.                    |
| `jwt.secret`         | **Em produção**, troque por um segredo forte e externo. |

---

## Como rodar

**Modo desenvolvimento (recarrega ao salvar, via DevTools):**
```bash
mvn spring-boot:run
```

**Gerar o JAR e executar:**
```bash
mvn clean package            # gera target/notas-fiscais-back-0.0.1-SNAPSHOT.jar
java -jar target/notas-fiscais-back-0.0.1-SNAPSHOT.jar
```

**Documentação interativa (Swagger UI):** com a API no ar, acesse
👉 **http://localhost:8080/swagger-ui.html**

---

## Endpoints da API

Base URL: `http://localhost:8080`. Salvo o login e a busca de CEP, **todos exigem o header**
`Authorization: Bearer <token>`.

### Autenticação — `/auth`
| Método | Rota          | Auth | Descrição                         |
|--------|---------------|------|-----------------------------------|
| POST   | `/auth/login` | ❌   | Login. Body: `{ email, password }`. Retorna o token JWT. |

### Despesas — `/api/despesas`
| Método | Rota                        | Auth | Descrição |
|--------|-----------------------------|------|-----------|
| GET    | `/api/despesas?ano=&mes=`   | ✅   | Lista despesas (filtros opcionais por ano/mês). |
| GET    | `/api/despesas/{id}`        | ✅   | Detalha uma despesa. |
| POST   | `/api/despesas`             | ✅   | Cria despesa. `multipart/form-data`: `arquivo`, `dtDespesa` (dd/MM/yyyy), `vlDespesa`, `dsDespesa` (opcional). |
| PUT    | `/api/despesas/{id}`        | ✅   | Atualiza despesa (`multipart/form-data`, campos opcionais). |
| DELETE | `/api/despesas/{id}`        | ✅   | Exclui despesa (dono ou ADMIN). |
| GET    | `/api/despesas/{id}/download` | ✅ | Baixa o arquivo/comprovante. |

### Usuários — `/api/usuarios`
| Método | Rota                      | Auth | Descrição |
|--------|---------------------------|------|-----------|
| GET    | `/api/usuarios`           | ✅ ADMIN | Lista todos os usuários. |
| GET    | `/api/usuarios/{id}`      | ✅   | Detalha usuário (ADMIN: qualquer um; USER: só o próprio). |
| POST   | `/api/usuarios`           | ✅ ADMIN | Cria usuário. |
| PUT    | `/api/usuarios/{id}`      | ✅   | Atualiza dados (USER: só os próprios). |
| PUT    | `/api/usuarios/{id}/senha`| ✅   | Altera senha (USER: só a própria). |

### Correios — `/api/correios`
| Método | Rota                    | Auth | Descrição |
|--------|-------------------------|------|-----------|
| GET    | `/api/correios/{cep}`   | ❌   | Busca endereço por CEP. |

Todas as respostas seguem o envelope: `{ "data": <payload>, "message": <texto> }`.

---

## Estrutura do projeto

Arquitetura **hexagonal (ports & adapters)**:

```
src/main/java/com/notasfiscais/
├── domain/                 # Núcleo: modelos, enums, repositórios (interfaces), exceções
│   ├── model/              # Pessoa, WebUser, Endereco, Arquivo, DescricaoDespesa
│   ├── enums/              # TipoArquivoEnum, TpResponsabilidadeEnum
│   └── repositories/       # Interfaces (ports de saída)
├── application/            # Casos de uso, DTOs, queries, exceções de aplicação
│   ├── usecase/            # Regras de negócio (Create/Update/Delete/Find/Login...)
│   ├── dto/                # Objetos de entrada/saída
│   └── port/in/            # Ports de entrada (serviços)
└── infrastructure/         # Adapters (mundo externo)
    ├── adapter/inbound/    # Controllers REST (Auth, Despesa, Usuario, Correios)
    ├── adapter/outbound/   # Implementações JPA dos repositórios + mappers (MapStruct)
    ├── configuration/      # SecurityConfig + JWT (filter, service, properties)
    ├── storage/            # Gravação de arquivos em disco
    └── queries/            # Consultas de leitura

src/main/resources/
├── application.properties
└── db/migration/           # V1__schema.sql, V2__seed.sql, V3__fix_seed_passwords.sql
```

---

## Banco de dados

Gerenciado por **Flyway**. As migrations em `src/main/resources/db/migration/` rodam
automaticamente ao iniciar a aplicação:

| Migration | O que faz |
|-----------|-----------|
| `V1__schema.sql` | Cria as tabelas (`endereco`, `pessoa`, `web_user`, `arquivo`, `descricao_despesa`). |
| `V2__seed.sql`   | Insere os usuários admin e comum (+ endereços/pessoas). |
| `V3__fix_seed_passwords.sql` | Corrige os hashes BCrypt do seed para baterem com `admin123`/`user123`. |

Modelo simplificado: `pessoa` 1—1 `web_user`, `pessoa` 1—N `descricao_despesa`,
cada despesa referencia um `arquivo`.

> Para **recriar do zero**: `dropdb notas_fiscais && createdb notas_fiscais` e rode a
> aplicação novamente — o Flyway reconstrói tudo.

---

## Testes

```bash
mvn test
```
Inclui testes unitários dos casos de uso e testes de integração (`@SpringBootTest`)
de login e de despesas, usando **H2 em memória** (não precisa de PostgreSQL para testar).

---

## Problemas comuns (FAQ)

**`release version 17 not supported` / erro de compilação**
Você está usando Java 8/11. Aponte o `JAVA_HOME` para um JDK 17 (veja
[Pré-requisitos](#pré-requisitos)).

**`Connection refused` / `FATAL: database "notas_fiscais" does not exist`**
O PostgreSQL não está rodando ou o banco não foi criado. Veja
[Passo 2](#2-criar-o-banco-de-dados-postgresql).

**`password authentication failed for user "postgres"`**
Usuário/senha do `application.properties` não batem com o seu PostgreSQL. Ajuste
`spring.datasource.*` **e** `spring.flyway.*`.

**Login retorna "Credenciais inválidas" mesmo com a senha certa**
Garanta que a migration `V3` foi aplicada (corrige os hashes do seed). Verifique:
```bash
psql -h localhost -U postgres -d notas_fiscais -c \
  "SELECT version, success FROM flyway_schema_history WHERE version='3';"
```
Se não existir, recrie o banco (`dropdb`/`createdb`) e suba a aplicação de novo.

**CORS bloqueado no navegador**
Confirme que `cors.allowed-origins` inclui a URL do frontend (por padrão
`http://localhost:4200`).

**Porta 8080 já em uso**
Altere `server.port` no `application.properties` (e o `apiUrl` do frontend).
