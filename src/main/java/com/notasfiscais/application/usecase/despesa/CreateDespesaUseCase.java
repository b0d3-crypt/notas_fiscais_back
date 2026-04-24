package com.notasfiscais.application.usecase.despesa;
import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.application.queries.IDespesaQuery;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
@Service
@Transactional(rollbackFor = Exception.class)
public class CreateDespesaUseCase {
    private final IDespesaRepository despesaRepository;
    private final IPessoaRepository pessoaRepository;
    private final IArquivoService arquivoService;
    private final IDespesaQuery despesaQuery;
    public CreateDespesaUseCase(IDespesaRepository despesaRepository,
                                IPessoaRepository pessoaRepository,
                                IArquivoService arquivoService,
                                IDespesaQuery despesaQuery) {
        this.despesaRepository = despesaRepository;
        this.pessoaRepository = pessoaRepository;
        this.arquivoService = arquivoService;
        this.despesaQuery = despesaQuery;
    }
    public DespesaDetailDTO execute(MultipartFile arquivo, LocalDate dtDespesa,
                                    BigDecimal vlDespesa, String dsDespesa,
                                    Integer cdPessoa) throws Exception {
        try {
            validarPessoa(cdPessoa);
            Arquivo arqSalvo = arquivoService.salvar(arquivo);
            DescricaoDespesa despesa = new DescricaoDespesa(
                    null, arqSalvo.getCdArquivo(), cdPessoa, dsDespesa, dtDespesa, vlDespesa);
            despesa = despesaRepository.save(despesa);
            return despesaQuery.getDespesa(despesa.getCdDescricaoDespesa());
        } catch (ValidacaoException e) {
            throw e;
        } catch (Exception e) {
            throw new DespesaException("Erro ao criar despesa: " + e.getMessage());
        }
    }
    private void validarPessoa(Integer cdPessoa) throws Exception {
        if (pessoaRepository.get(cdPessoa) == null) {
            throw new ValidacaoException("Pessoa não encontrada");
        }
    }
}
