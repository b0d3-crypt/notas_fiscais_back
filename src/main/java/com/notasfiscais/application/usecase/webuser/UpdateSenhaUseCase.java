package com.notasfiscais.application.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UpdateSenhaRequest;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSenhaUseCase {

    private final IWebUserRepository webUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UpdateSenhaUseCase(IWebUserRepository webUserRepository,
                              BCryptPasswordEncoder passwordEncoder) {
        this.webUserRepository = webUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = Exception.class)
    public void execute(Integer cdWebUser, UpdateSenhaRequest request) throws Exception {
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new ValidacaoException("Senha atual é obrigatória");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidacaoException("Senha é obrigatória");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidacaoException("Senhas não conferem");
        }

        WebUser existing = webUserRepository.getById(cdWebUser);
        if (existing == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), existing.getPassword())) {
            throw new ValidacaoException("Senha atual incorreta");
        }

        String encoded = passwordEncoder.encode(request.getPassword());
        webUserRepository.save(new WebUser(
                cdWebUser,
                existing.getCdPessoa(),
                encoded,
                existing.getTpResponsabilidade()
        ));
    }
}
