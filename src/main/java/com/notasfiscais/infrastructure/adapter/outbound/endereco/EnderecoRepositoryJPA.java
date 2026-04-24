package com.notasfiscais.infrastructure.adapter.outbound.endereco;
import com.notasfiscais.infrastructure.adapter.outbound.endereco.entities.EnderecoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EnderecoRepositoryJPA extends JpaRepository<EnderecoEntity, Integer> {
}
