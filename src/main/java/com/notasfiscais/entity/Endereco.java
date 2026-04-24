package com.notasfiscais.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "endereco")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_endereco")
    private Integer cdEndereco;

    @Column(name = "nm_logradouro")
    private String nmLogradouro;

    @Column(name = "ds_endereco")
    private String dsEndereco;

    @Column(name = "nr_cep")
    private String nrCep;

    @Column(name = "nr_endereco")
    private String nrEndereco;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado")
    private String estado;
}
