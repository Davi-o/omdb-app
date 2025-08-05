# Projeto Fullstack OMDB Gateway

Aplicação fullstack composta por um backend em Spring Boot (Java 21) que atua como gateway para a API do OMDB, e um frontend em React.

---

## Estrutura do Projeto
```
/
├── backend/ # Projeto Java (Spring Boot)
├── frontend/ # Projeto React
└── docker-compose.yml
```

---

## Pré-requisitos

- Docker e Docker Compose instalados
- Chave da API do OMDB (gratuita em http://www.omdbapi.com/apikey.aspx)

---

## Configuração

1. Atentar-se ao arquivo `backend/src/main/resources/application.properties` com o seguinte conteúdo:

```properties
omdb.api.url=https://www.omdbapi.com/
omdb.api.key=SUA_API_KEY_AQUI
```

## Rodando a aplicação

Na raiz do projeto (onde está o docker-compose.yml):
```
docker-compose up --build
```
A aplicação estará disponível em: http://localhost:3000
O Swagger pode ser acessada em: http://localhost:8080/swagger-ui.html