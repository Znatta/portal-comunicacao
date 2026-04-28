-- Adicionando colunas de auditoria na tabela categorias
ALTER TABLE categorias ADD COLUMN data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE categorias ADD COLUMN data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE categorias ADD COLUMN criado_por VARCHAR(255) DEFAULT 'SISTEMA';
ALTER TABLE categorias ADD COLUMN atualizado_por VARCHAR(255) DEFAULT 'SISTEMA';

-- Adicionando colunas de auditoria na tabela noticias
ALTER TABLE noticias ADD COLUMN data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE noticias ADD COLUMN data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE noticias ADD COLUMN criado_por VARCHAR(255) DEFAULT 'SISTEMA';
ALTER TABLE noticias ADD COLUMN atualizado_por VARCHAR(255) DEFAULT 'SISTEMA';

-- Adicionando colunas de auditoria na tabela usuarios
ALTER TABLE usuarios ADD COLUMN data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE usuarios ADD COLUMN data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE usuarios ADD COLUMN criado_por VARCHAR(255) DEFAULT 'SISTEMA';
ALTER TABLE usuarios ADD COLUMN atualizado_por VARCHAR(255) DEFAULT 'SISTEMA';
