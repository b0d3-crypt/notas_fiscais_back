package com.notasfiscais.domain.repositories;
import com.notasfiscais.domain.model.DescricaoDespesa;
public interface IDespesaRepository {
    DescricaoDespesa save(DescricaoDespesa despesa) throws Exception;
    DescricaoDespesa get(Integer cdDespesa) throws Exception;
    void delete(Integer cdDespesa) throws Exception;
}
