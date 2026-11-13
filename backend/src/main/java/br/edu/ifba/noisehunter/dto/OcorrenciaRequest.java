package br.edu.ifba.noisehunter.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record OcorrenciaRequest(
    @NotNull Integer dispositivoId,
    @NotBlank String tipoSom,
    @NotNull @DecimalMin("0.00") @DecimalMax("200.00") BigDecimal decibeis,
    @Size(max = 100) String localizacao
) {}
