package com.notasfiscais.application.usecase.despesa;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
import org.springframework.stereotype.Service;
@Service
public class DownloadDespesaUseCase {
    private final IDespesaRepository despesaRepository;
    private final IArquivoService arquivoService;
    public DownloadDespesaUseCase(IDespesaRepository despesaRepository, IArquivoService arquivoService) {
        this.despesaRepository = despesaRepository;
        this.arquivoService = arquivoService;
    }
    public DownloadResult execute(Integer cdDespesa) throws Exception {
        try {
            DescricaoDespesa despesa = despesaRepository.get(cdDespesa);
            if (despesa == null) throw new RecursoNaoEncontradoException("Despesa não encontrada");
            byte[] bytes = arquivoService.lerBytes(despesa.getCdArquivo());
            return new DownloadResult(bytes, despesa.getCdArquivo());
        } catch (RecursoNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new DespesaException("Erro ao fazer download: " + e.getMessage());
        }
    }
    public record DownloadResult(byte[] bytes, Integer cdArquivo) {}
}
