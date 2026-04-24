package com.notasfiscais.infrastructure.adapter.outbound.arquivo;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.entities.ArquivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ArquivoRepositoryJPA extends JpaRepository<ArquivoEntity, Integer> {
}
