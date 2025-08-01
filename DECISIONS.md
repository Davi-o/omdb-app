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
- Maven 3.2.5 está sendo usado.

---

Gateway com Spring Mvc

- o gateway foi feito usando WebClient com spring webflux, mas acabei trocando por spring mvc

- o motivo principal da troca foi por não entender direito o funcionamento do WebFlux e achar que ia acabar gastando muito tempo com isso

- como a aplicação é pequena e não precisa de alta concorrência ou reatividade, decidi ir pelo jeito que já conheço

- mantive o mesmo comportamento da api, só trocando as chamadas reativas por chamadas RestTemplate.

---

Build do backend no Docker

- O Dockerfile do backend faz o build do `.jar` diretamente.
- Não é necessário rodar `mvn package` manualmente antes.

---

Pendências:

- Adicionar opção de detalhe de plot (curto ou completo).
- Adicionar paginação.
- Adicionar cache para não ficar enviando várias requisições ao OMDB.