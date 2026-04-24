package com.notasfiscais.enums;

public enum TpResponsabilidade {

    ADMIN(0),
    USER(1);

    private final int codigo;

    TpResponsabilidade(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
