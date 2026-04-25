package com.notasfiscais.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.queries.IUsuarioQuery;
import com.notasfiscais.application.usecase.webuser.GetUsuarioUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUsuarioUseCase - Testes Unitários")
class GetUsuarioUseCaseTest {

    @Mock private IUsuarioQuery usuarioQuery;

    @InjectMocks
    private GetUsuarioUseCase useCase;

    @Test
    @DisplayName("Deve retornar o usuário quando encontrado")
    void deveRetornarUsuarioQuandoEncontrado() throws Exception {
        UsuarioDetailDTO dto = UsuarioDetailDTO.builder()
                .cdWebUser(1)
                .cdPessoa(1)
                .nmPessoa("João Silva")
                .nmEmail("joao@email.com")
                .nrCpf("12345678901")
                .tpResponsabilidade(1)
                .build();
        when(usuarioQuery.findById(1)).thenReturn(dto);

        UsuarioDetailDTO resultado = useCase.execute(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCdWebUser()).isEqualTo(1);
        assertThat(resultado.getNmPessoa()).isEqualTo("João Silva");
        verify(usuarioQuery).findById(1);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() throws Exception {
        when(usuarioQuery.findById(99)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(99))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(usuarioQuery).findById(99);
    }
}
