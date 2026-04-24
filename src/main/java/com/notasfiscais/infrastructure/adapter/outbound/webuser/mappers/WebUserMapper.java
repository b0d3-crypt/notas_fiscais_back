package com.notasfiscais.infrastructure.adapter.outbound.webuser.mappers;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.infrastructure.adapter.outbound.webuser.entities.WebUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface WebUserMapper {
    @Mapping(source = "pessoa.cdPessoa", target = "cdPessoa")
    WebUser entityToDomain(WebUserEntity entity);
}
