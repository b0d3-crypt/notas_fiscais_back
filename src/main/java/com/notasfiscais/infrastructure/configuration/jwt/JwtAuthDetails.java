package com.notasfiscais.infrastructure.configuration.jwt;
public class JwtAuthDetails {
    private final String email;
    private final Long cdPessoa;
    private final Long cdWebUser;
    private final Integer role;
    public JwtAuthDetails(String email, Long cdPessoa, Long cdWebUser, Integer role) {
        this.email = email;
        this.cdPessoa = cdPessoa;
        this.cdWebUser = cdWebUser;
        this.role = role;
    }
    public String getEmail() { return email; }
    public Long getCdPessoa() { return cdPessoa; }
    public Long getCdWebUser() { return cdWebUser; }
    public Integer getRole() { return role; }
}
