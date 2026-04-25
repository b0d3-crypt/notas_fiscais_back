package com.notasfiscais.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UpdateUsuarioRequest;
import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.queries.IUsuarioQuery;
import com.notasfiscais.application.usecase.webuser.UpdateUsuarioUseCase;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUsuarioUseCase - Testes Unitários")
class UpdateUsuarioUseCaseTest {

    @Mock private IUsuarioQuery usuarioQuery;
    @Mock private IEnderecoRepository enderecoRepository;
    @Mock private IPessoaRepository pessoaRepository;
    @Mock private IWebUserRepository webUserRepository;

    @InjectMocks
    private UpdateUsuarioUseCase useCase;

    private UpdateUsuarioRequest request;
    private UsuarioDetailDTO current;
    private WebUser existingWebUser;

    @BeforeEach
    void setUp() {
        request = new UpdateUsuarioRequest();
        request.setNmPessoa("João Atualizado");
        request.setNmEmail("joao@email.com");
        request.setNrTelefone("71999990000");
        request.setTpResponsabilidade(1);

        current = UsuarioDetailDTO.builder()
                .cdWebUser(1)
                .cdPessoa(5)
                .cdEndereco(10)
                .nmPessoa("João Silva")
                .nrCpf("12345678901")
                .nmEmail("joao.old@email.com")
                .tpResponsabilidade(1)
                .build();

        existingWebUser = new WebUser(1, 5, "$2a$hash_original", 1);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso quando dados são válidos")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        when(usuarioQuery.findById(1)).thenReturn(current);
        when(webUserRepository.getById(1)).thenReturn(existingWebUser);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(new Endereco(10, null, null, null, null, null, null, null));
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(new Pessoa(5, 10, "João Atualizado", null, "12345678901", "joao@email.com"));
        when(webUserRepository.save(any(WebUser.class))).thenReturn(existingWebUser);

        assertThatCode(() -> useCase.execute(1, request)).doesNotThrowAnyException();

        verify(enderecoRepository).save(any(Endereco.class));
        verify(pessoaRepository).save(any(Pessoa.class));
        verify(webUserRepository).save(any(WebUser.class));
    }

    @Test
    @DisplayName("Deve preservar CPF original ao atualizar")
    void devePreservarCpfOriginal() throws Exception {
        when(usuarioQuery.findById(1)).thenReturn(current);
        when(webUserRepository.getById(1)).thenReturn(existingWebUser);
        when(enderecoRepository.save(any())).thenReturn(new Endereco(10, null, null, null, null, null, null, null));
        when(pessoaRepository.save(any(Pessoa.class))).thenAnswer(inv -> {
            Pessoa p = inv.getArgument(0);
            assertThat(p.getNrCpf()).isEqualTo("12345678901");
            return p;
        });
        when(webUserRepository.save(any())).thenReturn(existingWebUser);

        useCase.execute(1, request);
    }

    @Test
    @DisplayName("Deve preservar tpResponsabilidade existente quando request envia null")
    void devePreservarTpResponsabilidadeQuandoNull() throws Exception {
        request.setTpResponsabilidade(null);

        when(usuarioQuery.findById(1)).thenReturn(current);
        when(webUserRepository.getById(1)).thenReturn(existingWebUser);
        when(enderecoRepository.save(any())).thenReturn(new Endereco(10, null, null, null, null, null, null, null));
        when(pessoaRepository.save(any())).thenReturn(new Pessoa(5, 10, "João Atualizado", null, "12345678901", "joao@email.com"));
        when(webUserRepository.save(any(WebUser.class))).thenAnswer(inv -> {
            WebUser wu = inv.getArgument(0);
            assertThat(wu.getTpResponsabilidade()).isEqualTo(1); // mantém o existente
            return wu;
        });

        useCase.execute(1, request);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando nome está em branco")
    void deveLancarExcecaoQuandoNomeEmBranco() {
        request.setNmPessoa("  ");

        assertThatThrownBy(() -> useCase.execute(1, request))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nome é obrigatório");

        verifyNoInteractions(usuarioQuery, enderecoRepository, pessoaRepository, webUserRepository);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() throws Exception {
        when(usuarioQuery.findById(99)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(99, request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(enderecoRepository, never()).save(any());
        verify(webUserRepository, never()).save(any());
    }
}
