package com.notasfiscais.inmemory;

import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryDespesaRepository implements IDespesaRepository {

    private final Map<Integer, DescricaoDespesa> store = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public DescricaoDespesa save(DescricaoDespesa despesa) {
        Integer id = despesa.getCdDescricaoDespesa();
        if (id == null) {
            id = sequence.getAndIncrement();
            despesa = new DescricaoDespesa(id, despesa.getCdArquivo(), despesa.getCdPessoa(),
                    despesa.getDsDespesa(), despesa.getDtDespesa(), despesa.getVlDespesa());
        }
        store.put(id, despesa);
        return despesa;
    }

    @Override
    public DescricaoDespesa get(Integer cdDespesa) {
        return store.get(cdDespesa);
    }

    @Override
    public void delete(Integer cdDespesa) {
        store.remove(cdDespesa);
    }

    public int size() {
        return store.size();
    }

    public java.util.Collection<DescricaoDespesa> getAll() {
        return store.values();
    }
}


