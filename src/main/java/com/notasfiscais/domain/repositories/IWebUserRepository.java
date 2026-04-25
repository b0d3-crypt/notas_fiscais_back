package com.notasfiscais.domain.repositories;
import com.notasfiscais.domain.model.WebUser;
public interface IWebUserRepository {
    WebUser save(WebUser webUser) throws Exception;
    WebUser getByEmail(String nmEmail) throws Exception;
    WebUser getById(Integer cdWebUser) throws Exception;
}
