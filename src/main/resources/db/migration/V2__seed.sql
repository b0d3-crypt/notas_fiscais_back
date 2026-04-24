-- Seed: 1 ADMIN user, 1 regular USER
-- admin password = "admin123" (BCrypt strength 10)
-- user  password = "user123"  (BCrypt strength 10)

INSERT INTO endereco (nm_logradouro, ds_endereco, nr_cep, nr_endereco, bairro, cidade, estado)
VALUES ('Rua das Flores', 'Bloco A', '40000000', '100', 'Centro', 'Salvador', 'BA');

INSERT INTO endereco (nm_logradouro, ds_endereco, nr_cep, nr_endereco, bairro, cidade, estado)
VALUES ('Av. Principal', NULL, '41000000', '200', 'Brotas', 'Salvador', 'BA');

INSERT INTO pessoa (cd_endereco, nm_pessoa, nr_telefone, nr_cpf, nm_email)
VALUES (1, 'Administrador', '71900000001', '000.000.000-00', 'admin@notas.com');

INSERT INTO pessoa (cd_endereco, nm_pessoa, nr_telefone, nr_cpf, nm_email)
VALUES (2, 'Usuário Padrão', '71900000002', '111.111.111-11', 'user@notas.com');

-- BCrypt hash of "admin123" (strength 10)
INSERT INTO web_user (cd_pessoa, password, tp_responsabilidade)
VALUES (1, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0);

-- BCrypt hash of "user123" (strength 10)
INSERT INTO web_user (cd_pessoa, password, tp_responsabilidade)
VALUES (2, '$2a$10$Oe4WqW8/RNvGFn0o/9pz2u9p9Y.5xk3Y5BqhkGD1NJ5wZ6Bz8l5/y', 1);
