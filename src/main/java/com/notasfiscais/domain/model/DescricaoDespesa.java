package com.notasfiscais.domain.model;
import java.math.BigDecimal;
import java.time.LocalDate;
public class DescricaoDespesa {
    private Integer cdDescricaoDespesa;
    private Integer cdArquivo;
    private Integer cdPessoa;
    private String dsDespesa;
    private LocalDate dtDespesa;
    private BigDecimal vlDespesa;
    public DescricaoDespesa(Integer cdDescricaoDespesa, Integer cdArquivo, Integer cdPessoa,
                            String dsDespesa, LocalDate dtDespesa, BigDecimal vlDespesa) {
        this.cdDescricaoDespesa = cdDescricaoDespesa;
        this.cdArquivo = cdArquivo;
        this.cdPessoa = cdPessoa;
        this.dsDespesa = dsDespesa;
        this.dtDespesa = dtDespesa;
        this.vlDespesa = vlDespesa;
    }
    public Integer getCdDescricaoDespesa() { return cdDescricaoDespesa; }
    public Integer getCdArquivo() { return cdArquivo; }
    public Integer getCdPessoa() { return cdPessoa; }
    public String getDsDespesa() { return dsDespesa; }
    public LocalDate getDtDespesa() { return dtDespesa; }
    public BigDecimal getVlDespesa() { return vlDespesa; }
}
