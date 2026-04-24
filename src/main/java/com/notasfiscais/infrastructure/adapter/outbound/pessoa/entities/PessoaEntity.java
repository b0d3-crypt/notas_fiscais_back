package com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities;
import com.notasfiscais.infrastructure.adapter.outbound.endereco.entities.EnderecoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "pessoa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_pessoa")
    private Integer cdPessoa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_endereco", nullable = false)
    private EnderecoEntity endereco;
    @Column(name = "nm_pessoa")
    private String nmPessoa;
    @Column(name = "nr_telefone")
    private String nrTelefone;
    @Column(name = "nr_cpf")
    private String nrCpf;
    @Column(name = "nm_email", unique = true)
    private String nmEmail;
}
