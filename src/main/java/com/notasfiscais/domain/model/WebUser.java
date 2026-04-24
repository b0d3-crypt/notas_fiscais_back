package com.notasfiscais.domain.model;
public class WebUser {
    private Integer cdWebUser;
    private Integer cdPessoa;
    private String password;
    private Integer tpResponsabilidade;
    public WebUser(Integer cdWebUser, Integer cdPessoa, String password, Integer tpResponsabilidade) {
        this.cdWebUser = cdWebUser;
        this.cdPessoa = cdPessoa;
        this.password = password;
        this.tpResponsabilidade = tpResponsabilidade;
    }
    public Integer getCdWebUser() { return cdWebUser; }
    public Integer getCdPessoa() { return cdPessoa; }
    public String getPassword() { return password; }
    public Integer getTpResponsabilidade() { return tpResponsabilidade; }
}
