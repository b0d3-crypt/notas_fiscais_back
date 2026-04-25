package com.notasfiscais.usecase.despesa;

import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.application.usecase.despesa.DeleteDespesaUseCase;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteDespesaUseCase - Testes Unitários")
class DeleteDespesaUseCaseTest {

    @Mock
    private IDespesaRepository despesaRepository;
    @Mock
    private IArquivoService arquivoService;

    @InjectMocks
    private DeleteDespesaUseCase useCase;

    private DescricaoDespesa despesa;

    @BeforeEach
    void setUp() {
        despesa = new DescricaoDespesa(1, 10, 5, "Despesa teste", LocalDate.now(), new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Deve deletar despesa quando o usuário é dono")
    void deveDeletarDespesaQuandoEhDono() throws Exception {
        when(despesaRepository.get(1)).thenReturn(despesa);

        assertThatCode(() -> useCase.execute(1, 5, TpResponsabilidadeEnum.USER.getCodigo()))
                .doesNotThrowAnyException();

        verify(despesaRepository).delete(1);
        verify(arquivoService).deletar(10);
    }

    @Test
    @DisplayName("Deve deletar despesa quando o usuário é administrador")
    void deveDeletarDespesaQuandoEhAdmin() throws Exception {
        when(despesaRepository.get(1)).thenReturn(despesa);

        // cdPessoa = 99 (não é dono), mas é admin
        assertThatCode(() -> useCase.execute(1, 99, TpResponsabilidadeEnum.ADMIN.getCodigo()))
                .doesNotThrowAnyException();

        verify(despesaRepository).delete(1);
        verify(arquivoService).deletar(10);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando despesa não existe")
    void deveLancarExcecaoQuandoDespesaNaoExiste() throws Exception {
        when(despesaRepository.get(99)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(99, 5, TpResponsabilidadeEnum.USER.getCodigo()))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(despesaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar AcessoNegadoException quando usuário não é dono nem admin")
    void deveLancarAcessoNegadoQuandoNaoEhDonoNemAdmin() throws Exception {
        when(despesaRepository.get(1)).thenReturn(despesa);

        assertThatThrownBy(() -> useCase.execute(1, 99, TpResponsabilidadeEnum.USER.getCodigo()))
                .isInstanceOf(AcessoNegadoException.class);

        verify(despesaRepository, never()).delete(any());
    }
}

