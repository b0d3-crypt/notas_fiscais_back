package com.notasfiscais.service;

import com.notasfiscais.auth.JwtAuthDetails;
import com.notasfiscais.dto.DespesaDetailDTO;
import com.notasfiscais.dto.DespesaListItemDTO;
import com.notasfiscais.entity.Arquivo;
import com.notasfiscais.entity.DescricaoDespesa;
import com.notasfiscais.entity.Pessoa;
import com.notasfiscais.enums.TpResponsabilidade;
import com.notasfiscais.exception.GlobalExceptionHandler.AcessoNegadoException;
import com.notasfiscais.exception.GlobalExceptionHandler.RecursoNaoEncontradoException;
import com.notasfiscais.repository.DespesaRepository;
import com.notasfiscais.repository.PessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class DespesaService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DespesaRepository despesaRepository;
    private final PessoaRepository pessoaRepository;
    private final ArquivoService arquivoService;

    public DespesaService(DespesaRepository despesaRepository,
                          PessoaRepository pessoaRepository,
                          ArquivoService arquivoService) {
        this.despesaRepository = despesaRepository;
        this.pessoaRepository = pessoaRepository;
        this.arquivoService = arquivoService;
    }

    public List<DespesaListItemDTO> listar(Integer ano, Integer mes) {
        return despesaRepository.findAllWithFilters(ano, mes)
                .stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
    }

    public DespesaDetailDTO buscar(Integer id) {
        return toDetail(findOrThrow(id));
    }

    public DespesaDetailDTO criar(MultipartFile arquivo, String dtDespesaStr,
                                  BigDecimal vlDespesa, String dsDespesa,
                                  JwtAuthDetails authDetails) {
        Pessoa pessoa = pessoaRepository.findById(authDetails.getCdPessoa().intValue())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));

        Arquivo arqSalvo = arquivoService.salvar(arquivo);
        LocalDate dtDespesa = LocalDate.parse(dtDespesaStr, DATE_FMT);

        DescricaoDespesa despesa = DescricaoDespesa.builder()
                .arquivo(arqSalvo)
                .pessoa(pessoa)
                .dtDespesa(dtDespesa)
                .vlDespesa(vlDespesa)
                .dsDespesa(dsDespesa)
                .build();

        return toDetail(despesaRepository.save(despesa));
    }

    public DespesaDetailDTO atualizar(Integer id, MultipartFile novoArquivo,
                                      String dtDespesaStr, BigDecimal vlDespesa,
                                      String dsDespesa, JwtAuthDetails authDetails) {
        DescricaoDespesa despesa = findOrThrow(id);
        checkOwnerOrAdmin(despesa, authDetails);

        if (novoArquivo != null && !novoArquivo.isEmpty()) {
            arquivoService.substituir(despesa.getArquivo(), novoArquivo);
        }
        if (dtDespesaStr != null && !dtDespesaStr.isBlank()) {
            despesa.setDtDespesa(LocalDate.parse(dtDespesaStr, DATE_FMT));
        }
        if (vlDespesa != null) {
            despesa.setVlDespesa(vlDespesa);
        }
        if (dsDespesa != null) {
            despesa.setDsDespesa(dsDespesa);
        }

        return toDetail(despesaRepository.save(despesa));
    }

    public void deletar(Integer id, JwtAuthDetails authDetails) {
        DescricaoDespesa despesa = findOrThrow(id);
        checkOwnerOrAdmin(despesa, authDetails);

        Arquivo arquivo = despesa.getArquivo();
        despesaRepository.delete(despesa);
        arquivoService.deletar(arquivo);
    }

    public DescricaoDespesa findOrThrow(Integer id) {
        return despesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Despesa não encontrada"));
    }

    private void checkOwnerOrAdmin(DescricaoDespesa despesa, JwtAuthDetails authDetails) {
        boolean isOwner = despesa.getPessoa().getCdPessoa().longValue() == authDetails.getCdPessoa();
        boolean isAdmin = authDetails.getRole() != null
                && authDetails.getRole() == TpResponsabilidade.ADMIN.getCodigo();
        if (!isOwner && !isAdmin) {
            throw new AcessoNegadoException("Sem permissão para esta operação");
        }
    }

    private DespesaListItemDTO toListItem(DescricaoDespesa d) {
        return DespesaListItemDTO.builder()
                .cdDescricaoDespesa(d.getCdDescricaoDespesa().longValue())
                .nmPessoa(d.getPessoa().getNmPessoa())
                .cdPessoa(d.getPessoa().getCdPessoa().longValue())
                .nmArquivo(d.getArquivo().getNmArquivo())
                .tpArquivo(d.getArquivo().getTpArquivo())
                .dtDespesa(d.getDtDespesa().format(DATE_FMT))
                .vlDespesa(d.getVlDespesa())
                .cdArquivo(d.getArquivo().getCdArquivo().longValue())
                .build();
    }

    private DespesaDetailDTO toDetail(DescricaoDespesa d) {
        return DespesaDetailDTO.builder()
                .cdDescricaoDespesa(d.getCdDescricaoDespesa().longValue())
                .nmPessoa(d.getPessoa().getNmPessoa())
                .cdPessoa(d.getPessoa().getCdPessoa().longValue())
                .nmArquivo(d.getArquivo().getNmArquivo())
                .tpArquivo(d.getArquivo().getTpArquivo())
                .dtDespesa(d.getDtDespesa().format(DATE_FMT))
                .vlDespesa(d.getVlDespesa())
                .cdArquivo(d.getArquivo().getCdArquivo().longValue())
                .dsDespesa(d.getDsDespesa())
                .dtArquivo(d.getArquivo().getDtArquivo().format(DATE_TIME_FMT))
                .build();
    }
}
