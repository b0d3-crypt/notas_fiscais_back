package com.notasfiscais.infrastructure.adapter.outbound.webuser;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IWebUserRepository;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import com.notasfiscais.infrastructure.adapter.outbound.webuser.entities.WebUserEntity;
import com.notasfiscais.infrastructure.adapter.outbound.webuser.mappers.WebUserMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public class WebUserRepositoryImpl implements IWebUserRepository {
    private final WebUserRepositoryJPA webUserRepositoryJPA;
    private final WebUserMapper webUserMapper;
    public WebUserRepositoryImpl(WebUserRepositoryJPA webUserRepositoryJPA, WebUserMapper webUserMapper) {
        this.webUserRepositoryJPA = webUserRepositoryJPA;
        this.webUserMapper = webUserMapper;
    }
    @Override
    public WebUser save(WebUser webUser) throws Exception {
        WebUserEntity entity;
        if (webUser.getCdWebUser() != null) {
            entity = webUserRepositoryJPA.findById(webUser.getCdWebUser())
                    .orElse(new WebUserEntity());
        } else {
            entity = new WebUserEntity();
        }
        entity.setCdWebUser(webUser.getCdWebUser());
        entity.setPassword(webUser.getPassword());
        entity.setTpResponsabilidade(webUser.getTpResponsabilidade());
        PessoaEntity pessoaRef = new PessoaEntity();
        pessoaRef.setCdPessoa(webUser.getCdPessoa());
        entity.setPessoa(pessoaRef);
        entity = webUserRepositoryJPA.save(entity);
        return webUserMapper.entityToDomain(entity);
    }
    @Override
    public WebUser getByEmail(String nmEmail) throws Exception {
        Optional<WebUserEntity> entity = webUserRepositoryJPA.findByPessoaNmEmail(nmEmail);
        return entity.map(webUserMapper::entityToDomain).orElse(null);
    }
    @Override
    public WebUser getById(Integer cdWebUser) throws Exception {
        Optional<WebUserEntity> entity = webUserRepositoryJPA.findById(cdWebUser);
        return entity.map(webUserMapper::entityToDomain).orElse(null);
    }
}
