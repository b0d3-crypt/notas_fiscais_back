package com.notasfiscais.domain.repositories;
import com.notasfiscais.domain.model.Pessoa;
public interface IPessoaRepository {
    Pessoa save(Pessoa pessoa) throws Exception;
    Pessoa get(Integer cdPessoa) throws Exception;
    Pessoa getByEmail(String nmEmail) throws Exception;
}
