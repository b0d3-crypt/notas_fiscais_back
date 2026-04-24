package com.notasfiscais.application.usecase.despesa;
import com.notasfiscais.application.dto.despesa.DespesaListItemDTO;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.queries.IDespesaQuery;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class FindDespesaUseCase {
    private final IDespesaQuery despesaQuery;
    public FindDespesaUseCase(IDespesaQuery despesaQuery) {
        this.despesaQuery = despesaQuery;
    }
    public List<DespesaListItemDTO> execute(Integer ano, Integer mes) throws Exception {
        try {
            return despesaQuery.findDespesas(ano, mes);
        } catch (Exception e) {
            throw new DespesaException("Erro ao buscar despesas: " + e.getMessage());
        }
    }
}
