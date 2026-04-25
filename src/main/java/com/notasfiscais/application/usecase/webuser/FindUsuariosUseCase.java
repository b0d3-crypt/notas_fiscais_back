package com.notasfiscais.application.usecase.webuser;

import com.notasfiscais.application.dto.usuario.UsuarioListItemDTO;
import com.notasfiscais.application.queries.IUsuarioQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindUsuariosUseCase {

    private final IUsuarioQuery usuarioQuery;

    public FindUsuariosUseCase(IUsuarioQuery usuarioQuery) {
        this.usuarioQuery = usuarioQuery;
    }

    public List<UsuarioListItemDTO> execute() throws Exception {
        return usuarioQuery.findAll();
    }
}
