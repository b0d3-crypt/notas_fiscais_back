package com.notasfiscais.integration;

import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.usecase.despesa.CreateDespesaUseCase;
import com.notasfiscais.application.usecase.despesa.DeleteDespesaUseCase;
import com.notasfiscais.application.usecase.despesa.FindDespesaUseCase;
import com.notasfiscais.application.usecase.despesa.GetDespesaUseCase;
import com.notasfiscais.application.usecase.despesa.UpdateDespesaUseCase;
import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.domain.enums.TpResponsabilidadeEnum;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.inmemory.InMemoryArquivoRepository;
import com.notasfiscais.inmemory.InMemoryArquivoService;
import com.notasfiscais.inmemory.InMemoryDespesaQuery;
import com.notasfiscais.inmemory.InMemoryDespesaRepository;
import com.notasfiscais.inmemory.InMemoryPessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Despesa - Testes de Integração (In-Memory)")
class DespesaIntegrationTest {

    private InMemoryDespesaRepository despesaRepository;
    private InMemoryArquivoRepository arquivoRepository;
    private InMemoryPessoaRepository pessoaRepository;
    private InMemoryArquivoService arquivoService;
    private InMemoryDespesaQuery despesaQuery;

    private CreateDespesaUseCase createUseCase;
    private DeleteDespesaUseCase deleteUseCase;
    private FindDespesaUseCase findUseCase;
    private GetDespesaUseCase getUseCase;
    private UpdateDespesaUseCase updateUseCase;

    private Pessoa pessoa;
    private MockMultipartFile pdfFile;

    @BeforeEach
    void setUp() {
        despesaRepository = new InMemoryDespesaRepository();
        arquivoRepository = new InMemoryArquivoRepository();
        pessoaRepository = new InMemoryPessoaRepository();
        arquivoService = new InMemoryArquivoService(arquivoRepository);
        despesaQuery = new InMemoryDespesaQuery(despesaRepository, arquivoRepository, pessoaRepository);

        createUseCase = new CreateDespesaUseCase(despesaRepository, pessoaRepository, arquivoService, despesaQuery);
        deleteUseCase = new DeleteDespesaUseCase(despesaRepository, arquivoService);
        findUseCase = new FindDespesaUseCase(despesaQuery);
        getUseCase = new GetDespesaUseCase(despesaQuery);
        updateUseCase = new UpdateDespesaUseCase(despesaRepository, arquivoService, despesaQuery);

        pessoa = pessoaRepository.save(
                new Pessoa(null, 1, "Maria Souza", "11988887777", "987.654.321-00", "maria@email.com"));

        pdfFile = new MockMultipartFile("file", "nota.pdf", "application/pdf", "conteudo".getBytes());
    }

    @Test
    @DisplayName("Deve criar e recuperar despesa com sucesso")
    void deveCriarERecuperarDespesa() throws Exception {
        DespesaDetailDTO criada = createUseCase.execute(
                pdfFile, LocalDate.of(2024, 3, 10), new BigDecimal("450.00"), "Conta de luz", pessoa.getCdPessoa());

        assertThat(criada).isNotNull();
        assertThat(criada.getDsDespesa()).isEqualTo("Conta de luz");
        assertThat(criada.getVlDespesa()).isEqualByComparingTo("450.00");
        assertThat(criada.getNmPessoa()).isEqualTo("Maria Souza");

        DespesaDetailDTO recuperada = getUseCase.execute(criada.getCdDescricaoDespesa().intValue());
        assertThat(recuperada.getCdDescricaoDespesa()).isEqualTo(criada.getCdDescricaoDespesa());
    }

    @Test
    @DisplayName("Deve listar despesas por ano e mês")
    void deveListarDespesasPorAnoEMes() throws Exception {
        createUseCase.execute(pdfFile, LocalDate.of(2024, 3, 10), new BigDecimal("100.00"), "Despesa março", pessoa.getCdPessoa());
        createUseCase.execute(
                new MockMultipartFile("f2", "n2.pdf", "application/pdf", "x".getBytes()),
                LocalDate.of(2024, 4, 5), new BigDecimal("200.00"), "Despesa abril", pessoa.getCdPessoa());

        var listaMarch = findUseCase.execute(2024, 3);
        var listaApril = findUseCase.execute(2024, 4);

        assertThat(listaMarch).hasSize(1);
        assertThat(listaApril).hasSize(1);
    }

    @Test
    @DisplayName("Deve deletar despesa e remover arquivo associado")
    void deveDeletarDespesaEArquivo() throws Exception {
        DespesaDetailDTO criada = createUseCase.execute(
                pdfFile, LocalDate.now(), new BigDecimal("300.00"), "Despesa deletável", pessoa.getCdPessoa());

        int cdDespesa = criada.getCdDescricaoDespesa().intValue();
        int cdArquivo = criada.getCdArquivo().intValue();

        deleteUseCase.execute(cdDespesa, pessoa.getCdPessoa(), TpResponsabilidadeEnum.USER.getCodigo());

        assertThat(despesaRepository.get(cdDespesa)).isNull();
        assertThat(arquivoRepository.get(cdArquivo)).isNull();
    }

    @Test
    @DisplayName("Deve lançar AcessoNegadoException ao tentar deletar despesa de outro usuário")
    void deveLancarAcessoNegadoAoDeletarDespesaDeOutro() throws Exception {
        DespesaDetailDTO criada = createUseCase.execute(
                pdfFile, LocalDate.now(), new BigDecimal("150.00"), "Despesa de Maria", pessoa.getCdPessoa());

        int cdDespesa = criada.getCdDescricaoDespesa().intValue();
        int outroCdPessoa = 999;

        assertThatThrownBy(() -> deleteUseCase.execute(cdDespesa, outroCdPessoa, TpResponsabilidadeEnum.USER.getCodigo()))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar despesa para pessoa inexistente")
    void deveLancarExcecaoParaPessoaInexistente() {
        assertThatThrownBy(() -> createUseCase.execute(
                pdfFile, LocalDate.now(), new BigDecimal("100.00"), "Teste", 9999))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao buscar despesa inexistente")
    void deveLancarExcecaoAoBuscarDespesaInexistente() {
        assertThatThrownBy(() -> getUseCase.execute(9999))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Admin deve conseguir deletar despesa de outro usuário")
    void adminDeveDeletarDespesaDeOutro() throws Exception {
        DespesaDetailDTO criada = createUseCase.execute(
                pdfFile, LocalDate.now(), new BigDecimal("250.00"), "Despesa para admin deletar", pessoa.getCdPessoa());

        int cdDespesa = criada.getCdDescricaoDespesa().intValue();

        assertThatCode(() -> deleteUseCase.execute(cdDespesa, 999, TpResponsabilidadeEnum.ADMIN.getCodigo()))
                .doesNotThrowAnyException();

        assertThat(despesaRepository.get(cdDespesa)).isNull();
    }

    @Test
    @DisplayName("Deve atualizar dados da despesa")
    void deveAtualizarDespesa() throws Exception {
        DespesaDetailDTO criada = createUseCase.execute(
                pdfFile, LocalDate.of(2024, 1, 10), new BigDecimal("100.00"), "Descrição original", pessoa.getCdPessoa());

        int cdDespesa = criada.getCdDescricaoDespesa().intValue();

        DespesaDetailDTO atualizada = updateUseCase.execute(
                cdDespesa, null, LocalDate.of(2024, 2, 20), new BigDecimal("999.00"),
                "Descrição nova", pessoa.getCdPessoa(), TpResponsabilidadeEnum.USER.getCodigo());

        assertThat(atualizada.getDsDespesa()).isEqualTo("Descrição nova");
        assertThat(atualizada.getVlDespesa()).isEqualByComparingTo("999.00");
    }
}

