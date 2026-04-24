package com.notasfiscais.infrastructure.adapter.outbound.despesa;
import com.notasfiscais.infrastructure.adapter.outbound.despesa.entities.DescricaoDespesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DespesaRepositoryJPA extends JpaRepository<DescricaoDespesaEntity, Integer> {
}
