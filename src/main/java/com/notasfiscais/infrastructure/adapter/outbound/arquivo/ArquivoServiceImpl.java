package com.notasfiscais.infrastructure.adapter.outbound.arquivo;
import com.notasfiscais.application.exceptions.ValidacaoException;
import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.application.port.in.IFileStorageService;
import com.notasfiscais.domain.enums.TipoArquivoEnum;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.repositories.IArquivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
@Service
public class ArquivoServiceImpl implements IArquivoService {
    private static final long MAX_SIZE = 2L * 1024 * 1024;
    private final IArquivoRepository arquivoRepository;
    private final IFileStorageService fileStorageService;
    public ArquivoServiceImpl(IArquivoRepository arquivoRepository, IFileStorageService fileStorageService) {
        this.arquivoRepository = arquivoRepository;
        this.fileStorageService = fileStorageService;
    }
    @Override
    public Arquivo salvar(MultipartFile file) throws Exception {
        validarArquivo(file);
        String relativePath = fileStorageService.save(file);
        TipoArquivoEnum tipo = TipoArquivoEnum.fromMimeType(file.getContentType());
        Arquivo arquivo = new Arquivo(null, file.getOriginalFilename(), LocalDateTime.now(),
                tipo.getCodigo(), relativePath);
        return arquivoRepository.save(arquivo);
    }
    @Override
    public Arquivo substituir(Integer cdArquivo, MultipartFile novoFile) throws Exception {
        validarArquivo(novoFile);
        Arquivo arquivo = arquivoRepository.get(cdArquivo);
        if (arquivo == null) throw new Exception("Arquivo não encontrado");
        fileStorageService.delete(arquivo.getCaminhoArquivo());
        String relativePath = fileStorageService.save(novoFile);
        TipoArquivoEnum tipo = TipoArquivoEnum.fromMimeType(novoFile.getContentType());
        Arquivo atualizado = new Arquivo(cdArquivo, novoFile.getOriginalFilename(),
                LocalDateTime.now(), tipo.getCodigo(), relativePath);
        return arquivoRepository.save(atualizado);
    }
    @Override
    public byte[] lerBytes(Integer cdArquivo) throws Exception {
        Arquivo arquivo = arquivoRepository.get(cdArquivo);
        if (arquivo == null) throw new Exception("Arquivo não encontrado");
        return fileStorageService.read(arquivo.getCaminhoArquivo());
    }
    @Override
    public void deletar(Integer cdArquivo) throws Exception {
        Arquivo arquivo = arquivoRepository.get(cdArquivo);
        if (arquivo != null) {
            fileStorageService.delete(arquivo.getCaminhoArquivo());
            arquivoRepository.delete(cdArquivo);
        }
    }
    private void validarArquivo(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new ValidacaoException("Arquivo excede o limite de 2MB");
        }
        if (TipoArquivoEnum.fromMimeType(file.getContentType()) == null) {
            throw new ValidacaoException("Tipo de arquivo não permitido");
        }
    }
}
