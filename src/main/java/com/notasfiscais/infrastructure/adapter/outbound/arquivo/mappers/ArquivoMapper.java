package com.notasfiscais.infrastructure.adapter.outbound.arquivo.mappers;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.entities.ArquivoEntity;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface ArquivoMapper {
    ArquivoEntity domainToEntity(Arquivo arquivo);
    Arquivo entityToDomain(ArquivoEntity entity);
}
