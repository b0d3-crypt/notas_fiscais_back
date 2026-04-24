package com.notasfiscais.application.queries;
import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.dto.despesa.DespesaListItemDTO;
import java.util.List;
public interface IDespesaQuery {
    List<DespesaListItemDTO> findDespesas(Integer ano, Integer mes) throws Exception;
    DespesaDetailDTO getDespesa(Integer cdDespesa) throws Exception;
}
