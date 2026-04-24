package com.notasfiscais.domain.model;
public class Endereco {
    private Integer cdEndereco;
    private String nmLogradouro;
    private String dsEndereco;
    private String nrCep;
    private String nrEndereco;
    private String bairro;
    private String cidade;
    private String estado;
    public Endereco(Integer cdEndereco, String nmLogradouro, String dsEndereco,
                    String nrCep, String nrEndereco, String bairro, String cidade, String estado) {
        this.cdEndereco = cdEndereco;
        this.nmLogradouro = nmLogradouro;
        this.dsEndereco = dsEndereco;
        this.nrCep = nrCep;
        this.nrEndereco = nrEndereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }
    public Integer getCdEndereco() { return cdEndereco; }
    public String getNmLogradouro() { return nmLogradouro; }
    public String getDsEndereco() { return dsEndereco; }
    public String getNrCep() { return nrCep; }
    public String getNrEndereco() { return nrEndereco; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
}
