package com.notasfiscais.repository;

import com.notasfiscais.entity.DescricaoDespesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DespesaRepository extends JpaRepository<DescricaoDespesa, Integer> {

    @Query("SELECT d FROM DescricaoDespesa d JOIN FETCH d.arquivo a JOIN FETCH d.pessoa p " +
            "WHERE (:ano IS NULL OR YEAR(d.dtDespesa) = :ano) " +
            "AND (:mes IS NULL OR MONTH(d.dtDespesa) = :mes) " +
            "ORDER BY d.dtDespesa DESC")
    List<DescricaoDespesa> findAllWithFilters(@Param("ano") Integer ano, @Param("mes") Integer mes);
}
