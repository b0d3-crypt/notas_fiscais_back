package com.notasfiscais.controller;

import com.notasfiscais.dto.LoginRequest;
import com.notasfiscais.dto.LoginResponse;
import com.notasfiscais.dto.Response;
import com.notasfiscais.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new Response<>(response, "Login realizado com sucesso"));
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new Response<>(e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new Response<>("Erro interno ao realizar login"));
        }
    }
}
