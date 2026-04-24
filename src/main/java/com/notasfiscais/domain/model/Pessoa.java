package com.notasfiscais.domain.model;
public class Pessoa {
    private Integer cdPessoa;
    private Integer cdEndereco;
    private String nmPessoa;
    private String nrTelefone;
    private String nrCpf;
    private String nmEmail;
    public Pessoa(Integer cdPessoa, Integer cdEndereco, String nmPessoa,
                  String nrTelefone, String nrCpf, String nmEmail) {
        this.cdPessoa = cdPessoa;
        this.cdEndereco = cdEndereco;
        this.nmPessoa = nmPessoa;
        this.nrTelefone = nrTelefone;
        this.nrCpf = nrCpf;
        this.nmEmail = nmEmail;
    }
    public Integer getCdPessoa() { return cdPessoa; }
    public Integer getCdEndereco() { return cdEndereco; }
    public String getNmPessoa() { return nmPessoa; }
    public String getNrTelefone() { return nrTelefone; }
    public String getNrCpf() { return nrCpf; }
    public String getNmEmail() { return nmEmail; }
}
