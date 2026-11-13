package br.edu.ifba.noisehunter.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tipo_som", length = 50)
    private String tipoSom;

    @Column(name = "decibeis", precision = 5, scale = 2)
    private BigDecimal decibeis;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "localizacao", length = 100)
    private String localizacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dispositivo_id")
    private Dispositivo dispositivo;

    public Integer getId() { return id; }
    public String getTipoSom() { return tipoSom; }
    public void setTipoSom(String tipoSom) { this.tipoSom = tipoSom; }
    public BigDecimal getDecibeis() { return decibeis; }
    public void setDecibeis(BigDecimal decibeis) { this.decibeis = decibeis; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public Dispositivo getDispositivo() { return dispositivo; }
    public void setDispositivo(Dispositivo dispositivo) { this.dispositivo = dispositivo; }
}
