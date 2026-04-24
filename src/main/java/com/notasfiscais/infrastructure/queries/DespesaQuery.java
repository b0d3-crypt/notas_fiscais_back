package com.notasfiscais.infrastructure.queries;
import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.dto.despesa.DespesaListItemDTO;
import com.notasfiscais.application.queries.IDespesaQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
public class DespesaQuery implements IDespesaQuery {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final JdbcTemplate jdbcTemplate;
    public DespesaQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<DespesaListItemDTO> findDespesas(Integer ano, Integer mes) throws Exception {
        StringBuilder sql = new StringBuilder("""
                SELECT d.cd_descricao_despesa, p.nm_pessoa, p.cd_pessoa,
                       a.nm_arquivo, a.tp_arquivo, d.dt_despesa, d.vl_despesa, a.cd_arquivo
                FROM descricao_despesa d
                JOIN arquivo a ON a.cd_arquivo = d.cd_arquivo
                JOIN pessoa p ON p.cd_pessoa = d.cd_pessoa
                WHERE 1=1
                """);
        if (ano != null) sql.append(" AND EXTRACT(YEAR FROM d.dt_despesa) = ").append(ano);
        if (mes != null) sql.append(" AND EXTRACT(MONTH FROM d.dt_despesa) = ").append(mes);
        sql.append(" ORDER BY d.dt_despesa DESC");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> DespesaListItemDTO.builder()
                .cdDescricaoDespesa(rs.getLong("cd_descricao_despesa"))
                .nmPessoa(rs.getString("nm_pessoa"))
                .cdPessoa(rs.getLong("cd_pessoa"))
                .nmArquivo(rs.getString("nm_arquivo"))
                .tpArquivo(rs.getInt("tp_arquivo"))
                .dtDespesa(rs.getDate("dt_despesa").toLocalDate().format(DATE_FMT))
                .vlDespesa(rs.getBigDecimal("vl_despesa"))
                .cdArquivo(rs.getLong("cd_arquivo"))
                .build());
    }
    @Override
    public DespesaDetailDTO getDespesa(Integer cdDespesa) throws Exception {
        String sql = """
                SELECT d.cd_descricao_despesa, p.nm_pessoa, p.cd_pessoa,
                       a.nm_arquivo, a.tp_arquivo, d.dt_despesa, d.vl_despesa,
                       a.cd_arquivo, d.ds_despesa, a.dt_arquivo
                FROM descricao_despesa d
                JOIN arquivo a ON a.cd_arquivo = d.cd_arquivo
                JOIN pessoa p ON p.cd_pessoa = d.cd_pessoa
                WHERE d.cd_descricao_despesa = ?
                """;
        List<DespesaDetailDTO> result = jdbcTemplate.query(sql,
                ps -> ps.setInt(1, cdDespesa),
                (rs, rowNum) -> DespesaDetailDTO.builder()
                        .cdDescricaoDespesa(rs.getLong("cd_descricao_despesa"))
                        .nmPessoa(rs.getString("nm_pessoa"))
                        .cdPessoa(rs.getLong("cd_pessoa"))
                        .nmArquivo(rs.getString("nm_arquivo"))
                        .tpArquivo(rs.getInt("tp_arquivo"))
                        .dtDespesa(rs.getDate("dt_despesa").toLocalDate().format(DATE_FMT))
                        .vlDespesa(rs.getBigDecimal("vl_despesa"))
                        .cdArquivo(rs.getLong("cd_arquivo"))
                        .dsDespesa(rs.getString("ds_despesa"))
                        .dtArquivo(rs.getTimestamp("dt_arquivo").toLocalDateTime().format(DATETIME_FMT))
                        .build());
        return result.isEmpty() ? null : result.get(0);
    }
}
