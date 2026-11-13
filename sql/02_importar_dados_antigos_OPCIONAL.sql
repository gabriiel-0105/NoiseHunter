-- OPCIONAL: executar APENAS se o projeto anterior usava:
-- noisehunter.dispositivos / noisehunter.eventos_sonoros.
-- Executar DEPOIS de 01_criar_banco_atividade.sql.
-- Nao apaga nem altera as tabelas antigas. Preserva IDs.
-- Se ja havia outros dados em noisehunter_atividade, faca backup antes.

-- Os sensores antigos nao tinham usuario nem status; ficam ATIVO e sem usuario.
INSERT IGNORE INTO noisehunter_atividade.dispositivos
    (id, nome, localizacao, status, usuario_id)
SELECT id, LEFT(nome, 100), LEFT(localizacao, 100), 'ATIVO', NULL
FROM noisehunter.dispositivos;

-- Os eventos antigos eram simulados: agora sao ocorrencias da atividade.
-- A descricao/observacao anterior nao faz parte da estrutura SQL entregue pelo aluno.
INSERT IGNORE INTO noisehunter_atividade.ocorrencias
    (id, tipo_som, decibeis, data_hora, localizacao, dispositivo_id)
SELECT e.id, LEFT(e.tipo, 50), ROUND(e.decibeis, 2),
       e.registrado_em, LEFT(d.localizacao, 100), e.dispositivo_id
FROM noisehunter.eventos_sonoros AS e
JOIN noisehunter_atividade.dispositivos AS d ON d.id = e.dispositivo_id;

SELECT COUNT(*) AS dispositivos_importados FROM noisehunter_atividade.dispositivos;
SELECT COUNT(*) AS ocorrencias_importadas FROM noisehunter_atividade.ocorrencias;
