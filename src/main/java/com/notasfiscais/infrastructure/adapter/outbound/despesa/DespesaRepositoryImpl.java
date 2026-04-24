package com.notasfiscais.infrastructure.adapter.outbound.despesa;
import com.notasfiscais.domain.model.DescricaoDespesa;
import com.notasfiscais.domain.repositories.IDespesaRepository;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.ArquivoRepositoryJPA;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.entities.ArquivoEntity;
import com.notasfiscais.infrastructure.adapter.outbound.despesa.entities.DescricaoDespesaEntity;
import com.notasfiscais.infrastructure.adapter.outbound.despesa.mappers.IDespesaMapper;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.PessoaRepositoryJPA;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public class DespesaRepositoryImpl implements IDespesaRepository {
    private final DespesaRepositoryJPA despesaRepositoryJPA;
    private final ArquivoRepositoryJPA arquivoRepositoryJPA;
    private final PessoaRepositoryJPA pessoaRepositoryJPA;
    private final IDespesaMapper despesaMapper;
    public DespesaRepositoryImpl(DespesaRepositoryJPA despesaRepositoryJPA,
                                  ArquivoRepositoryJPA arquivoRepositoryJPA,
                                  PessoaRepositoryJPA pessoaRepositoryJPA,
                                  IDespesaMapper despesaMapper) {
        this.despesaRepositoryJPA = despesaRepositoryJPA;
        this.arquivoRepositoryJPA = arquivoRepositoryJPA;
        this.pessoaRepositoryJPA = pessoaRepositoryJPA;
        this.despesaMapper = despesaMapper;
    }
    @Override
    public DescricaoDespesa save(DescricaoDespesa despesa) throws Exception {
        ArquivoEntity arquivo = arquivoRepositoryJPA.findById(despesa.getCdArquivo())
                .orElseThrow(() -> new Exception("Arquivo não encontrado"));
        PessoaEntity pessoa = pessoaRepositoryJPA.findById(despesa.getCdPessoa())
                .orElseThrow(() -> new Exception("Pessoa não encontrada"));
        DescricaoDespesaEntity entity = new DescricaoDespesaEntity(
                despesa.getCdDescricaoDespesa(),
                arquivo,
                pessoa,
                despesa.getDsDespesa(),
                despesa.getDtDespesa(),
                despesa.getVlDespesa());
        entity = despesaRepositoryJPA.save(entity);
        return despesaMapper.entityToDomain(entity);
    }
    @Override
    public DescricaoDespesa get(Integer cdDespesa) throws Exception {
        Optional<DescricaoDespesaEntity> entity = despesaRepositoryJPA.findById(cdDespesa);
        return entity.map(despesaMapper::entityToDomain).orElse(null);
    }
    @Override
    public void delete(Integer cdDespesa) throws Exception {
        despesaRepositoryJPA.deleteById(cdDespesa);
    }
}
