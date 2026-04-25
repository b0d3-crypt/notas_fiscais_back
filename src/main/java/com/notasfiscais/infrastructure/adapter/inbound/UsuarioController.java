package com.notasfiscais.infrastructure.adapter.inbound;

import com.notasfiscais.application.dto.Response;
import com.notasfiscais.application.dto.usuario.CreateUsuarioRequest;
import com.notasfiscais.application.dto.usuario.UpdateSenhaRequest;
import com.notasfiscais.application.dto.usuario.UpdateUsuarioRequest;
import com.notasfiscais.application.dto.usuario.UsuarioDetailDTO;
import com.notasfiscais.application.dto.usuario.UsuarioListItemDTO;
import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.exceptions.WebUserException;
import com.notasfiscais.application.usecase.webuser.CreateUsuarioUseCase;
import com.notasfiscais.application.usecase.webuser.FindUsuariosUseCase;
import com.notasfiscais.application.usecase.webuser.GetUsuarioUseCase;
import com.notasfiscais.application.usecase.webuser.UpdateSenhaUseCase;
import com.notasfiscais.application.usecase.webuser.UpdateUsuarioUseCase;
import com.notasfiscais.domain.enums.TpResponsabilidadeEnum;
import com.notasfiscais.infrastructure.configuration.jwt.JwtAuthDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final FindUsuariosUseCase findUsuariosUseCase;
    private final GetUsuarioUseCase getUsuarioUseCase;
    private final CreateUsuarioUseCase createUsuarioUseCase;
    private final UpdateUsuarioUseCase updateUsuarioUseCase;
    private final UpdateSenhaUseCase updateSenhaUseCase;

    public UsuarioController(FindUsuariosUseCase findUsuariosUseCase,
                             GetUsuarioUseCase getUsuarioUseCase,
                             CreateUsuarioUseCase createUsuarioUseCase,
                             UpdateUsuarioUseCase updateUsuarioUseCase,
                             UpdateSenhaUseCase updateSenhaUseCase) {
        this.findUsuariosUseCase = findUsuariosUseCase;
        this.getUsuarioUseCase = getUsuarioUseCase;
        this.createUsuarioUseCase = createUsuarioUseCase;
        this.updateUsuarioUseCase = updateUsuarioUseCase;
        this.updateSenhaUseCase = updateSenhaUseCase;
    }

    @GetMapping
    @Operation(description = "Listagem de usuários (admin only).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso."),
        @ApiResponse(responseCode = "403", description = "Acesso negado."),
        @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<Response<List<UsuarioListItemDTO>>> findAll(
            @AuthenticationPrincipal JwtAuthDetails auth) {
        try {
            requireAdmin(auth);
            List<UsuarioListItemDTO> list = findUsuariosUseCase.execute();
            return ResponseEntity.ok(new Response<>(list, "Usuários listados com sucesso"));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao listar usuários"));
        }
    }

    @GetMapping("/{id}")
    @Operation(description = "Busca de usuário por ID. Admin pode acessar qualquer usuário; usuário comum pode acessar apenas o próprio.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
        @ApiResponse(responseCode = "403", description = "Acesso negado."),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
        @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<Response<UsuarioDetailDTO>> getById(
            @PathVariable Integer id,
            @AuthenticationPrincipal JwtAuthDetails auth) {
        try {
            boolean isAdmin = auth != null && auth.getRole() == TpResponsabilidadeEnum.ADMIN.getCodigo();
            boolean isSelf = auth != null && auth.getCdWebUser() != null && auth.getCdWebUser().intValue() == id;
            if (!isAdmin && !isSelf) {
                throw new AcessoNegadoException("Acesso restrito");
            }
            UsuarioDetailDTO dto = getUsuarioUseCase.execute(id);
            return ResponseEntity.ok(new Response<>(dto, "Usuário encontrado"));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao buscar usuário"));
        }
    }

    @PostMapping
    @Operation(description = "Criação de usuário (admin only).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "403", description = "Acesso negado."),
        @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<Response<Integer>> create(
            @RequestBody CreateUsuarioRequest request,
            @AuthenticationPrincipal JwtAuthDetails auth) {
        try {
            requireAdmin(auth);
            Integer id = createUsuarioUseCase.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(id, "Usuário criado com sucesso"));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (ValidacaoException | WebUserException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao criar usuário"));
        }
    }

    @PutMapping("/{id}")
    @Operation(description = "Atualização de dados gerais. Admin pode atualizar qualquer usuário; usuário comum pode atualizar apenas o próprio.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "403", description = "Acesso negado."),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
        @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<Response<Void>> update(
            @PathVariable Integer id,
            @RequestBody UpdateUsuarioRequest request,
            @AuthenticationPrincipal JwtAuthDetails auth) {
        try {
            boolean isAdmin = auth != null && auth.getRole() == TpResponsabilidadeEnum.ADMIN.getCodigo();
            boolean isSelf = auth != null && auth.getCdWebUser() != null && auth.getCdWebUser().intValue() == id;
            if (!isAdmin && !isSelf) {
                throw new AcessoNegadoException("Acesso restrito");
            }
            // Somente admin pode alterar o tpResponsabilidade de outro usuário
            if (!isAdmin) {
                request.setTpResponsabilidade(null);
            }
            updateUsuarioUseCase.execute(id, request);
            return ResponseEntity.ok(new Response<>(null, "Usuário atualizado com sucesso"));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage()));
        } catch (ValidacaoException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao atualizar usuário"));
        }
    }

    @PutMapping("/{id}/senha")
    @Operation(description = "Atualização de senha. Admin pode atualizar qualquer senha; usuário comum pode atualizar apenas a própria.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "403", description = "Acesso negado."),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
        @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<Response<Void>> updateSenha(
            @PathVariable Integer id,
            @RequestBody UpdateSenhaRequest request,
            @AuthenticationPrincipal JwtAuthDetails auth) {
        try {
            boolean isAdmin = auth != null && auth.getRole() == TpResponsabilidadeEnum.ADMIN.getCodigo();
            boolean isSelf = auth != null && auth.getCdWebUser() != null && auth.getCdWebUser().intValue() == id;
            if (!isAdmin && !isSelf) {
                throw new AcessoNegadoException("Acesso restrito");
            }
            updateSenhaUseCase.execute(id, request);
            return ResponseEntity.ok(new Response<>(null, "Senha atualizada com sucesso"));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage()));
        } catch (ValidacaoException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao atualizar senha"));
        }
    }

    private void requireAdmin(JwtAuthDetails auth) {
        if (auth == null || auth.getRole() != TpResponsabilidadeEnum.ADMIN.getCodigo()) {
            throw new AcessoNegadoException("Acesso restrito a administradores");
        }
    }
}
