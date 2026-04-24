package com.notasfiscais.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "descricao_despesa")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescricaoDespesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_descricao_despesa")
    private Integer cdDescricaoDespesa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_arquivo", nullable = false)
    private Arquivo arquivo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "ds_despesa")
    private String dsDespesa;

    @Column(name = "dt_despesa", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDate dtDespesa;

    @Column(name = "vl_despesa", nullable = false, precision = 10, scale = 2)
    private BigDecimal vlDespesa;
}
