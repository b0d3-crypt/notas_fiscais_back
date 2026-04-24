package com.notasfiscais.application.port.in;
import com.notasfiscais.domain.model.Arquivo;
import org.springframework.web.multipart.MultipartFile;
public interface IArquivoService {
    Arquivo salvar(MultipartFile file) throws Exception;
    Arquivo substituir(Integer cdArquivo, MultipartFile novoFile) throws Exception;
    byte[] lerBytes(Integer cdArquivo) throws Exception;
    void deletar(Integer cdArquivo) throws Exception;
}
