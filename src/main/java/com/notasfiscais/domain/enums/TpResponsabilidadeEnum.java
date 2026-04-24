package com.notasfiscais.domain.enums;
public enum TpResponsabilidadeEnum {
    ADMIN(0),
    USER(1);
    private final int codigo;
    TpResponsabilidadeEnum(int codigo) { this.codigo = codigo; }
    public int getCodigo() { return codigo; }
}
