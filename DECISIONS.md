# DECISIONS.md

Registro rápido das decisões técnicas tomadas até agora. Documento incompleto. Serve mais como referência de contexto durante o desenvolvimento.

---

Organização do projeto e reestruturação de pastas

- Separado o projeto em dois diretórios principais: `backend/` (Spring Boot) e `frontend/` (React).
- Essa mudança foi feita para facilitar a orquestração do backend e do frontend.
- Antes o backend estava sozinho. Agora a estrutura ficou mais coesa.

---

Backend com Spring Boot (Java 21)

- Usando Java 21 LTS e Spring Boot.
- Projeto gerado via Spring Initializr.
- Maven 4.0 está sendo usado. Nada específico, só acompanhando a versão padrão do projeto.

---

Gateway com Spring WebFlux

- Escolhido usar WebClient do Spring WebFlux para fazer as requisições HTTP.
- Escolhi o WebClient por ser mais moderno que RestTemplate.

---

Build do backend no Docker

- O Dockerfile do backend faz o build do `.jar` diretamente.
- Não é necessário rodar `mvn package` manualmente antes.

---

Pendências:

- Testes
- Adicionar OpenApi.
- Adicionar os filtros de ano e tipo da produção (?y=2025&type=episode).
- Adicionar consulta por ID.
- Adicionar opção de detalhe de plot (curto ou completo).
- Adicionar paginação.
- Adicionar cache para não ficar enviando várias requisições ao OMDB.