package com.notasfiscais.infrastructure.adapter.inbound;
import com.notasfiscais.application.dto.Response;
import com.notasfiscais.application.dto.despesa.DespesaDetailDTO;
import com.notasfiscais.application.dto.despesa.DespesaListItemDTO;
import com.notasfiscais.application.exceptions.AcessoNegadoException;
import com.notasfiscais.application.exceptions.DespesaException;
import com.notasfiscais.application.exceptions.RecursoNaoEncontradoException;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.usecase.despesa.*;
import com.notasfiscais.domain.enums.TipoArquivoEnum;
import com.notasfiscais.infrastructure.configuration.jwt.JwtAuthDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@RestController
@RequestMapping("/api/despesas")
public class DespesaController {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final FindDespesaUseCase findDespesaUseCase;
    private final GetDespesaUseCase getDespesaUseCase;
    private final CreateDespesaUseCase createDespesaUseCase;
    private final UpdateDespesaUseCase updateDespesaUseCase;
    private final DeleteDespesaUseCase deleteDespesaUseCase;
    private final DownloadDespesaUseCase downloadDespesaUseCase;
    public DespesaController(FindDespesaUseCase findDespesaUseCase,
                              GetDespesaUseCase getDespesaUseCase,
                              CreateDespesaUseCase createDespesaUseCase,
                              UpdateDespesaUseCase updateDespesaUseCase,
                              DeleteDespesaUseCase deleteDespesaUseCase,
                              DownloadDespesaUseCase downloadDespesaUseCase) {
        this.findDespesaUseCase = findDespesaUseCase;
        this.getDespesaUseCase = getDespesaUseCase;
        this.createDespesaUseCase = createDespesaUseCase;
        this.updateDespesaUseCase = updateDespesaUseCase;
        this.deleteDespesaUseCase = deleteDespesaUseCase;
        this.downloadDespesaUseCase = downloadDespesaUseCase;
    }
    @GetMapping
    @Operation(description = "Listagem de despesas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Despesas buscadas com sucesso."),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    public ResponseEntity<Response<List<DespesaListItemDTO>>> listar(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes) {
        try {
            List<DespesaListItemDTO> lista = findDespesaUseCase.execute(ano, mes);
            return ResponseEntity.ok(new Response<>(lista, "Despesas buscadas com sucesso"));
        } catch (DespesaException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao buscar despesas"));
        }
    }
    @GetMapping("/{id}")
    @Operation(description = "Busca de despesa por ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Despesa buscada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Despesa não encontrada."),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    public ResponseEntity<Response<DespesaDetailDTO>> buscar(@PathVariable Integer id) {
        try {
            DespesaDetailDTO dto = getDespesaUseCase.execute(id);
            return ResponseEntity.ok(new Response<>(dto, "Despesa buscada com sucesso"));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao buscar despesa"));
        }
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Criação de despesa.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Despesa criada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    public ResponseEntity<Response<DespesaDetailDTO>> criar(
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestPart("dtDespesa") String dtDespesa,
            @RequestPart("vlDespesa") String vlDespesa,
            @RequestPart(value = "dsDespesa", required = false) String dsDespesa,
            @AuthenticationPrincipal JwtAuthDetails authDetails) {
        try {
            DespesaDetailDTO dto = createDespesaUseCase.execute(
                    arquivo,
                    LocalDate.parse(dtDespesa, DATE_FMT),
                    new BigDecimal(vlDespesa),
                    dsDespesa,
                    authDetails.getCdPessoa().intValue());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Response<>(dto, "Despesa criada com sucesso"));
        } catch (ValidacaoException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (DespesaException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao criar despesa"));
        }
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Atualização de despesa.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Despesa atualizada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Despesa não encontrada."),
        @ApiResponse(responseCode = "403", description = "Acesso negado."),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    })
    public ResponseEntity<Response<DespesaDetailDTO>> atualizar(
            @PathVariable Integer id,
            @RequestPart(value = "arquivo", required = false) MultipartFile arquivo,
            @RequestPart(value = "dtDespesa", required = false) String dtDespesa,
            @RequestPart(value = "vlDespesa", required = false) String vlDespesa,
            @RequestPart(value = "dsDespesa", required = false) String dsDespesa,
            @AuthenticationPrincipal JwtAuthDetails authDetails) {
        try {
            LocalDate data = (dtDespesa != null && !dtDespesa.isBlank())
                    ? LocalDate.parse(dtDespesa, DATE_FMT) : null;
            BigDecimal valor = (vlDespesa != null && !vlDespesa.isBlank())
                    ? new BigDecimal(vlDespesa) : null;
            DespesaDetailDTO dto = updateDespesaUseCase.execute(
                    id, arquivo, data, valor, dsDespesa,
                    authDetails.getCdPessoa().intValue(), authDetails.getRole());
            return ResponseEntity.ok(new Response<>(dto, "Despesa atualizada com sucesso"));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage()));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (ValidacaoException e) {
            return ResponseEntity.badRequest().body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao atualizar despesa"));
        }
    }
    @DeleteMapping("/{id}")
    @Operation(description = "Exclusão de despesa.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Despesa deletada com sucesso."),
        @ApiResponse(responseCode = "404", description = "Despesa não encontrada."),
        @ApiResponse(responseCode = "403", description = "Acesso negado.")
    })
    public ResponseEntity<Response<Void>> deletar(
            @PathVariable Integer id,
            @AuthenticationPrincipal JwtAuthDetails authDetails) {
        try {
            deleteDespesaUseCase.execute(id, authDetails.getCdPessoa().intValue(), authDetails.getRole());
            return ResponseEntity.noContent().build();
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage()));
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response<>(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>("Erro interno ao deletar despesa"));
        }
    }
    @GetMapping("/{id}/download")
    @Operation(description = "Download do arquivo da despesa.")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Integer id) {
        try {
            DownloadDespesaUseCase.DownloadResult result = downloadDespesaUseCase.execute(id);
            DespesaDetailDTO dto = getDespesaUseCase.execute(id);
            TipoArquivoEnum tipo = TipoArquivoEnum.fromCodigo(dto.getTpArquivo());
            MediaType mediaType = tipo != null
                    ? MediaType.parseMediaType(tipo.getMimeType())
                    : MediaType.APPLICATION_OCTET_STREAM;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(dto.getNmArquivo()).build());
            headers.setContentType(mediaType);
            return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(result.bytes()));
        } catch (RecursoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
