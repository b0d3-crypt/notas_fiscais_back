package com.notasfiscais.inmemory;

import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.dto.despesa.DespesaListItemDTO;
import com.notasfiscais.application.queries.IDespesaQuery;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.model.Pessoa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory implementation of IDespesaQuery that cross-references
 * the in-memory despesa, arquivo and pessoa repositories.
 */
public class InMemoryDespesaQuery implements IDespesaQuery {

    private final InMemoryDespesaRepository despesaRepo;
    private final InMemoryArquivoRepository arquivoRepo;
    private final InMemoryPessoaRepository pessoaRepo;

    public InMemoryDespesaQuery(InMemoryDespesaRepository despesaRepo,
                                InMemoryArquivoRepository arquivoRepo,
                                InMemoryPessoaRepository pessoaRepo) {
        this.despesaRepo = despesaRepo;
        this.arquivoRepo = arquivoRepo;
        this.pessoaRepo = pessoaRepo;
    }

    @Override
    public List<DespesaListItemDTO> findDespesas(Integer ano, Integer mes) {
        return despesaRepo.getAll().stream()
                .filter(d -> (ano == null || d.getDtDespesa().getYear() == ano)
                        && (mes == null || d.getDtDespesa().getMonthValue() == mes))
                .map(this::toListItem)
                .collect(Collectors.toList());
    }

    @Override
    public DespesaDetailDTO getDespesa(Integer cdDespesa) throws Exception {
        DescricaoDespesa d = despesaRepo.get(cdDespesa);
        if (d == null) return null;
        Arquivo arq = arquivoRepo.get(d.getCdArquivo());
        Pessoa p = pessoaRepo.get(d.getCdPessoa());
        return DespesaDetailDTO.builder()
                .cdDescricaoDespesa(d.getCdDescricaoDespesa().longValue())
                .cdPessoa(d.getCdPessoa().longValue())
                .nmPessoa(p != null ? p.getNmPessoa() : null)
                .cdArquivo(arq != null ? arq.getCdArquivo().longValue() : null)
                .nmArquivo(arq != null ? arq.getNmArquivo() : null)
                .tpArquivo(arq != null ? arq.getTpArquivo() : null)
                .dtDespesa(d.getDtDespesa() != null ? d.getDtDespesa().toString() : null)
                .vlDespesa(d.getVlDespesa())
                .dsDespesa(d.getDsDespesa())
                .dtArquivo(arq != null && arq.getDtArquivo() != null ? arq.getDtArquivo().toString() : null)
                .build();
    }

    private DespesaListItemDTO toListItem(DescricaoDespesa d) {
        Arquivo arq = null;
        try { arq = arquivoRepo.get(d.getCdArquivo()); } catch (Exception ignored) {}
        Pessoa p = null;
        try { p = pessoaRepo.get(d.getCdPessoa()); } catch (Exception ignored) {}
        return DespesaListItemDTO.builder()
                .cdDescricaoDespesa(d.getCdDescricaoDespesa().longValue())
                .cdPessoa(d.getCdPessoa().longValue())
                .nmPessoa(p != null ? p.getNmPessoa() : null)
                .cdArquivo(arq != null ? arq.getCdArquivo().longValue() : null)
                .nmArquivo(arq != null ? arq.getNmArquivo() : null)
                .tpArquivo(arq != null ? arq.getTpArquivo() : null)
                .dtDespesa(d.getDtDespesa() != null ? d.getDtDespesa().toString() : null)
                .vlDespesa(d.getVlDespesa())
                .build();
    }
}

