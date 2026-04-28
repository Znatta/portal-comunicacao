-- Criando VIEW para auditoria unificada
CREATE OR REPLACE VIEW vw_auditoria_geral AS
SELECT 
    'NOTICIA' AS tipo_recurso,
    id AS recurso_id,
    titulo AS identificador,
    data_atualizacao,
    atualizado_por,
    data_criacao,
    criado_por
FROM noticias
UNION ALL
SELECT 
    'CATEGORIA' AS tipo_recurso,
    id AS recurso_id,
    nome AS identificador,
    data_atualizacao,
    atualizado_por,
    data_criacao,
    criado_por
FROM categorias
UNION ALL
SELECT 
    'USUARIO' AS tipo_recurso,
    id AS recurso_id,
    email AS identificador,
    data_atualizacao,
    atualizado_por,
    data_criacao,
    criado_por
FROM usuarios;
