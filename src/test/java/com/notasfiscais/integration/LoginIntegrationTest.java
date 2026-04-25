package com.notasfiscais.integration;

import com.notasfiscais.application.dto.auth.LoginRequest;
import com.notasfiscais.application.dto.auth.LoginResponse;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.application.usecase.webuser.LoginUseCase;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.inmemory.InMemoryPessoaRepository;
import com.notasfiscais.inmemory.InMemoryWebUserRepository;
import com.notasfiscais.infrastructure.configuration.jwt.JwtProperties;
import com.notasfiscais.infrastructure.configuration.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Login - Testes de Integração (In-Memory)")
class LoginIntegrationTest {

    private InMemoryWebUserRepository webUserRepository;
    private InMemoryPessoaRepository pessoaRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private LoginUseCase loginUseCase;

    private final String EMAIL = "joao@email.com";
    private final String SENHA = "senha123";

    @BeforeEach
    void setUp() {
        webUserRepository = new InMemoryWebUserRepository();
        pessoaRepository = new InMemoryPessoaRepository();
        passwordEncoder = new BCryptPasswordEncoder();

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("chaveSecretaParaTestesUnitariosDeIntegracao1234");
        jwtProperties.setExpiration(86400000L);
        jwtService = new JwtService(jwtProperties);
        jwtService.init();

        loginUseCase = new LoginUseCase(webUserRepository, pessoaRepository, passwordEncoder, jwtService);

        // Setup: criar pessoa e usuário no "banco" in-memory
        Pessoa pessoa = pessoaRepository.save(
                new Pessoa(null, 1, "João Silva", "11999990000", "12345678901", EMAIL));

        String senhaHash = passwordEncoder.encode(SENHA);
        WebUser webUser = new WebUser(null, pessoa.getCdPessoa(), senhaHash, 1);
        webUserRepository.saveWithEmail(webUser, EMAIL);
    }

    @Test
    @DisplayName("Deve retornar token JWT válido com credenciais corretas")
    void deveRetornarTokenComCredenciaisCorretas() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(SENHA);

        LoginResponse response = loginUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getNmPessoa()).isEqualTo("João Silva");
        assertThat(response.getCdPessoa()).isPositive();
    }

    @Test
    @DisplayName("Deve lançar WebUserException com senha incorreta")
    void deveLancarExcecaoComSenhaErrada() {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword("senhaErrada");

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(WebUserException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    @DisplayName("Deve lançar WebUserException com email inexistente")
    void deveLancarExcecaoComEmailInexistente() {
        LoginRequest request = new LoginRequest();
        request.setEmail("naoexiste@email.com");
        request.setPassword(SENHA);

        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(WebUserException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    @DisplayName("Token gerado deve conter email do usuário")
    void tokenDeveConterEmailDoUsuario() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(SENHA);

        LoginResponse response = loginUseCase.execute(request);
        String emailExtraido = jwtService.extractEmail(response.getToken());

        assertThat(emailExtraido).isEqualTo(EMAIL);
    }
}

