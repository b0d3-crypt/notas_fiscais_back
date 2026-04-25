package com.notasfiscais.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UpdateSenhaRequest;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.usecase.webuser.UpdateSenhaUseCase;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateSenhaUseCase - Testes Unitários")
class UpdateSenhaUseCaseTest {

    @Mock private IWebUserRepository webUserRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateSenhaUseCase useCase;

    private UpdateSenhaRequest request;
    private WebUser existingWebUser;

    @BeforeEach
    void setUp() {
        request = new UpdateSenhaRequest();
        request.setCurrentPassword("senhaAtual123");
        request.setPassword("novaSenha123");
        request.setConfirmPassword("novaSenha123");

        existingWebUser = new WebUser(1, 5, "$2a$hash_antigo", 1);
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso quando dados são válidos")
    void deveAlterarSenhaComSucesso() throws Exception {
        when(webUserRepository.getById(1)).thenReturn(existingWebUser);
        when(passwordEncoder.matches("senhaAtual123", "$2a$hash_antigo")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$hash_novo");
        when(webUserRepository.save(any(WebUser.class))).thenReturn(existingWebUser);

        assertThatCode(() -> useCase.execute(1, request)).doesNotThrowAnyException();

        verify(passwordEncoder).matches("senhaAtual123", "$2a$hash_antigo");
        verify(passwordEncoder).encode("novaSenha123");
        verify(webUserRepository).save(any(WebUser.class));
    }

    @Test
    @DisplayName("Deve salvar a senha codificada, não a plaintext")
    void deveSalvarSenhaCodificada() throws Exception {
        when(webUserRepository.getById(1)).thenReturn(existingWebUser);
        when(passwordEncoder.matches("senhaAtual123", "$2a$hash_antigo")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$hash_novo");
        when(webUserRepository.save(any(WebUser.class))).thenAnswer(inv -> {
            WebUser wu = inv.getArgument(0);
            assertThat(wu.getPassword()).isEqualTo("$2a$hash_novo");
            assertThat(wu.getPassword()).doesNotContain("novaSenha123");
            return wu;
        });

        useCase.execute(1, request);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha atual está em branco")
    void deveLancarExcecaoQuandoSenhaAtualEmBranco() {
        request.setCurrentPassword("");

        assertThatThrownBy(() -> useCase.execute(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Senha atual é obrigatória");

        verifyNoInteractions(webUserRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha atual está nula")
    void deveLancarExcecaoQuandoSenhaAtualNula() {
        request.setCurrentPassword(null);

        assertThatThrownBy(() -> useCase.execute(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Senha atual é obrigatória");

        verifyNoInteractions(webUserRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha está em branco")
    void deveLancarExcecaoQuandoSenhaEmBranco() {
        request.setPassword("");

        assertThatThrownBy(() -> useCase.execute(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Senha é obrigatória");

        verifyNoInteractions(webUserRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senhas não conferem")
    void deveLancarExcecaoQuandoSenhasNaoConferem() {
        request.setConfirmPassword("senhaErrada");

        assertThatThrownBy(() -> useCase.execute(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Senhas não conferem");

        verifyNoInteractions(webUserRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() throws Exception {
        when(webUserRepository.getById(99)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(99, request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(passwordEncoder, never()).encode(any());
        verify(webUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha atual está incorreta")
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() throws Exception {
        when(webUserRepository.getById(1)).thenReturn(existingWebUser);
        when(passwordEncoder.matches("senhaAtual123", "$2a$hash_antigo")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Senha atual incorreta");

        verify(passwordEncoder, never()).encode(any());
        verify(webUserRepository, never()).save(any());
    }
}
