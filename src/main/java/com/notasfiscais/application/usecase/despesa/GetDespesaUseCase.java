package com.notasfiscais.application.usecase.despesa;
import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.queries.IDespesaQuery;
import org.springframework.stereotype.Service;
@Service
public class GetDespesaUseCase {
    private final IDespesaQuery despesaQuery;
    public GetDespesaUseCase(IDespesaQuery despesaQuery) {
        this.despesaQuery = despesaQuery;
    }
    public DespesaDetailDTO execute(Integer cdDespesa) throws Exception {
        try {
            DespesaDetailDTO dto = despesaQuery.getDespesa(cdDespesa);
            if (dto == null) throw new RecursoNaoEncontradoException("Despesa não encontrada");
            return dto;
        } catch (RecursoNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new DespesaException("Erro ao buscar despesa: " + e.getMessage());
        }
    }
}
