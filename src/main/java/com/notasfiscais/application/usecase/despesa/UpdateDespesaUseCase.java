package com.notasfiscais.application.usecase.despesa;
import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.application.queries.IDespesaQuery;
import com.notasfiscais.domain.enums.TpResponsabilidadeEnum;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
@Service
@Transactional(rollbackFor = Exception.class)
public class UpdateDespesaUseCase {
    private final IDespesaRepository despesaRepository;
    private final IArquivoService arquivoService;
    private final IDespesaQuery despesaQuery;
    public UpdateDespesaUseCase(IDespesaRepository despesaRepository,
                                IArquivoService arquivoService,
                                IDespesaQuery despesaQuery) {
        this.despesaRepository = despesaRepository;
        this.arquivoService = arquivoService;
        this.despesaQuery = despesaQuery;
    }
    public DespesaDetailDTO execute(Integer cdDespesa, MultipartFile novoArquivo,
                                    LocalDate dtDespesa, BigDecimal vlDespesa,
                                    String dsDespesa, Integer cdPessoa, Integer role) throws Exception {
        try {
            DescricaoDespesa despesa = despesaRepository.get(cdDespesa);
            if (despesa == null) throw new RecursoNaoEncontradoException("Despesa não encontrada");
            checkOwnerOrAdmin(despesa, cdPessoa, role);
            Integer cdArquivo = despesa.getCdArquivo();
            if (novoArquivo != null && !novoArquivo.isEmpty()) {
                arquivoService.substituir(cdArquivo, novoArquivo);
            }
            LocalDate novaData = (dtDespesa != null) ? dtDespesa : despesa.getDtDespesa();
            BigDecimal novoValor = (vlDespesa != null) ? vlDespesa : despesa.getVlDespesa();
            String novaDescricao = (dsDespesa != null) ? dsDespesa : despesa.getDsDespesa();
            DescricaoDespesa atualizada = new DescricaoDespesa(
                    cdDespesa, cdArquivo, despesa.getCdPessoa(), novaDescricao, novaData, novoValor);
            despesaRepository.save(atualizada);
            return despesaQuery.getDespesa(cdDespesa);
        } catch (RecursoNaoEncontradoException | AcessoNegadoException e) {
            throw e;
        } catch (Exception e) {
            throw new DespesaException("Erro ao atualizar despesa: " + e.getMessage());
        }
    }
    private void checkOwnerOrAdmin(DescricaoDespesa despesa, Integer cdPessoa, Integer role) {
        boolean isOwner = despesa.getCdPessoa().equals(cdPessoa);
        boolean isAdmin = role != null && role == TpResponsabilidadeEnum.ADMIN.getCodigo();
        if (!isOwner && !isAdmin) {
            throw new AcessoNegadoException("Sem permissão para esta operação");
        }
    }
}
