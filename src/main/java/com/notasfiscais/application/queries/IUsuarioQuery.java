package com.notasfiscais.application.queries;

import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.dto.usuario.UsuarioListItemDTO;

import java.util.List;

public interface IUsuarioQuery {
    List<UsuarioListItemDTO> findAll() throws Exception;
    UsuarioDetailDTO findById(Integer cdWebUser) throws Exception;
}
