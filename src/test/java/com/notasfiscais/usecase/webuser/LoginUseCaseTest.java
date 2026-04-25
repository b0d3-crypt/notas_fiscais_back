package com.notasfiscais.usecase.webuser;

import com.notasfiscais.application.dto.auth.LoginRequest;
import com.notasfiscais.application.dto.auth.LoginResponse;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.application.usecase.webuser.LoginUseCase;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import com.notasfiscais.infrastructure.configuration.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase - Testes Unitários")
class LoginUseCaseTest {

    @Mock private IWebUserRepository webUserRepository;
    @Mock private IPessoaRepository pessoaRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private LoginUseCase useCase;

    private final String EMAIL = "joao@email.com";
    private final String SENHA_PLAIN = "senha123";
    private final String SENHA_HASH = "$2a$10$hashedpassword";

    private WebUser webUser;
    private Pessoa pessoa;
    private LoginRequest request;

    @BeforeEach
    void setUp() {
        webUser = new WebUser(1, 1, SENHA_HASH, 1);
        pessoa = new Pessoa(1, 1, "João Silva", "11999990000", "12345678901", EMAIL);
        request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(SENHA_PLAIN);
    }

    @Test
    @DisplayName("Deve retornar token quando credenciais são válidas")
    void deveRetornarTokenComCredenciaisValidas() throws Exception {
        when(webUserRepository.getByEmail(EMAIL)).thenReturn(webUser);
        when(passwordEncoder.matches(SENHA_PLAIN, SENHA_HASH)).thenReturn(true);
        when(pessoaRepository.get(1)).thenReturn(pessoa);
        when(jwtService.generateToken(webUser, pessoa)).thenReturn("jwt.token.aqui");

        LoginResponse response = useCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.token.aqui");
        assertThat(response.getNmPessoa()).isEqualTo("João Silva");
        assertThat(response.getCdPessoa()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar WebUserException quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() throws Exception {
        when(webUserRepository.getByEmail(EMAIL)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(WebUserException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    @DisplayName("Deve lançar WebUserException quando senha é incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() throws Exception {
        when(webUserRepository.getByEmail(EMAIL)).thenReturn(webUser);
        when(passwordEncoder.matches(SENHA_PLAIN, SENHA_HASH)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(WebUserException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    @DisplayName("Deve lançar WebUserException para erros inesperados")
    void deveLancarWebUserExceptionParaErroInesperado() throws Exception {
        when(webUserRepository.getByEmail(EMAIL)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(WebUserException.class)
                .hasMessageContaining("Erro ao realizar login");
    }
}


