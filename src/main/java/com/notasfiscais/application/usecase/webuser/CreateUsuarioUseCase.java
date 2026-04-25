package com.notasfiscais.application.usecase.webuser;

import com.notasfiscais.application.dto.usuario.CreateUsuarioRequest;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.domain.model.Endereco;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IEnderecoRepository;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUsuarioUseCase {

    private final IEnderecoRepository enderecoRepository;
    private final IPessoaRepository pessoaRepository;
    private final IWebUserRepository webUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CreateUsuarioUseCase(IEnderecoRepository enderecoRepository,
                                IPessoaRepository pessoaRepository,
                                IWebUserRepository webUserRepository,
                                BCryptPasswordEncoder passwordEncoder) {
        this.enderecoRepository = enderecoRepository;
        this.pessoaRepository = pessoaRepository;
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer execute(CreateUsuarioRequest request) throws Exception {
        validar(request);

        Endereco endereco = enderecoRepository.save(new Endereco(
                null,
                request.getNmLogradouro(),
                request.getDsEndereco(),
                request.getNrCep(),
                request.getNrEndereco(),
                request.getBairro(),
                request.getCidade(),
                request.getEstado()
        ));

        Pessoa pessoa = pessoaRepository.save(new Pessoa(
                null,
                endereco.getCdEndereco(),
                request.getNmPessoa(),
                request.getNrTelefone(),
                request.getNrCpf(),
                request.getNmEmail()
        ));

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        WebUser webUser = webUserRepository.save(new WebUser(
                null,
                pessoa.getCdPessoa(),
                encodedPassword,
                request.getTpResponsabilidade() != null ? request.getTpResponsabilidade() : 1
        ));

        return webUser.getCdWebUser();
    }

    private void validar(CreateUsuarioRequest request) throws Exception {
        if (request.getNmPessoa() == null || request.getNmPessoa().isBlank()) {
            throw new ValidacaoException("Nome é obrigatório");
        }
        if (request.getNrCpf() == null || request.getNrCpf().isBlank()) {
            throw new ValidacaoException("CPF é obrigatório");
        }
        if (request.getNmEmail() == null || request.getNmEmail().isBlank()) {
            throw new ValidacaoException("Email é obrigatório");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidacaoException("Senha é obrigatória");
        }

        Pessoa pessoaExistente = pessoaRepository.getByEmail(request.getNmEmail());
        if (pessoaExistente != null) {
            throw new WebUserException("Email já cadastrado");
        }
    }
}
