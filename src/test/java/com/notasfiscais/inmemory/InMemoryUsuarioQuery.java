package com.notasfiscais.inmemory;

import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.dto.usuario.UsuarioListItemDTO;
import com.notasfiscais.application.queries.IUsuarioQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUsuarioQuery implements IUsuarioQuery {

    private final Map<Integer, UsuarioDetailDTO> store = new HashMap<>();
    private final List<UsuarioListItemDTO> listStore = new ArrayList<>();

    public void add(UsuarioDetailDTO dto) {
        store.put(dto.getCdWebUser(), dto);
        listStore.add(UsuarioListItemDTO.builder()
                .cdWebUser(dto.getCdWebUser())
                .cdPessoa(dto.getCdPessoa())
                .nmPessoa(dto.getNmPessoa())
                .nrCpf(dto.getNrCpf())
                .nmEmail(dto.getNmEmail())
                .tpResponsabilidade(dto.getTpResponsabilidade())
                .build());
    }

    @Override
    public List<UsuarioListItemDTO> findAll() {
        return new ArrayList<>(listStore);
    }

    @Override
    public UsuarioDetailDTO findById(Integer cdWebUser) {
        return store.get(cdWebUser);
    }
}
