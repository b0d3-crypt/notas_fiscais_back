package com.notasfiscais.application.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUsuarioRequest {
    private String nmPessoa;
    private String nrCpf;
    private String nmEmail;
    private String nrTelefone;
    private Integer tpResponsabilidade;
    private String password;
    // endereco
    private String nmLogradouro;
    private String dsEndereco;
    private String nrEndereco;
    private String nrCep;
    private String bairro;
    private String cidade;
    private String estado;
}
