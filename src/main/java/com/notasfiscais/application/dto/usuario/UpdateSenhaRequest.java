package com.notasfiscais.application.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSenhaRequest {
    private String currentPassword;
    private String password;
    private String confirmPassword;
}
