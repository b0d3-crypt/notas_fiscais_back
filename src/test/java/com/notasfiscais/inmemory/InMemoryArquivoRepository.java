package com.notasfiscais.inmemory;

import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.repositories.IArquivoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryArquivoRepository implements IArquivoRepository {

    private final Map<Integer, Arquivo> store = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public Arquivo save(Arquivo arquivo) {
        Integer id = arquivo.getCdArquivo();
        if (id == null) {
            id = sequence.getAndIncrement();
            arquivo = new Arquivo(id, arquivo.getNmArquivo(), arquivo.getDtArquivo(),
                    arquivo.getTpArquivo(), arquivo.getCaminhoArquivo());
        }
        store.put(id, arquivo);
        return arquivo;
    }

    @Override
    public Arquivo get(Integer cdArquivo) {
        return store.get(cdArquivo);
    }

    @Override
    public void delete(Integer cdArquivo) {
        store.remove(cdArquivo);
    }

    public int size() {
        return store.size();
    }
}

