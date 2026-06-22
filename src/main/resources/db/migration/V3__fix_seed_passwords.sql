-- Corrige os hashes de senha do seed (V2), que não correspondiam às senhas documentadas.
-- Os hashes abaixo são BCrypt ($2a$, força 10) e foram validados contra as senhas em texto.
--
-- Credenciais de acesso após esta migration:
--   ADMIN -> email: admin@notas.com | senha: admin123
--   USER  -> email: user@notas.com  | senha: user123

-- ADMIN (cd_pessoa = 1) -> senha "admin123"
UPDATE web_user
SET password = '$2a$10$weTVFiOUaMx.6hF3V/9OpeUD5pcnNO2c.jIDKGvGb75kveSQ4bCqm'
WHERE cd_pessoa = 1;

-- USER (cd_pessoa = 2) -> senha "user123"
UPDATE web_user
SET password = '$2a$10$TDKVUcxY1xlxoZzMLd6rFOLeVQT2XviXqph1Szsn/mnX0F83JRHkK'
WHERE cd_pessoa = 2;
