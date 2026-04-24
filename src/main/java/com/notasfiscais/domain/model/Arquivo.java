package com.notasfiscais.domain.model;
import java.time.LocalDateTime;
public class Arquivo {
    private Integer cdArquivo;
    private String nmArquivo;
    private LocalDateTime dtArquivo;
    private Integer tpArquivo;
    private String caminhoArquivo;
    public Arquivo(Integer cdArquivo, String nmArquivo, LocalDateTime dtArquivo,
                   Integer tpArquivo, String caminhoArquivo) {
        this.cdArquivo = cdArquivo;
        this.nmArquivo = nmArquivo;
        this.dtArquivo = dtArquivo;
        this.tpArquivo = tpArquivo;
        this.caminhoArquivo = caminhoArquivo;
    }
    public Integer getCdArquivo() { return cdArquivo; }
    public String getNmArquivo() { return nmArquivo; }
    public LocalDateTime getDtArquivo() { return dtArquivo; }
    public Integer getTpArquivo() { return tpArquivo; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
}
