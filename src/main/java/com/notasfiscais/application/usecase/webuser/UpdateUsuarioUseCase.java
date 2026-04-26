package com.notasfiscais.application.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UpdateUsuarioRequest;
import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.queries.IUsuarioQuery;
import com.notasfiscais.domain.model.Endereco;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IEnderecoRepository;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUsuarioUseCase {

    private final IUsuarioQuery usuarioQuery;
    private final IEnderecoRepository enderecoRepository;
    private final IPessoaRepository pessoaRepository;
    private final IWebUserRepository webUserRepository;

    public UpdateUsuarioUseCase(IUsuarioQuery usuarioQuery,
                                IEnderecoRepository enderecoRepository,
                                IPessoaRepository pessoaRepository,
                                IWebUserRepository webUserRepository) {
        this.usuarioQuery = usuarioQuery;
        this.enderecoRepository = enderecoRepository;
        this.pessoaRepository = pessoaRepository;
        this.webUserRepository = webUserRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void execute(Integer cdWebUser, UpdateUsuarioRequest request) throws Exception {
        if (request.getNmPessoa() == null || request.getNmPessoa().isBlank()) {
            throw new ValidacaoException("Nome é obrigatório");
        }

        UsuarioDetailDTO current = usuarioQuery.findById(cdWebUser);
        if (current == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }

        String nrCep = request.getNrCep() != null ? request.getNrCep().replaceAll("[^\\d]", "") : null;

        enderecoRepository.save(new Endereco(
                current.getCdEndereco(),
                request.getNmLogradouro(),
                request.getDsEndereco(),
                nrCep,
                request.getNrEndereco(),
                request.getBairro(),
                request.getCidade(),
                request.getEstado()
        ));

        // CPF não pode ser alterado — mantemos o original
        pessoaRepository.save(new Pessoa(
                current.getCdPessoa(),
                current.getCdEndereco(),
                request.getNmPessoa(),
                request.getNrTelefone(),
                current.getNrCpf(),
                request.getNmEmail()
        ));

        // Busca a senha atual (não altera)
        WebUser existing = webUserRepository.getById(cdWebUser);
        webUserRepository.save(new WebUser(
                cdWebUser,
                current.getCdPessoa(),
                existing.getPassword(),
                request.getTpResponsabilidade() != null ? request.getTpResponsabilidade() : existing.getTpResponsabilidade()
        ));
    }
}
