package com.notasfiscais.inmemory;

import com.notasfiscais.domain.model.WebUser;
import com.notasfiscais.domain.repositories.IWebUserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryWebUserRepository implements IWebUserRepository {

    private final Map<Integer, WebUser> store = new HashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public WebUser save(WebUser webUser) {
        Integer id = webUser.getCdWebUser();
        if (id == null) {
            id = sequence.getAndIncrement();
            webUser = new WebUser(id, webUser.getCdPessoa(), webUser.getPassword(), webUser.getTpResponsabilidade());
        }
        store.put(id, webUser);
        return webUser;
    }

    @Override
    public WebUser getByEmail(String nmEmail) {
        // WebUser does not hold email; this lookup requires matching via cdPessoa and external context.
        // For test purposes, we store a helper map by email.
        return emailIndex.get(nmEmail);
    }

    /** Helper: register user with email for lookup. */
    public WebUser saveWithEmail(WebUser webUser, String email) {
        WebUser saved = save(webUser);
        emailIndex.put(email, saved);
        return saved;
    }

    @Override
    public WebUser getById(Integer cdWebUser) {
        return store.get(cdWebUser);
    }

    private final Map<String, WebUser> emailIndex = new HashMap<>();
}

