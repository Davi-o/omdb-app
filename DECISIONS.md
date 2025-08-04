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
Documentação com Swagger

- Foi adicionado o Swagger/OpenAPI à aplicação para documentar os endpoints de forma automática.
- Está usando `springdoc-openapi` (versão para Spring Boot 3).
- A documentação está disponível em `/swagger-ui.html`.

---

Frontend com React + Bootstrap

- O frontend foi criado com React e React-Bootstrap, para facilitar a criação de UI sem precisar trabalhar diretamente com CSS.
- Inicialmente foi tentado o uso do `create-react-app`, mas ele apresentou problemas com dependências e scripts devido à versão de Node suportada no macOS Catalina (10.15).
- Decidi então adotar o Vite, que possui menos dependências legadas, tornando a configuração mais leve e compatível com esse ambiente antigo.

---

Contexto da máquina de desenvolvimento:

- O desenvolvimento do frontend foi iniciado utilizando um MacBook Air que não possui suporte oficial à versão atual do Docker Desktop.
- Por esse motivo, o frontend não roda em container. Está sendo executado localmente com Node.js.

---

Pendências:
- Backend: 
    - Testes.[]
    - Adicionar OpenApi. [x]
    - Adicionar os filtros de ano e tipo da produção (?y=2025&type=episode). [x]
    - Adicionar consulta por ID. [x]
    - Adicionar opção de detalhe de plot (curto ou completo). [x]
    - Adicionar paginação. [x]
    - Adicionar cache para não ficar enviando várias requisições ao OMDB. [x]
    - Adicionar Rate-Limit.
    - Testar o cache.
    - Refinar validações e tratamento de erros.
    - Adicionar Logs.
- Frontend:
    - Tela de busca com filtros. [x]
    - Listagem com poster, titulo e ano. [x]
    - Paginação. [x]
    - Mostrar detalhes ao selecionar produção. []
    - Adicionar feedback de carregamento. []