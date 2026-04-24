package com.notasfiscais.infrastructure.adapter.outbound.endereco.mappers;
import com.notasfiscais.domain.model.Endereco;
import com.notasfiscais.infrastructure.adapter.outbound.endereco.entities.EnderecoEntity;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface EnderecoMapper {
    EnderecoEntity domainToEntity(Endereco endereco);
    Endereco entityToDomain(EnderecoEntity entity);
}
