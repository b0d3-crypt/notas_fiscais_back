package com.notasfiscais.application.dto.despesa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DespesaListItemDTO {
    private Long cdDescricaoDespesa;
    private String nmPessoa;
    private Long cdPessoa;
    private String nmArquivo;
    private Integer tpArquivo;
    private String dtDespesa;
    private BigDecimal vlDespesa;
    private Long cdArquivo;
}
