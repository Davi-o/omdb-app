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

Build do frontend no Docker

- O Dockerfile do frontend está usando um container Nginx para servir os arquivos estáticos.

---

Documentação com Swagger

- Foi adicionado o Swagger/OpenAPI à aplicação para documentar os endpoints de forma automática.
- Está usando `springdoc-openapi` (versão para Spring Boot 3).
- A documentação está disponível em `/swagger-ui.html`.

---

Logs
- Os estão seguindo um padrão de `entity.action.status field='value'` ou `entity.action.field='value'`.

---

Refatoração do Backend motivada pela Integração com o Frontend

- Como o frontend foi feito separado do backend, quando fui integrar de verdade, percebi uma falha importante no gateway.
- Antes, ele só aceitava a URL no formato ?s={valor}, ignorando qualquer outro parâmetro como type, year ou paginação.
- Isso causava vários problemas, porque o frontend precisava enviar esses filtros, mas o backend simplesmente não se importava com eles.
- Por isso precisei refatorar o backend para corrigir essa questão da URL, tratando todos os parâmetros que o frontend manda.
- Também aproveitei para deixar o código da service mais limpo.
- Essa mudança foi essencial pra garantir que frontend e backend funcionem direitinho juntos, sem perder os filtros.

---

Frontend com React + Bootstrap

- O frontend foi criado com React e React-Bootstrap, para facilitar a criação de UI sem precisar trabalhar diretamente com CSS.
- Inicialmente foi tentado o uso do `create-react-app`, mas ele apresentou problemas com dependências e scripts devido à versão de Node suportada no macOS Catalina (10.15).
- Decidi então adotar o Vite, que possui menos dependências legadas, tornando a configuração mais leve e compatível com esse ambiente antigo.
- Vi a necessidade de usar `react-router-dom` para fazer o roteamento entre a home com as pesquisas e os detalhes das produções.

---

Contexto da máquina de desenvolvimento:

- O desenvolvimento do frontend foi iniciado utilizando um MacBook Air que não possui suporte oficial à versão atual do Docker Desktop.
- Posteriormente voltei a desenvolver na minha maquina com Linux Mint, onde pude então fazer o docker-compose integrando as duas aplicações.

---

Pendências:
- Backend: 
    - Aumentar cobertura dos testes.[x]
    - Adicionar OpenApi. [x]
    - Adicionar os filtros de ano e tipo da produção (?y=2025&type=episode). [x]
    - Adicionar consulta por ID. [x]
    - Adicionar opção de detalhe de plot (curto ou completo). [x]
    - Adicionar paginação. [x]
    - Adicionar cache para não ficar enviando várias requisições ao OMDB. [x]
    - Adicionar Rate-Limit. [x]
    - Testar o cache. [x]
    - Refinar validações e tratamento de erros. [x]
    - Adicionar Logs. [x]
- Frontend:
    - Tela de busca com filtros. [x]
    - Listagem com poster, titulo e ano. [x]
    - Paginação. [x]
    - Mostrar detalhes ao selecionar produção. [x]
    - Adicionar feedback de carregamento. [x]
    - Melhorar visual. [x]
    - Salvar busca ao sair do detalhe da produção. [x]
- Integração:
    - Dockerizar o front. [x]
    - Testar gateway. [x]