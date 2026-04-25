package com.notasfiscais.usecase.despesa;

import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.application.queries.IDespesaQuery;
import com.notasfiscais.application.usecase.despesa.UpdateDespesaUseCase;
import com.notasfiscais.domain.enums.TpResponsabilidadeEnum;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateDespesaUseCase - Testes Unitários")
class UpdateDespesaUseCaseTest {

    @Mock private IDespesaRepository despesaRepository;
    @Mock private IArquivoService arquivoService;
    @Mock private IDespesaQuery despesaQuery;

    @InjectMocks
    private UpdateDespesaUseCase useCase;

    private DescricaoDespesa despesaExistente;
    private DespesaDetailDTO detailDTO;

    @BeforeEach
    void setUp() {
        despesaExistente = new DescricaoDespesa(1, 10, 5, "Descrição original",
                LocalDate.of(2024, 1, 15), new BigDecimal("300.00"));
        detailDTO = DespesaDetailDTO.builder()
                .cdDescricaoDespesa(1L)
                .vlDespesa(new BigDecimal("500.00"))
                .dsDespesa("Nova descrição")
                .build();
    }

    @Test
    @DisplayName("Deve atualizar despesa quando o usuário é o dono")
    void deveAtualizarDespesaQuandoEhDono() throws Exception {
        when(despesaRepository.get(1)).thenReturn(despesaExistente);
        when(despesaRepository.save(any())).thenReturn(despesaExistente);
        when(despesaQuery.getDespesa(1)).thenReturn(detailDTO);

        DespesaDetailDTO resultado = useCase.execute(1, null, LocalDate.now(),
                new BigDecimal("500.00"), "Nova descrição", 5, TpResponsabilidadeEnum.USER.getCodigo());

        assertThat(resultado).isNotNull();
        verify(despesaRepository).save(any(DescricaoDespesa.class));
        verify(arquivoService, never()).substituir(any(), any());
    }

    @Test
    @DisplayName("Deve atualizar arquivo quando novoArquivo é fornecido")
    void deveSubstituirArquivoQuandoFornecido() throws Exception {
        MultipartFile novoFile = mock(MultipartFile.class);
        when(novoFile.isEmpty()).thenReturn(false);
        when(despesaRepository.get(1)).thenReturn(despesaExistente);
        when(despesaRepository.save(any())).thenReturn(despesaExistente);
        when(despesaQuery.getDespesa(1)).thenReturn(detailDTO);

        useCase.execute(1, novoFile, null, null, null, 5, TpResponsabilidadeEnum.USER.getCodigo());

        verify(arquivoService).substituir(10, novoFile);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando despesa não existe")
    void deveLancarExcecaoQuandoDespesaNaoExiste() throws Exception {
        when(despesaRepository.get(99)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(99, null, null, null, null, 5, TpResponsabilidadeEnum.USER.getCodigo()))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve lançar AcessoNegadoException quando usuário não é dono nem admin")
    void deveLancarAcessoNegadoQuandoNaoEhDonoNemAdmin() throws Exception {
        when(despesaRepository.get(1)).thenReturn(despesaExistente);

        assertThatThrownBy(() -> useCase.execute(1, null, null, null, null, 99, TpResponsabilidadeEnum.USER.getCodigo()))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    @DisplayName("Admin deve conseguir atualizar despesa de outro usuário")
    void adminDeveAtualizarDespesaDeOutroUsuario() throws Exception {
        when(despesaRepository.get(1)).thenReturn(despesaExistente);
        when(despesaRepository.save(any())).thenReturn(despesaExistente);
        when(despesaQuery.getDespesa(1)).thenReturn(detailDTO);

        assertThatCode(() -> useCase.execute(1, null, null, null, null, 99, TpResponsabilidadeEnum.ADMIN.getCodigo()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve lançar DespesaException para erros inesperados")
    void deveLancarDespesaExceptionParaErroInesperado() throws Exception {
        when(despesaRepository.get(1)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> useCase.execute(1, null, null, null, null, 5, TpResponsabilidadeEnum.USER.getCodigo()))
                .isInstanceOf(DespesaException.class);
    }
}

