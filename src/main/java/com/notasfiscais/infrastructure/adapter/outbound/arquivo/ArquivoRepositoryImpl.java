package com.notasfiscais.infrastructure.adapter.outbound.arquivo;
import com.notasfiscais.domain.model.Arquivo;
import com.notasfiscais.domain.repositories.IArquivoRepository;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.entities.ArquivoEntity;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.mappers.ArquivoMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public class ArquivoRepositoryImpl implements IArquivoRepository {
    private final ArquivoRepositoryJPA arquivoRepositoryJPA;
    private final ArquivoMapper arquivoMapper;
    public ArquivoRepositoryImpl(ArquivoRepositoryJPA arquivoRepositoryJPA, ArquivoMapper arquivoMapper) {
        this.arquivoRepositoryJPA = arquivoRepositoryJPA;
        this.arquivoMapper = arquivoMapper;
    }
    @Override
    public Arquivo save(Arquivo arquivo) throws Exception {
        ArquivoEntity entity = arquivoMapper.domainToEntity(arquivo);
        entity = arquivoRepositoryJPA.save(entity);
        return arquivoMapper.entityToDomain(entity);
    }
    @Override
    public Arquivo get(Integer cdArquivo) throws Exception {
        Optional<ArquivoEntity> entity = arquivoRepositoryJPA.findById(cdArquivo);
        return entity.map(arquivoMapper::entityToDomain).orElse(null);
    }
    @Override
    public void delete(Integer cdArquivo) throws Exception {
        arquivoRepositoryJPA.deleteById(cdArquivo);
    }
}
