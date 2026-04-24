package com.notasfiscais.infrastructure.adapter.outbound.pessoa;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface PessoaRepositoryJPA extends JpaRepository<PessoaEntity, Integer> {
    Optional<PessoaEntity> findByNmEmail(String nmEmail);
}
