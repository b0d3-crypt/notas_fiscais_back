package com.notasfiscais.infrastructure.adapter.outbound.pessoa.mappers;
import com.notasfiscais.domain.model.Pessoa;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface PessoaMapper {
    @Mapping(source = "endereco.cdEndereco", target = "cdEndereco")
    Pessoa entityToDomain(PessoaEntity entity);
}
