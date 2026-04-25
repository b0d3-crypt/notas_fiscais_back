package com.notasfiscais.inmemory;

import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.repositories.IPessoaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPessoaRepository implements IPessoaRepository {

    private final Map<Integer, Pessoa> store = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public Pessoa save(Pessoa pessoa) {
        Integer id = pessoa.getCdPessoa();
        if (id == null) {
            id = sequence.getAndIncrement();
            pessoa = new Pessoa(id, pessoa.getCdEndereco(), pessoa.getNmPessoa(),
                    pessoa.getNrTelefone(), pessoa.getNrCpf(), pessoa.getNmEmail());
        }
        store.put(id, pessoa);
        return pessoa;
    }

    @Override
    public Pessoa get(Integer cdPessoa) {
        return store.get(cdPessoa);
    }

    @Override
    public Pessoa getByEmail(String nmEmail) {
        return store.values().stream()
                .filter(p -> nmEmail.equals(p.getNmEmail()))
                .findFirst()
                .orElse(null);
    }
}

