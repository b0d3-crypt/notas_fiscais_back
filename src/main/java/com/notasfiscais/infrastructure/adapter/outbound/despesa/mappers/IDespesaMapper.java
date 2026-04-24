package com.notasfiscais.infrastructure.adapter.outbound.despesa.mappers;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.infrastructure.adapter.outbound.despesa.entities.DescricaoDespesaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface IDespesaMapper {
    @Mapping(source = "arquivo.cdArquivo", target = "cdArquivo")
    @Mapping(source = "pessoa.cdPessoa", target = "cdPessoa")
    DescricaoDespesa entityToDomain(DescricaoDespesaEntity entity);
}
