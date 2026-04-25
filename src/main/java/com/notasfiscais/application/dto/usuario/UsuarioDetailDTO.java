package com.notasfiscais.application.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDetailDTO {
    private Integer cdWebUser;
    private Integer cdPessoa;
    private Integer cdEndereco;
    private String nmPessoa;
    private String nrCpf;
    private String nmEmail;
    private String nrTelefone;
    private Integer tpResponsabilidade;
    // endereco fields
    private String nmLogradouro;
    private String dsEndereco;
    private String nrEndereco;
    private String nrCep;
    private String bairro;
    private String cidade;
    private String estado;
}
