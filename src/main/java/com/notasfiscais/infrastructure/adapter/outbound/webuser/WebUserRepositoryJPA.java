package com.notasfiscais.infrastructure.adapter.outbound.webuser;
import com.notasfiscais.infrastructure.adapter.outbound.webuser.entities.WebUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface WebUserRepositoryJPA extends JpaRepository<WebUserEntity, Integer> {
    @Query("SELECT u FROM WebUserEntity u JOIN FETCH u.pessoa p WHERE p.nmEmail = :email")
    Optional<WebUserEntity> findByPessoaNmEmail(@Param("email") String email);
}
