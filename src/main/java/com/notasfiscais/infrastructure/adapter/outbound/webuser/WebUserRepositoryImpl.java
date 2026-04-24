package com.notasfiscais.infrastructure.adapter.outbound.webuser;
import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IWebUserRepository;
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
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public WebUser getByEmail(String nmEmail) throws Exception {
        Optional<WebUserEntity> entity = webUserRepositoryJPA.findByPessoaNmEmail(nmEmail);
        return entity.map(webUserMapper::entityToDomain).orElse(null);
    }
}
