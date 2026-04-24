package com.notasfiscais.application.dto.auth;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    private String email;
    private String password;
}
