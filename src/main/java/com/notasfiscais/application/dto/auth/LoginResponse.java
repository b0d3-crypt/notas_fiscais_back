package com.notasfiscais.application.dto.auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nmPessoa;
    private Long cdPessoa;
    private Long cdWebUser;
    private Integer role;
}
