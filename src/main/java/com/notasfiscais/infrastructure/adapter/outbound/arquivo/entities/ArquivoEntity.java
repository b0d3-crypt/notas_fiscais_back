package com.notasfiscais.infrastructure.adapter.outbound.arquivo.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name = "arquivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_arquivo")
    private Integer cdArquivo;
    @Column(name = "nm_arquivo", nullable = false)
    private String nmArquivo;
    @Column(name = "dt_arquivo", nullable = false)
    private LocalDateTime dtArquivo;
    @Column(name = "tp_arquivo", nullable = false)
    private Integer tpArquivo;
    @Column(name = "caminho_arquivo", nullable = false)
    private String caminhoArquivo;
}
