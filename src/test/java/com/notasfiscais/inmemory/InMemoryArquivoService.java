package com.notasfiscais.inmemory;

import com.notasfiscais.application.port.in.IArquivoService;
import com.notasfiscais.domain.model.Arquivo;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory implementation of IArquivoService for integration tests.
 * Stores file bytes in memory and delegates metadata to InMemoryArquivoRepository.
 */
public class InMemoryArquivoService implements IArquivoService {

    private final InMemoryArquivoRepository arquivoRepository;
    private final Map<Integer, byte[]> bytesStore = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    public InMemoryArquivoService(InMemoryArquivoRepository arquivoRepository) {
        this.arquivoRepository = arquivoRepository;
    }

    @Override
    public Arquivo salvar(MultipartFile file) throws Exception {
        int id = sequence.getAndIncrement();
        byte[] bytes = file.getBytes();
        bytesStore.put(id, bytes);
        Arquivo arq = new Arquivo(id, file.getOriginalFilename(), LocalDateTime.now(), 1, "inmemory/" + id);
        return arquivoRepository.save(arq);
    }

    @Override
    public Arquivo substituir(Integer cdArquivo, MultipartFile novoFile) throws Exception {
        byte[] bytes = novoFile.getBytes();
        bytesStore.put(cdArquivo, bytes);
        Arquivo existing = arquivoRepository.get(cdArquivo);
        Arquivo updated = new Arquivo(cdArquivo,
                novoFile.getOriginalFilename() != null ? novoFile.getOriginalFilename() : existing.getNmArquivo(),
                LocalDateTime.now(), existing.getTpArquivo(), existing.getCaminhoArquivo());
        return arquivoRepository.save(updated);
    }

    @Override
    public byte[] lerBytes(Integer cdArquivo) throws Exception {
        byte[] bytes = bytesStore.get(cdArquivo);
        if (bytes == null) throw new RuntimeException("Arquivo não encontrado: " + cdArquivo);
        return bytes;
    }

    @Override
    public void deletar(Integer cdArquivo) throws Exception {
        bytesStore.remove(cdArquivo);
        arquivoRepository.delete(cdArquivo);
    }
}

