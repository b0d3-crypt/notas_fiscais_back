package com.notasfiscais.infrastructure.adapter.outbound.pessoa;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.domain.repositories.IPessoaRepository;
import com.notasfiscais.infrastructure.adapter.outbound.endereco.entities.EnderecoEntity;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.mappers.PessoaMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public class PessoaRepositoryImpl implements IPessoaRepository {
    private final PessoaRepositoryJPA pessoaRepositoryJPA;
    private final PessoaMapper pessoaMapper;
    public PessoaRepositoryImpl(PessoaRepositoryJPA pessoaRepositoryJPA, PessoaMapper pessoaMapper) {
        this.pessoaRepositoryJPA = pessoaRepositoryJPA;
        this.pessoaMapper = pessoaMapper;
    }
    @Override
    public Pessoa save(Pessoa pessoa) throws Exception {
        PessoaEntity entity;
        if (pessoa.getCdPessoa() != null) {
            entity = pessoaRepositoryJPA.findById(pessoa.getCdPessoa())
                    .orElse(new PessoaEntity());
        } else {
            entity = new PessoaEntity();
        }
        entity.setCdPessoa(pessoa.getCdPessoa());
        entity.setNmPessoa(pessoa.getNmPessoa());
        entity.setNrTelefone(pessoa.getNrTelefone());
        entity.setNrCpf(pessoa.getNrCpf());
        entity.setNmEmail(pessoa.getNmEmail());
        // Mantém o endereço existente se não for fornecido um novo cdEndereco
        if (pessoa.getCdEndereco() != null) {
            EnderecoEntity enderecoRef = new EnderecoEntity();
            enderecoRef.setCdEndereco(pessoa.getCdEndereco());
            entity.setEndereco(enderecoRef);
        }
        entity = pessoaRepositoryJPA.save(entity);
        return pessoaMapper.entityToDomain(entity);
    }
    @Override
    public Pessoa get(Integer cdPessoa) throws Exception {
        Optional<PessoaEntity> entity = pessoaRepositoryJPA.findById(cdPessoa);
        return entity.map(pessoaMapper::entityToDomain).orElse(null);
    }
    @Override
    public Pessoa getByEmail(String nmEmail) throws Exception {
        Optional<PessoaEntity> entity = pessoaRepositoryJPA.findByNmEmail(nmEmail);
        return entity.map(pessoaMapper::entityToDomain).orElse(null);
    }
}
