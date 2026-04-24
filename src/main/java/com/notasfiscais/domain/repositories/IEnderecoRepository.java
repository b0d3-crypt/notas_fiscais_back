package com.notasfiscais.domain.repositories;
import com.notasfiscais.domain.model.Endereco;
public interface IEnderecoRepository {
    Endereco save(Endereco endereco) throws Exception;
    Endereco get(Integer cdEndereco) throws Exception;
}
