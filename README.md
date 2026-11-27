# Somativa – Backend, DevOps e Computação em Nuvem

[![CI - Build e Testes](https://github.com/bruninxsz/Somativa-DevOpsNuvem/actions/workflows/pipeline.yml/badge.svg)](https://github.com/bruninxsz/Somativa-DevOpsNuvem/actions/workflows/pipeline.yml)

Duas APIs REST em **Java 21 + Spring Boot 4** desenvolvidas para a CloudTech Solutions, com testes automatizados (JUnit 5 + MockMvc), integração contínua no GitHub Actions e deploy na Railway.

| Projeto | Descrição | URL pública (Railway) |
|---|---|---|
| [exercicio01](exercicio01/) | Monitoramento de máquinas industriais | `https://bruno-nuvem01.up.railway.app` |
| [exercicio02](exercicio02/) | Gerenciamento do acervo da biblioteca | `https://bruno-nuvem02.up.railway.app` |

## Tecnologias

- Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation)
- Banco H2 em memória
- JUnit 5 + MockMvc
- Maven (com Maven Wrapper)
- Docker (build multi-stage)
- GitHub Actions (CI)
- Railway (deploy)

## Estrutura do repositório

```
Somativa-DevOpsNuvem/
├── exercicio01/                 # API de monitoramento de máquinas
│   ├── src/main/java/.../
│   │   ├── controller/          # MaquinasController, DadosController, InfoController
│   │   ├── dto/                 # MaquinasDTO, DadosDTO
│   │   ├── model/               # Maquinas, Dados, Status
│   │   └── repository/
│   ├── src/test/java/.../       # MaquinasApiTests (MockMvc)
│   ├── Dockerfile
│   └── pom.xml
├── exercicio02/                 # API da biblioteca
│   ├── src/main/java/.../
│   │   ├── controller/          # LivroController, InfoController
│   │   ├── model/               # Livro
│   │   └── repository/
│   ├── src/test/java/.../       # LivrosApiTests (MockMvc)
│   ├── Dockerfile
│   └── pom.xml
├── .github/workflows/pipeline.yml
├── .gitignore
└── README.md
```

## Como executar localmente

Pré-requisito: **JDK 21** instalado e a variável `JAVA_HOME` apontando para ele (`java -version` deve mostrar 21).

```bash
cd exercicio01        # ou exercicio02
./mvnw spring-boot:run          # Linux/macOS/Git Bash
mvnw.cmd spring-boot:run        # Windows (cmd/PowerShell)
```

A API sobe em `http://localhost:8080`. A porta pode ser trocada pela variável de ambiente `PORT`.

### Rodar os testes automatizados

```bash
./mvnw test
```

### Gerar o .jar

```bash
./mvnw package
java -jar target/*.jar
```

### Rodar com Docker

```bash
cd exercicio01
docker build -t monitoramento-maquinas .
docker run -p 8080:8080 monitoramento-maquinas
```

---

## Exercício 01 – Monitoramento de Máquinas

| Método | Endpoint | Descrição | Sucesso | Erro |
|---|---|---|---|---|
| GET | `/` | Página inicial | 200 | |
| GET | `/status` | Status da aplicação | 200 | |
| GET | `/versao` | Versão da aplicação | 200 | |
| GET | `/maquinas` | Lista as máquinas | 200 | |
| GET | `/maquinas/{id}` | Consulta uma máquina | 200 | 404 |
| POST | `/maquinas/cadastro` | Cadastra uma máquina | 201 | 400 |
| PUT | `/maquinas/{id}` | Atualiza uma máquina | 200 | 400 / 404 |
| DELETE | `/maquinas/{id}` | Remove a máquina e seu histórico de produção | 204 | 404 |
| GET | `/producao` | Lista todos os registros de produção | 200 | |
| GET | `/producao?maquinaId={id}` | Histórico de produção e consumo de uma máquina | 200 | |
| GET | `/producao/{id}` | Consulta um registro de produção | 200 | 404 |
| POST | `/producao` | Registra dados de produção | 201 | 400 / 404 |
| PUT | `/producao/{id}` | Atualiza um registro de produção | 200 | 400 / 404 |
| DELETE | `/producao/{id}` | Remove um registro de produção | 204 | 404 |

Valores aceitos em `status`: `"em operação"`, `"desligada"`, `"desativada"`.

**POST `/maquinas/cadastro`**
```json
{
  "nome": "Máquina CNC 01",
  "tipo": "CNC",
  "setor": "Usinagem",
  "status": "em operação"
}
```

**POST `/producao`**
```json
{
  "maquinaId": 1,
  "consumoKw": 12.5,
  "horarioAcionamento": "08:30",
  "pecasProduzidas": 150
}
```

**GET `/status`**
```json
{
  "status": "online",
  "mensagem": "Aplicacao de monitoramento em execucao"
}
```

---

## Exercício 02 – Biblioteca CloudTech

| Método | Endpoint | Descrição | Sucesso | Erro |
|---|---|---|---|---|
| GET | `/` | Mensagem de boas-vindas | 200 | |
| GET | `/livros` | Lista todos os livros | 200 | |
| GET | `/livros/{id}` | Consulta um livro pelo ID | 200 | 404 |
| POST | `/livros/cadastrar` | Cadastra um novo livro | 201 | 400 |
| PUT | `/livros/alterar/{id}` | Atualiza um livro | 200 | 400 / 404 |
| DELETE | `/livros/{id}` | Remove um livro | 204 | 404 |
| GET | `/documentacao` | Lista os endpoints disponíveis | 200 | |

**POST `/livros/cadastrar`**
```json
{
  "titulo": "Banco de Dados para Iniciantes",
  "autor": "Maria Souza",
  "editora": "EduTech",
  "ano": 2023,
  "categoria": "Informática",
  "status": "Disponível"
}
```

---

## Testes automatizados

| Projeto | Classe | O que é verificado |
|---|---|---|
| exercicio01 | `MaquinasApiTests` | página inicial (200), `/status` online, cadastro de máquina (201), listagem de máquinas (200), registro de produção (201), máquina inexistente (404) |
| exercicio02 | `LivrosApiTests` | página inicial (200), listagem (200), cadastro (201), consulta por ID (200), alteração (200), ID inexistente (404), `/documentacao` (200) |

## Pipeline de Integração Contínua

O workflow [.github/workflows/pipeline.yml](.github/workflows/pipeline.yml) roda a cada push em `main`, `develop` e `feature/**` e a cada pull request para `main` e `develop`. Para os dois projetos, em paralelo, ele:

1. Faz o checkout do código.
2. Configura o Java 21 (Temurin), com cache do Maven.
3. Baixa as dependências (`dependency:go-offline`).
4. Compila a aplicação (`compile`).
5. Executa os testes JUnit 5 + MockMvc e gera o `.jar` (`package`).
6. Publica o relatório dos testes e o `.jar` como artefatos da execução.

## Deploy na Railway

1. Na Railway: **New Project → Deploy from GitHub repo** e selecione este repositório.
2. Crie **um serviço por exercício**. Em *Settings → Source → Root Directory*, informe `exercicio01` (ou `exercicio02`).
3. A Railway detecta o `Dockerfile` da pasta e faz o build da imagem.
4. A aplicação lê a porta da variável `PORT` definida pela Railway (`server.port=${PORT:8080}`).
5. Em *Settings → Networking*, clique em **Generate Domain** para obter a URL pública.
6. Valide com `GET https://<seu-dominio>/status` (exercício 01) ou `GET https://<seu-dominio>/documentacao` (exercício 02).

## Fluxo de versionamento

- `main`: versão estável, publicada na Railway
- `develop`: integração das funcionalidades
- `feature/*`: uma branch por funcionalidade (ex.: `feature/crud-maquinas`, `feature/testes`, `feature/pipeline`), integrada à `develop` via pull request
