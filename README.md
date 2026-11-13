# NoiseHunter — versão integrada ao modelo de banco da atividade (MySQL)

Adaptação do projeto **IFBA Financeiro** para o NoiseHunter. Arquitetura mantida: Vue.js 3, Vuetify, Axios, Java 21+, Spring Boot e MySQL. Esta versão implementa **as três tabelas e relacionamentos propostos pelos alunos**: `usuarios → dispositivos → ocorrencias`.

## IMPORTANTE: preservar os dados anteriores

O protótipo anterior do NoiseHunter usa o banco `noisehunter`, com uma tabela `dispositivos` em formato antigo e uma tabela `eventos_sonoros`. Para evitar mexer nos registros que vocês já testaram, esta atualização usa **um novo banco chamado `noisehunter_atividade`**. Nenhum SQL deste pacote remove ou modifica a base `noisehunter` anterior.

- O script `sql/01_criar_banco_atividade.sql` cria o banco novo com **os mesmos campos e FKs da atividade do professor**.
- Opcionalmente, `sql/02_importar_dados_antigos_OPCIONAL.sql` copia para o novo banco os sensores e os eventos cadastrados no protótipo anterior, mantendo IDs. **Execute apenas se a base antiga `noisehunter` tiver as tabelas `dispositivos` e `eventos_sonoros`**.
- O script não importa descrições/observações antigas porque esses campos não fazem parte do esquema pedido na atividade. A tabela `usuarios` começa vazia; cadastre usuários pela página e vincule os sensores editando cada um.
- Se você já possui `noisehunter_atividade` com outras tabelas/dados, faça backup antes de executar scripts.

## 1. Banco de dados (Workbench)

1. Deixe o MySQL Server conectado no Workbench.
2. Abra o arquivo `sql/01_criar_banco_atividade.sql` e execute **todo o conteúdo**.
3. Se desejar reaproveitar os dados do projeto anterior, execute depois `sql/02_importar_dados_antigos_OPCIONAL.sql` (se as tabelas antigas existirem).
4. Confira:

```sql
USE noisehunter_atividade;
SHOW TABLES;
SELECT * FROM usuarios;
SELECT * FROM dispositivos;
SELECT * FROM ocorrencias;
```

O backend agora usa `spring.jpa.hibernate.ddl-auto=validate`, pois o esquema foi criado pelo SQL dos alunos. A validação protege contra alterações automáticas de tabelas. **Não configure o servidor HTTP na porta 3306**: `3306` é a porta do MySQL, o backend usa `8081`.

## 2. Backend (VS Code — terminal dentro de `backend`)

Reutilize as credenciais do MySQL que já funcionavam no protótipo anterior.

Arquivo: `backend/src/main/resources/application.properties`

```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/noisehunter_atividade?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bahia
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
spring.jpa.hibernate.ddl-auto=validate
```

> Ajuste usuário, senha, host e porta conforme a conexão no Workbench. **Não compartilhe a senha nem publique o arquivo com credenciais reais.**

Execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Confirme `http://localhost:8081/api/health`. Quando a aplicação inicia, o terminal fica ocupado: **isso é normal!** Não inicie outro backend na mesma porta.

## 3. Frontend (outro terminal — dentro de `frontend`)

```powershell
npm install
npm run dev
```

Entre em **http://localhost:3000**. O frontend comunica com a API na porta 8081 (`frontend/.env`). Não precisa reinstalar pacotes se copiou o frontend para uma pasta que já tenha as dependências; se for uma pasta nova, `npm install` será necessário.

## 4. Demonstração na aula

1. **Usuários:** cadastrar uma pessoa responsável pelo equipamento, com nome, e-mail e senha.
2. **Dispositivos:** cadastrar o sensor `Sensor IFBA 01`, local `Campus Paulo Afonso`, status `ATIVO`, escolhendo o usuário responsável.
3. **Ocorrências:** escolher o sensor, tipo `VEICULO_BARULHENTO`, valor `95.50` dB e local. O horário é registrado automaticamente.
4. **Dashboard:** atualizar os indicadores e verificar o histórico.
5. **Workbench:** executar:

```sql
USE noisehunter_atividade;
SELECT id, nome, email FROM usuarios;
SELECT * FROM dispositivos;
SELECT * FROM ocorrencias;

-- Mostrar o relacionamento das três tabelas:
SELECT o.id AS ocorrencia_id, o.tipo_som, o.decibeis, o.data_hora,
       d.nome AS dispositivo, u.nome AS responsavel
FROM ocorrencias o
JOIN dispositivos d ON d.id = o.dispositivo_id
LEFT JOIN usuarios u ON u.id = d.usuario_id
ORDER BY o.data_hora DESC;
```

## 5. Campos e relações iguais à atividade

| Tabela | Campos | Chaves |
|---|---|---|
| `usuarios` | id, nome, email, senha | PK id |
| `dispositivos` | id, nome, localizacao, status, usuario_id | PK id; FK usuario_id → usuarios(id) |
| `ocorrencias` | id, tipo_som, decibeis, data_hora, localizacao, dispositivo_id | PK id; FK dispositivo_id → dispositivos(id) |

Os formulários usam `usuarioId` e `dispositivoId` em JSON, e o Spring/JPA grava nas colunas `usuario_id`/`dispositivo_id`. O cadastro de ocorrências é **manual/simulado**. O tipo do som **não é detectado automaticamente** e não representa um disparo confirmado.

## 6. Endpoints implementados

- `GET /api/health`
- `GET /api/usuarios`, `GET /api/usuarios/{id}`, `POST /api/usuarios`, `PUT /api/usuarios/{id}`, `DELETE /api/usuarios/{id}`
- `GET /api/dispositivos`, `GET /api/dispositivos/{id}`, `POST /api/dispositivos`, `PUT /api/dispositivos/{id}`, `DELETE /api/dispositivos/{id}`
- `GET /api/ocorrencias`, `GET /api/ocorrencias/{id}`, `POST /api/ocorrencias`, `DELETE /api/ocorrencias/{id}`

**Exemplos de JSON:**

```json
{"nome":"Usuário Teste","email":"teste@example.com","senha":"senha-exemplo-123"}
```

```json
{"nome":"Sensor IFBA 01","localizacao":"Campus Paulo Afonso","status":"ATIVO","usuarioId":1}
```

```json
{"dispositivoId":1,"tipoSom":"VEICULO_BARULHENTO","decibeis":95.50,"localizacao":"Campus Paulo Afonso"}
```

- Senhas novas são **armazenadas como hash BCrypt**, nunca retornadas na API (campo `senha` oculto na serialização).
- O cadastro de usuários **não é autenticação**. A API não possui controle de acesso e deve ser usada **somente em ambiente local de demonstração**, sem dados pessoais reais e sem ser publicada na internet. Uma próxima etapa poderá implementar autenticação/autorização e proteção de dados.
- Um usuário não pode ser excluído enquanto possuir dispositivos; um dispositivo não pode ser excluído enquanto possuir ocorrências, respeitando os vínculos do banco.

## 7. O que foi modificado

- Backend: entidades `Usuario`, `Dispositivo`, `Ocorrencia`, seus repositórios, DTOs, controladores e configuração do banco.
- Frontend: nova página de usuários, dispositivos com responsável/status, ocorrências com localização e dashboard já existente preservado.
- SQL: criação isolada do modelo do professor e importação opcional dos dados do protótipo antigo.
- Mantidos os scripts de inicialização `mvnw.cmd` e `npm run dev`.
