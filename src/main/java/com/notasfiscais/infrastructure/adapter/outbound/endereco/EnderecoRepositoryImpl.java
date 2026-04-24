package com.notasfiscais.infrastructure.adapter.outbound.endereco;
import com.notasfiscais.domain.model.Endereco;
import com.notasfiscais.domain.repositories.IEnderecoRepository;
import com.notasfiscais.infrastructure.adapter.outbound.endereco.entities.EnderecoEntity;
import com.notasfiscais.infrastructure.adapter.outbound.endereco.mappers.EnderecoMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public class EnderecoRepositoryImpl implements IEnderecoRepository {
    private final EnderecoRepositoryJPA enderecoRepositoryJPA;
    private final EnderecoMapper enderecoMapper;
    public EnderecoRepositoryImpl(EnderecoRepositoryJPA enderecoRepositoryJPA, EnderecoMapper enderecoMapper) {
        this.enderecoRepositoryJPA = enderecoRepositoryJPA;
        this.enderecoMapper = enderecoMapper;
    }
    @Override
    public Endereco save(Endereco endereco) throws Exception {
        EnderecoEntity entity = enderecoMapper.domainToEntity(endereco);
        entity = enderecoRepositoryJPA.save(entity);
        return enderecoMapper.entityToDomain(entity);
    }
    @Override
    public Endereco get(Integer cdEndereco) throws Exception {
        Optional<EnderecoEntity> entity = enderecoRepositoryJPA.findById(cdEndereco);
        return entity.map(enderecoMapper::entityToDomain).orElse(null);
    }
}
