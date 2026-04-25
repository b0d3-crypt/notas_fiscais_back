package com.notasfiscais.usecase.despesa;

import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.application.queries.IDespesaQuery;
import com.notasfiscais.application.usecase.despesa.CreateDespesaUseCase;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDespesaUseCase - Testes Unitários")
class CreateDespesaUseCaseTest {

    @Mock
    private IDespesaRepository despesaRepository;
    @Mock
    private IPessoaRepository pessoaRepository;
    @Mock
    private IArquivoService arquivoService;
    @Mock
    private IDespesaQuery despesaQuery;
    @Mock
    private MultipartFile arquivo;

    @InjectMocks
    private CreateDespesaUseCase useCase;

    private Pessoa pessoa;
    private Arquivo arquivoSalvo;
    private DescricaoDespesa despesaSalva;
    private DespesaDetailDTO detailDTO;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa(1, null, "João Silva", "11999990000", "12345678901", "joao@email.com");
        arquivoSalvo = new Arquivo(1, "nota.pdf", LocalDateTime.now(), 1, "2024/04/uuid_nota.pdf");
        despesaSalva = new DescricaoDespesa(1, 1, 1, "Compra de material", LocalDate.now(), new BigDecimal("150.00"));
        detailDTO = DespesaDetailDTO.builder()
                .cdDescricaoDespesa(1L)
                .nmPessoa("João Silva")
                .vlDespesa(new BigDecimal("150.00"))
                .build();
    }

    @Test
    @DisplayName("Deve criar despesa com sucesso quando dados são válidos")
    void deveCriarDespesaComSucesso() throws Exception {
        when(pessoaRepository.get(1)).thenReturn(pessoa);
        when(arquivoService.salvar(arquivo)).thenReturn(arquivoSalvo);
        when(despesaRepository.save(any(DescricaoDespesa.class))).thenReturn(despesaSalva);
        when(despesaQuery.getDespesa(1)).thenReturn(detailDTO);

        DespesaDetailDTO resultado = useCase.execute(arquivo, LocalDate.now(), new BigDecimal("150.00"), "Compra de material", 1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCdDescricaoDespesa()).isEqualTo(1L);
        assertThat(resultado.getNmPessoa()).isEqualTo("João Silva");

        verify(pessoaRepository).get(1);
        verify(arquivoService).salvar(arquivo);
        verify(despesaRepository).save(any(DescricaoDespesa.class));
        verify(despesaQuery).getDespesa(1);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando pessoa não existe")
    void deveLancarValidacaoExceptionQuandoPessoaNaoExiste() throws Exception {
        when(pessoaRepository.get(99)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(arquivo, LocalDate.now(), new BigDecimal("100.00"), "Teste", 99))
                .isInstanceOf(ValidacaoException.class);

        verify(arquivoService, never()).salvar(any());
        verify(despesaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DespesaException quando arquivoService falha")
    void deveLancarDespesaExceptionQuandoArquivoServiceFalha() throws Exception {
        when(pessoaRepository.get(1)).thenReturn(pessoa);
        when(arquivoService.salvar(any())).thenThrow(new RuntimeException("Erro de I/O"));

        assertThatThrownBy(() -> useCase.execute(arquivo, LocalDate.now(), new BigDecimal("100.00"), "Teste", 1))
                .isInstanceOf(DespesaException.class)
                .hasMessageContaining("Erro ao criar despesa");
    }
}

