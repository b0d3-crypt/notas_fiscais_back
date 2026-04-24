-- Run in this order due to FK dependencies

CREATE TABLE endereco (
    cd_endereco SERIAL PRIMARY KEY,
    nm_logradouro VARCHAR(100),
    ds_endereco  VARCHAR(255),
    nr_cep       VARCHAR(8),
    nr_endereco  VARCHAR(50),
    bairro       VARCHAR(50),
    cidade       VARCHAR(50),
    estado       VARCHAR(50)
);

CREATE TABLE pessoa (
    cd_pessoa   SERIAL PRIMARY KEY,
    cd_endereco INTEGER      NOT NULL,
    nm_pessoa   VARCHAR(150),
    nr_telefone VARCHAR(20),
    nr_cpf      VARCHAR(14),
    nm_email    VARCHAR(255) UNIQUE,
    CONSTRAINT FK_Endereco_Pessoa FOREIGN KEY (cd_endereco) REFERENCES endereco (cd_endereco)
);

CREATE TABLE web_user (
    cd_web_user        SERIAL PRIMARY KEY,
    cd_pessoa          INTEGER      NOT NULL,
    password           VARCHAR(255) NOT NULL,
    tp_responsabilidade SMALLINT    NOT NULL DEFAULT 1,
    -- 0 = ADMIN, 1 = USER
    CONSTRAINT FK_Pessoa_Web_User FOREIGN KEY (cd_pessoa) REFERENCES pessoa (cd_pessoa)
);

CREATE TABLE arquivo (
    cd_arquivo      SERIAL PRIMARY KEY,
    nm_arquivo      VARCHAR(255) NOT NULL,
    dt_arquivo      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    tp_arquivo      SMALLINT     NOT NULL,
    -- 1=PDF, 2=JPEG, 3=PNG, 4=GIF
    caminho_arquivo VARCHAR(255) NOT NULL
);

CREATE TABLE descricao_despesa (
    cd_descricao_despesa SERIAL PRIMARY KEY,
    cd_arquivo           INTEGER        NOT NULL,
    cd_pessoa            INTEGER        NOT NULL,
    ds_despesa           VARCHAR,
    dt_despesa           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    vl_despesa           NUMERIC(10, 2) NOT NULL,
    CONSTRAINT FK_despesa_arquivo FOREIGN KEY (cd_arquivo) REFERENCES arquivo (cd_arquivo) ON DELETE RESTRICT,
    CONSTRAINT FK_despesa_pessoa FOREIGN KEY (cd_pessoa) REFERENCES pessoa (cd_pessoa) ON DELETE RESTRICT
);

CREATE INDEX idx_despesa_dt ON descricao_despesa (dt_despesa DESC);
CREATE INDEX idx_despesa_pessoa ON descricao_despesa (cd_pessoa);
