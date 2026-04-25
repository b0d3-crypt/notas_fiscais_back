package com.notasfiscais.application.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.queries.IUsuarioQuery;
import org.springframework.stereotype.Service;

@Service
public class GetUsuarioUseCase {

    private final IUsuarioQuery usuarioQuery;

    public GetUsuarioUseCase(IUsuarioQuery usuarioQuery) {
        this.usuarioQuery = usuarioQuery;
    }

    public UsuarioDetailDTO execute(Integer cdWebUser) throws Exception {
        UsuarioDetailDTO dto = usuarioQuery.findById(cdWebUser);
        if (dto == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }
        return dto;
    }
}
