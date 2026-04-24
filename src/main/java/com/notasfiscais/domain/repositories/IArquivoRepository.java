package com.notasfiscais.domain.repositories;
import com.notasfiscais.domain.model.Arquivo;
public interface IArquivoRepository {
    Arquivo save(Arquivo arquivo) throws Exception;
    Arquivo get(Integer cdArquivo) throws Exception;
    void delete(Integer cdArquivo) throws Exception;
}
