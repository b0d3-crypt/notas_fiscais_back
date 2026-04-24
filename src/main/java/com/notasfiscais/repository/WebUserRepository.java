package com.notasfiscais.repository;

import com.notasfiscais.entity.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebUserRepository extends JpaRepository<WebUser, Integer> {

    @Query("SELECT u FROM WebUser u JOIN FETCH u.pessoa p WHERE p.nmEmail = :email")
    Optional<WebUser> findByPessoaNmEmail(@Param("email") String email);
}
