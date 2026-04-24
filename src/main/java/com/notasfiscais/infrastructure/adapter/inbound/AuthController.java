package com.notasfiscais.infrastructure.adapter.inbound;
import com.notasfiscais.application.dto.Response;
import com.notasfiscais.application.dto.auth.LoginRequest;
import com.notasfiscais.application.dto.auth.LoginResponse;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.application.usecase.webuser.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;
    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }
    @PostMapping("/login")
    @Operation(description = "Login de usuário.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas.")
    })
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = loginUseCase.execute(request);
            return ResponseEntity.ok(new Response<>(response, "Login realizado com sucesso"));
        } catch (WebUserException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new Response<>("Erro interno ao realizar login"));
        }
    }
}
