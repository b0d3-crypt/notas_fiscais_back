package com.notasfiscais.infrastructure.queries;

import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.dto.usuario.UsuarioListItemDTO;
import com.notasfiscais.application.queries.IUsuarioQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioQuery implements IUsuarioQuery {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UsuarioListItemDTO> findAll() throws Exception {
        String sql = """
                SELECT w.cd_web_user, p.cd_pessoa, p.nm_pessoa, p.nr_cpf, p.nm_email, w.tp_responsabilidade
                FROM web_user w
                JOIN pessoa p ON p.cd_pessoa = w.cd_pessoa
                ORDER BY p.nm_pessoa
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> UsuarioListItemDTO.builder()
                .cdWebUser(rs.getInt("cd_web_user"))
                .cdPessoa(rs.getInt("cd_pessoa"))
                .nmPessoa(rs.getString("nm_pessoa"))
                .nrCpf(rs.getString("nr_cpf"))
                .nmEmail(rs.getString("nm_email"))
                .tpResponsabilidade(rs.getInt("tp_responsabilidade"))
                .build());
    }

    @Override
    public UsuarioDetailDTO findById(Integer cdWebUser) throws Exception {
        String sql = """
                SELECT w.cd_web_user, p.cd_pessoa, e.cd_endereco,
                       p.nm_pessoa, p.nr_cpf, p.nm_email, p.nr_telefone,
                       w.tp_responsabilidade,
                       e.nm_logradouro, e.ds_endereco, e.nr_endereco,
                       e.nr_cep, e.bairro, e.cidade, e.estado
                FROM web_user w
                JOIN pessoa p ON p.cd_pessoa = w.cd_pessoa
                JOIN endereco e ON e.cd_endereco = p.cd_endereco
                WHERE w.cd_web_user = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> UsuarioDetailDTO.builder()
                .cdWebUser(rs.getInt("cd_web_user"))
                .cdPessoa(rs.getInt("cd_pessoa"))
                .cdEndereco(rs.getInt("cd_endereco"))
                .nmPessoa(rs.getString("nm_pessoa"))
                .nrCpf(rs.getString("nr_cpf"))
                .nmEmail(rs.getString("nm_email"))
                .nrTelefone(rs.getString("nr_telefone"))
                .tpResponsabilidade(rs.getInt("tp_responsabilidade"))
                .nmLogradouro(rs.getString("nm_logradouro"))
                .dsEndereco(rs.getString("ds_endereco"))
                .nrEndereco(rs.getString("nr_endereco"))
                .nrCep(rs.getString("nr_cep"))
                .bairro(rs.getString("bairro"))
                .cidade(rs.getString("cidade"))
                .estado(rs.getString("estado"))
                .build(), cdWebUser)
                .stream().findFirst().orElse(null);
    }
}
