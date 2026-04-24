package com.notasfiscais.enums;

public enum TipoArquivo {

    PDF(1, "PDF", "application/pdf"),
    JPEG(2, "JPEG", "image/jpeg"),
    PNG(3, "PNG", "image/png"),
    GIF(4, "GIF", "image/gif");

    private final int codigo;
    private final String label;
    private final String mimeType;

    TipoArquivo(int codigo, String label, String mimeType) {
        this.codigo = codigo;
        this.label = label;
        this.mimeType = mimeType;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getLabel() {
        return label;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static TipoArquivo fromMimeType(String mimeType) {
        for (TipoArquivo tipo : values()) {
            if (tipo.getMimeType().equalsIgnoreCase(mimeType)) {
                return tipo;
            }
        }
        return null;
    }

    public static TipoArquivo fromCodigo(int codigo) {
        for (TipoArquivo tipo : values()) {
            if (tipo.getCodigo() == codigo) {
                return tipo;
            }
        }
        return null;
    }
}
