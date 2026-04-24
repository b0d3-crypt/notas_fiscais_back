package com.notasfiscais.service;

import com.notasfiscais.entity.Arquivo;
import com.notasfiscais.enums.TipoArquivo;
import com.notasfiscais.exception.GlobalExceptionHandler.ValidacaoException;
import com.notasfiscais.repository.ArquivoRepository;
import com.notasfiscais.util.FileStorageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ArquivoService {

    private static final long MAX_SIZE = 2L * 1024 * 1024;

    private final ArquivoRepository arquivoRepository;
    private final FileStorageUtil fileStorageUtil;

    public ArquivoService(ArquivoRepository arquivoRepository, FileStorageUtil fileStorageUtil) {
        this.arquivoRepository = arquivoRepository;
        this.fileStorageUtil = fileStorageUtil;
    }

    public Arquivo salvar(MultipartFile file) {
        validarArquivo(file);

        TipoArquivo tipo = TipoArquivo.fromMimeType(file.getContentType());
        String relativePath;
        try {
            relativePath = fileStorageUtil.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }

        Arquivo arquivo = Arquivo.builder()
                .nmArquivo(file.getOriginalFilename())
                .dtArquivo(LocalDateTime.now())
                .tpArquivo(tipo.getCodigo())
                .caminhoArquivo(relativePath)
                .build();

        return arquivoRepository.save(arquivo);
    }

    public void substituir(Arquivo arquivo, MultipartFile novoFile) {
        validarArquivo(novoFile);

        TipoArquivo tipo = TipoArquivo.fromMimeType(novoFile.getContentType());

        try {
            fileStorageUtil.delete(arquivo.getCaminhoArquivo());
            String relativePath = fileStorageUtil.save(novoFile);
            arquivo.setNmArquivo(novoFile.getOriginalFilename());
            arquivo.setDtArquivo(LocalDateTime.now());
            arquivo.setTpArquivo(tipo.getCodigo());
            arquivo.setCaminhoArquivo(relativePath);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao substituir arquivo: " + e.getMessage());
        }

        arquivoRepository.save(arquivo);
    }

    public byte[] lerBytes(String caminhoArquivo) {
        try {
            return fileStorageUtil.read(caminhoArquivo);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    public void deletar(Arquivo arquivo) {
        try {
            fileStorageUtil.delete(arquivo.getCaminhoArquivo());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar arquivo: " + e.getMessage());
        }
        arquivoRepository.delete(arquivo);
    }

    private void validarArquivo(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new ValidacaoException("Arquivo excede o limite de 2MB");
        }
        if (TipoArquivo.fromMimeType(file.getContentType()) == null) {
            throw new ValidacaoException("Tipo de arquivo não permitido");
        }
    }
}
