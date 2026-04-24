package com.notasfiscais.infrastructure.adapter.outbound.webuser.entities;
import com.notasfiscais.infrastructure.adapter.outbound.pessoa.entities.PessoaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "web_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_web_user")
    private Integer cdWebUser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_pessoa", nullable = false)
    private PessoaEntity pessoa;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "tp_responsabilidade", nullable = false)
    private Integer tpResponsabilidade;
}
