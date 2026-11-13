# NoiseHunter — ambiente Docker isolado

Esta atualizacao **adiciona** arquivos ao projeto em funcionamento e **nao substitui** os arquivos atuais de backend, frontend, banco ou a configuracao `docker-compose.yml` antiga.

## Aplicar

1. Extraia este ZIP. Copie o conteudo da pasta `NoiseHunter_Docker_Atualizacao` para a raiz do seu projeto `NoiseHunter_MySQL` (a pasta que ja contem `backend`, `frontend`, `sql`). Mantenha as subpastas. Os arquivos novos serao: `compose.noisehunter.yaml`, `.env.example`, `backend/Dockerfile`, `backend/.dockerignore`, `frontend/Dockerfile`, `frontend/.dockerignore`, `frontend/nginx.conf`.
2. Abra o terminal do VS Code na **raiz** do projeto (nao dentro de `frontend` ou `backend`).
3. Copie o arquivo de exemplo com `Copy-Item .env.example .env` e edite **apenas o `.env` da raiz** com duas senhas fortes de teste, diferentes entre si. Nao as publique e nao use senhas reais de contas pessoais.
4. Confira a configuracao: `docker compose -f compose.noisehunter.yaml config --quiet`
5. Construa e inicie: `docker compose -f compose.noisehunter.yaml up -d --build`
6. Veja se os tres servicos estao ativos: `docker compose -f compose.noisehunter.yaml ps`. A primeira compilacao pode consumir varios GB de imagens e downloads.
7. Acesse o frontend em http://localhost:3001; backend em http://localhost:8082/api/health. O banco demo responde no localhost:3307, com usuario `noisehunter` e a senha MYSQL_APP_PASSWORD do .env.
8. No Workbench, crie **outra conexao** para localhost:3307; nao substitua a conexao do MySQL original. Verifique `SHOW TABLES` no banco `noisehunter_atividade` e os cadastros feitos pela nova pagina.

## Se algo falhar

- `docker compose -f compose.noisehunter.yaml logs --tail 100 backend` para logs do Spring Boot.
- `docker compose -f compose.noisehunter.yaml logs --tail 100 mysql` para erros do banco.
- `docker compose -f compose.noisehunter.yaml logs --tail 100 frontend` para erros do Nginx.
- Se a porta 3001, 8082 ou 3307 estiver ocupada, pare o servico conflitante ou escolha outra porta **do lado esquerdo** de `:` no arquivo compose.
- O banco demo e criado na primeira inicializacao do volume. O arquivo `sql/01_criar_banco_atividade.sql` eh executado somente quando o volume esta vazio. Nao rode comandos `down -v`, `volume prune` ou `system prune --volumes`, pois podem apagar os dados do Docker.

## Como parar e reiniciar

- Pausar contêineres: `docker compose -f compose.noisehunter.yaml stop`.
- Iniciar de novo: `docker compose -f compose.noisehunter.yaml start`.
- Remover contêineres **preservando dados**: `docker compose -f compose.noisehunter.yaml down`.

## Diferencas intencionais

- Instalacao atual (fora do Docker): frontend 3000, backend 8081, MySQL 3306.
- Demonstração Docker: frontend 3001, backend 8082, MySQL 3307.
- Dentro da rede Docker, o backend acessa `mysql:3306`, sem usar `localhost`.
- Os dados dos dois bancos estao separados. O novo banco de demonstracao inicia vazio. Para copiar dados reais de testes, importe um dump de teste conscientemente, evitando informações pessoais.
- O projeto tem API sem autenticacao: **use apenas localmente e nao publique na rede/internet**.
- Este pacote foi conferido estaticamente. A execucao Docker deve ser validada na maquina do IFBA.
