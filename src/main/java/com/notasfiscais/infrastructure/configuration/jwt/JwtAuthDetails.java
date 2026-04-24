package com.notasfiscais.infrastructure.configuration.jwt;
public class JwtAuthDetails {
    private final String email;
    private final Long cdPessoa;
    private final Integer role;
    public JwtAuthDetails(String email, Long cdPessoa, Integer role) {
        this.email = email;
        this.cdPessoa = cdPessoa;
        this.role = role;
    }
    public String getEmail() { return email; }
    public Long getCdPessoa() { return cdPessoa; }
    public Integer getRole() { return role; }
}
