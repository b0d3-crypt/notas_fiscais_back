package com.notasfiscais.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "web_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_web_user")
    private Integer cdWebUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_pessoa", nullable = false)
    private Pessoa pessoa;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "tp_responsabilidade", nullable = false)
    private Integer tpResponsabilidade;
}
