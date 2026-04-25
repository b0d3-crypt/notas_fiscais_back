package com.notasfiscais.application.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioListItemDTO {
    private Integer cdWebUser;
    private Integer cdPessoa;
    private String nmPessoa;
    private String nrCpf;
    private String nmEmail;
    private Integer tpResponsabilidade;
}
