package com.notasfiscais.controller;

import com.notasfiscais.auth.JwtAuthDetails;
import com.notasfiscais.dto.DespesaDetailDTO;
import com.notasfiscais.dto.DespesaListItemDTO;
import com.notasfiscais.dto.Response;
import com.notasfiscais.entity.DescricaoDespesa;
import com.notasfiscais.enums.TipoArquivo;
import com.notasfiscais.exception.GlobalExceptionHandler.AcessoNegadoException;
import com.notasfiscais.exception.GlobalExceptionHandler.RecursoNaoEncontradoException;
import com.notasfiscais.exception.GlobalExceptionHandler.ValidacaoException;
import com.notasfiscais.service.ArquivoService;
import com.notasfiscais.service.DespesaService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final DespesaService despesaService;
    private final ArquivoService arquivoService;

    public DespesaController(DespesaService despesaService, ArquivoService arquivoService) {
        this.despesaService = despesaService;
        this.arquivoService = arquivoService;
    }

    @GetMapping
    public ResponseEntity<Response<List<DespesaListItemDTO>>> listar(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        try {
            List<DespesaListItemDTO> lista = despesaService.listar(ano, mes);
            return ResponseEntity.ok(new Response<>(lista, "Despesas buscadas com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao buscar despesas"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<DespesaDetailDTO>> buscar(@PathVariable Integer id) {
        try {
            DespesaDetailDTO dto = despesaService.buscar(id);
            return ResponseEntity.ok(new Response<>(dto, "Despesa buscada com sucesso"));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao buscar despesa"));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<DespesaDetailDTO>> criar(
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestPart("dtDespesa") String dtDespesa,
            @RequestPart("vlDespesa") String vlDespesa,
            @RequestPart(value = "dsDespesa", required = false) String dsDespesa,
            @AuthenticationPrincipal JwtAuthDetails authDetails) {
        try {
            DespesaDetailDTO dto = despesaService.criar(
                    arquivo, dtDespesa, new BigDecimal(vlDespesa), dsDespesa, authDetails);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(dto, "Despesa criada com sucesso"));
        } catch (ValidacaoException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao criar despesa"));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<DespesaDetailDTO>> atualizar(
            @PathVariable Integer id,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
            @RequestPart(value = "dtDespesa", required = false) String dtDespesa,
            @RequestPart(value = "vlDespesa", required = false) String vlDespesa,
            @RequestPart(value = "dsDespesa", required = false) String dsDespesa,
            @AuthenticationPrincipal JwtAuthDetails authDetails) {
        try {
            BigDecimal valor = (vlDespesa != null && !vlDespesa.isBlank())
                    ? new BigDecimal(vlDespesa) : null;
            DespesaDetailDTO dto = despesaService.atualizar(id, arquivo, dtDespesa,
                    valor, dsDespesa, authDetails);
            return ResponseEntity.ok(new Response<>(dto, "Despesa atualizada com sucesso"));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>(e.getMessage()));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(e.getMessage()));
        } catch (ValidacaoException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao atualizar despesa"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deletar(
            @PathVariable Integer id,
            @AuthenticationPrincipal JwtAuthDetails authDetails) {
        try {
            despesaService.deletar(id, authDetails);
            return ResponseEntity.noContent().build();
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>(e.getMessage()));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao deletar despesa"));
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Integer id) {
        try {
            DescricaoDespesa despesa = despesaService.findOrThrow(id);
            byte[] bytes = arquivoService.lerBytes(despesa.getArquivo().getCaminhoArquivo());

            TipoArquivo tipo = TipoArquivo.fromCodigo(despesa.getArquivo().getTpArquivo());
            MediaType mediaType = tipo != null
                    ? MediaType.parseMediaType(tipo.getMimeType())
                    : MediaType.APPLICATION_OCTET_STREAM;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(despesa.getArquivo().getNmArquivo()).build());
            headers.setContentType(mediaType);

            return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(bytes));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
