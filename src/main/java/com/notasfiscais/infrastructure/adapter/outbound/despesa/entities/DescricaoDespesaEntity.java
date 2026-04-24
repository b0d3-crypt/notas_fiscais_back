package com.notasfiscais.infrastructure.adapter.outbound.despesa.entities;
import com.notasfiscais.infrastructure.adapter.outbound.arquivo.entities.ArquivoEntity;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "descricao_despesa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescricaoDespesaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_descricao_despesa")
    private Integer cdDescricaoDespesa;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_arquivo", nullable = false)
    private ArquivoEntity arquivo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_pessoa", nullable = false)
    private PessoaEntity pessoa;
    @Column(name = "ds_despesa")
    private String dsDespesa;
    @Column(name = "dt_despesa", nullable = false)
    private LocalDate dtDespesa;
    @Column(name = "vl_despesa", nullable = false, precision = 10, scale = 2)
    private BigDecimal vlDespesa;
}
