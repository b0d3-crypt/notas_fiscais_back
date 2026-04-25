package com.notasfiscais.service;

import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.port.in.IFileStorageService;
import com.notasfiscais.domain.enums.TipoArquivoEnum;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.repositories.IArquivoRepository;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.ArquivoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArquivoServiceImpl - Testes Unitários")
class ArquivoServiceImplTest {

    @Mock private IArquivoRepository arquivoRepository;
    @Mock private IFileStorageService fileStorageService;

    @InjectMocks
    private ArquivoServiceImpl arquivoService;

    private MockMultipartFile pdfValido;
    private MockMultipartFile arquivoGrande;

    @BeforeEach
    void setUp() {
        pdfValido = new MockMultipartFile("file", "nota.pdf", "application/pdf", "conteudo pdf".getBytes());
        // 3MB - excede o limite de 2MB
        arquivoGrande = new MockMultipartFile("file", "grande.pdf", "application/pdf", new byte[3 * 1024 * 1024]);
    }

    @Test
    @DisplayName("Deve salvar arquivo PDF com sucesso")
    void deveSalvarArquivoPdfComSucesso() throws Exception {
        String caminho = "2024/01/uuid_nota.pdf";
        Arquivo arquivoSalvo = new Arquivo(1, "nota.pdf", LocalDateTime.now(), TipoArquivoEnum.PDF.getCodigo(), caminho);

        when(fileStorageService.save(pdfValido)).thenReturn(caminho);
        when(arquivoRepository.save(any(Arquivo.class))).thenReturn(arquivoSalvo);

        Arquivo resultado = arquivoService.salvar(pdfValido);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCdArquivo()).isEqualTo(1);
        assertThat(resultado.getNmArquivo()).isEqualTo("nota.pdf");
        verify(fileStorageService).save(pdfValido);
        verify(arquivoRepository).save(any(Arquivo.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando arquivo excede 2MB")
    void deveLancarExcecaoQuandoArquivoExcedeTamanho() {
        assertThatThrownBy(() -> arquivoService.salvar(arquivoGrande))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("2MB");

        verifyNoInteractions(fileStorageService);
        verifyNoInteractions(arquivoRepository);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando tipo de arquivo não é permitido")
    void deveLancarExcecaoQuandoTipoNaoPermitido() {
        MockMultipartFile txtFile = new MockMultipartFile("file", "doc.txt", "text/plain", "texto".getBytes());

        assertThatThrownBy(() -> arquivoService.salvar(txtFile))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Tipo de arquivo não permitido");
    }

    @Test
    @DisplayName("Deve retornar bytes do arquivo quando existir")
    void deveRetornarBytesDoArquivo() throws Exception {
        Arquivo arquivo = new Arquivo(1, "nota.pdf", LocalDateTime.now(), 1, "2024/01/nota.pdf");
        byte[] bytes = "pdf content".getBytes();

        when(arquivoRepository.get(1)).thenReturn(arquivo);
        when(fileStorageService.read("2024/01/nota.pdf")).thenReturn(bytes);

        byte[] resultado = arquivoService.lerBytes(1);

        assertThat(resultado).isEqualTo(bytes);
    }

    @Test
    @DisplayName("Deve lançar exceção ao ler arquivo inexistente")
    void deveLancarExcecaoAoLerArquivoInexistente() throws Exception {
        when(arquivoRepository.get(99)).thenReturn(null);

        assertThatThrownBy(() -> arquivoService.lerBytes(99))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Arquivo não encontrado");
    }

    @Test
    @DisplayName("Deve deletar arquivo e remover do storage")
    void deveDeletarArquivo() throws Exception {
        Arquivo arquivo = new Arquivo(1, "nota.pdf", LocalDateTime.now(), 1, "2024/01/nota.pdf");
        when(arquivoRepository.get(1)).thenReturn(arquivo);

        arquivoService.deletar(1);

        verify(fileStorageService).delete("2024/01/nota.pdf");
        verify(arquivoRepository).delete(1);
    }

    @Test
    @DisplayName("Deve ignorar deletar quando arquivo não existe")
    void deveIgnorarDeletarArquivoInexistente() throws Exception {
        when(arquivoRepository.get(99)).thenReturn(null);

        assertThatCode(() -> arquivoService.deletar(99)).doesNotThrowAnyException();

        verify(fileStorageService, never()).delete(any());
    }

    @Test
    @DisplayName("Deve substituir arquivo com sucesso")
    void deveSubstituirArquivoComSucesso() throws Exception {
        Arquivo arquivoExistente = new Arquivo(1, "antigo.pdf", LocalDateTime.now(), 1, "2024/01/antigo.pdf");
        String novoCaminho = "2024/01/novo_uuid.pdf";
        Arquivo arquivoAtualizado = new Arquivo(1, "nota.pdf", LocalDateTime.now(), 1, novoCaminho);

        when(arquivoRepository.get(1)).thenReturn(arquivoExistente);
        when(fileStorageService.save(pdfValido)).thenReturn(novoCaminho);
        when(arquivoRepository.save(any(Arquivo.class))).thenReturn(arquivoAtualizado);

        Arquivo resultado = arquivoService.substituir(1, pdfValido);

        assertThat(resultado).isNotNull();
        verify(fileStorageService).delete("2024/01/antigo.pdf");
        verify(fileStorageService).save(pdfValido);
    }
}

