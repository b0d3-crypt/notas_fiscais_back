package com.notasfiscais.infrastructure.adapter.inbound;

import com.notasfiscais.application.dto.EnderecoCorreiosDTO;
import com.notasfiscais.application.dto.Response;
import com.notasfiscais.application.exceptions.CepNaoEncontradoException;
import com.notasfiscais.application.usecase.GetEnderecoCorreiosUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/correios")
public class CorreiosController {

    private final GetEnderecoCorreiosUseCase getEnderecoCorreiosUseCase;

    public CorreiosController(GetEnderecoCorreiosUseCase getEnderecoCorreiosUseCase) {
        this.getEnderecoCorreiosUseCase = getEnderecoCorreiosUseCase;
    }

    @GetMapping("/{nrCep}")
    public ResponseEntity<Response<EnderecoCorreiosDTO>> getEndereco(@PathVariable String nrCep) {
        try {
            EnderecoCorreiosDTO endereco = getEnderecoCorreiosUseCase.execute(nrCep);
            return ResponseEntity.ok(new Response<>(endereco, "Endereço encontrado com sucesso"));
        } catch (CepNaoEncontradoException e) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro ao buscar endereço"));
        }
    }
}
