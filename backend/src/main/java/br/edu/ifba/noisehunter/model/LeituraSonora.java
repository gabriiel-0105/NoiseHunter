package br.edu.ifba.noisehunter.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Leitura bruta do ADC. Amplitude_pp NAO representa decibeis. */
@Entity
@Table(name = "leituras_sonoras")
public class LeituraSonora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @Column(name = "amplitude_pp", nullable = false)
    private Integer amplitudePp;

    @Column(name = "intervalo_ms", nullable = false)
    private Integer intervaloMs;

    @Column(name = "classificacao", length = 30, nullable = false)
    private String classificacao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Integer getId() { return id; }
    public Dispositivo getDispositivo() { return dispositivo; }
    public Integer getAmplitudePp() { return amplitudePp; }
    public Integer getIntervaloMs() { return intervaloMs; }
    public String getClassificacao() { return classificacao; }
    public LocalDateTime getDataHora() { return dataHora; }

    public void setDispositivo(Dispositivo dispositivo) { this.dispositivo = dispositivo; }
    public void setAmplitudePp(Integer amplitudePp) { this.amplitudePp = amplitudePp; }
    public void setIntervaloMs(Integer intervaloMs) { this.intervaloMs = intervaloMs; }
    public void setClassificacao(String classificacao) { this.classificacao = classificacao; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
