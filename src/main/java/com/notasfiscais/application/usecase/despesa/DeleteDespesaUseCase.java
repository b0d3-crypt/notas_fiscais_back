package com.notasfiscais.application.usecase.despesa;
import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.domain.enums.TpResponsabilidadeEnum;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(rollbackFor = Exception.class)
public class DeleteDespesaUseCase {
    private final IDespesaRepository despesaRepository;
    private final IArquivoService arquivoService;
    public DeleteDespesaUseCase(IDespesaRepository despesaRepository, IArquivoService arquivoService) {
        this.despesaRepository = despesaRepository;
        this.arquivoService = arquivoService;
    }
    public void execute(Integer cdDespesa, Integer cdPessoa, Integer role) throws Exception {
        try {
            DescricaoDespesa despesa = despesaRepository.get(cdDespesa);
            if (despesa == null) throw new RecursoNaoEncontradoException("Despesa não encontrada");
            boolean isOwner = despesa.getCdPessoa().equals(cdPessoa);
            boolean isAdmin = role != null && role == TpResponsabilidadeEnum.ADMIN.getCodigo();
            if (!isOwner && !isAdmin) throw new AcessoNegadoException("Sem permissão para esta operação");
            Integer cdArquivo = despesa.getCdArquivo();
            despesaRepository.delete(cdDespesa);
            arquivoService.deletar(cdArquivo);
        } catch (RecursoNaoEncontradoException | AcessoNegadoException e) {
            throw e;
        } catch (Exception e) {
            throw new DespesaException("Erro ao deletar despesa: " + e.getMessage());
        }
    }
}
