package com.notasfiscais.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UsuarioListItemDTO;
import com.notasfiscais.application.queries.IUsuarioQuery;
import com.notasfiscais.application.usecase.webuser.FindUsuariosUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUsuariosUseCase - Testes Unitários")
class FindUsuariosUseCaseTest {

    @Mock private IUsuarioQuery usuarioQuery;

    @InjectMocks
    private FindUsuariosUseCase useCase;

    @Test
    @DisplayName("Deve retornar lista de usuários quando há registros")
    void deveRetornarListaDeUsuarios() throws Exception {
        List<UsuarioListItemDTO> lista = List.of(
                UsuarioListItemDTO.builder().cdWebUser(1).nmPessoa("João Silva").nmEmail("joao@email.com").tpResponsabilidade(1).build(),
                UsuarioListItemDTO.builder().cdWebUser(2).nmPessoa("Maria Souza").nmEmail("maria@email.com").tpResponsabilidade(0).build()
        );
        when(usuarioQuery.findAll()).thenReturn(lista);

        List<UsuarioListItemDTO> resultado = useCase.execute();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNmPessoa()).isEqualTo("João Silva");
        assertThat(resultado.get(1).getTpResponsabilidade()).isEqualTo(0);
        verify(usuarioQuery).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há registros")
    void deveRetornarListaVaziaQuandoNaoHaRegistros() throws Exception {
        when(usuarioQuery.findAll()).thenReturn(List.of());

        List<UsuarioListItemDTO> resultado = useCase.execute();

        assertThat(resultado).isEmpty();
        verify(usuarioQuery).findAll();
    }
}
