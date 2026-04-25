package com.notasfiscais.usecase.webuser;

import com.notasfiscais.application.dto.usuario.CreateUsuarioRequest;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.application.usecase.webuser.CreateUsuarioUseCase;
import com.notasfiscais.domain.model.Endereco;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IEnderecoRepository;
import com.notasfiscais.domain.repositories.IPessoaRepository;
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
@DisplayName("CreateUsuarioUseCase - Testes Unitários")
class CreateUsuarioUseCaseTest {

    @Mock private IEnderecoRepository enderecoRepository;
    @Mock private IPessoaRepository pessoaRepository;
    @Mock private IWebUserRepository webUserRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUsuarioUseCase useCase;

    private CreateUsuarioRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateUsuarioRequest();
        request.setNmPessoa("João Silva");
        request.setNrCpf("12345678901");
        request.setNmEmail("joao@email.com");
        request.setPassword("senha123");
        request.setTpResponsabilidade(1);
        request.setNmLogradouro("Rua A");
        request.setNrCep("40000000");
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso quando dados são válidos")
    void deveCriarUsuarioComSucesso() throws Exception {
        Endereco endereco = new Endereco(10, "Rua A", null, "40000000", null, null, null, null);
        Pessoa pessoa = new Pessoa(5, 10, "João Silva", null, "12345678901", "joao@email.com");
        WebUser webUser = new WebUser(3, 5, "$2a$hash", 1);

        when(pessoaRepository.getByEmail("joao@email.com")).thenReturn(null);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");
        when(webUserRepository.save(any(WebUser.class))).thenReturn(webUser);

        Integer cdWebUser = useCase.execute(request);

        assertThat(cdWebUser).isEqualTo(3);
        verify(enderecoRepository).save(any(Endereco.class));
        verify(pessoaRepository).save(any(Pessoa.class));
        verify(webUserRepository).save(any(WebUser.class));
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    @DisplayName("Deve usar tpResponsabilidade=1 quando não informado")
    void deveUsarTpResponsabilidadePadraoQuandoNaoInformado() throws Exception {
        request.setTpResponsabilidade(null);

        Endereco endereco = new Endereco(10, null, null, null, null, null, null, null);
        Pessoa pessoa = new Pessoa(5, 10, "João Silva", null, "12345678901", "joao@email.com");

        when(pessoaRepository.getByEmail(any())).thenReturn(null);
        when(enderecoRepository.save(any())).thenReturn(endereco);
        when(pessoaRepository.save(any())).thenReturn(pessoa);
        when(passwordEncoder.encode(any())).thenReturn("$2a$hash");
        when(webUserRepository.save(any(WebUser.class))).thenAnswer(inv -> {
            WebUser wu = inv.getArgument(0);
            assertThat(wu.getTpResponsabilidade()).isEqualTo(1);
            return new WebUser(1, wu.getCdPessoa(), wu.getPassword(), wu.getTpResponsabilidade());
        });

        useCase.execute(request);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando nome está em branco")
    void deveLancarExcecaoQuandoNomeEmBranco() {
        request.setNmPessoa("");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nome é obrigatório");

        verifyNoInteractions(enderecoRepository, webUserRepository);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando CPF está em branco")
    void deveLancarExcecaoQuandoCpfEmBranco() {
        request.setNrCpf("");

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("CPF é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando email está em branco")
    void deveLancarExcecaoQuandoEmailEmBranco() {
        request.setNmEmail(null);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Email é obrigatório");
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando senha está em branco")
    void deveLancarExcecaoQuandoSenhaEmBranco() {
        request.setPassword(null);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Senha é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar WebUserException quando email já está cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() throws Exception {
        Pessoa pessoaExistente = new Pessoa(2, 1, "Outro", null, "99999999999", "joao@email.com");
        when(pessoaRepository.getByEmail("joao@email.com")).thenReturn(pessoaExistente);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(WebUserException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(enderecoRepository, never()).save(any());
        verify(webUserRepository, never()).save(any());
    }
}
