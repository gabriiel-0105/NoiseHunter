package br.edu.ifba.noisehunter.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LeituraSonoraRequest(
    @NotNull Integer dispositivoId,
    @NotNull @Min(0) @Max(4095) Integer amplitudePp,
    @NotNull @Min(100) @Max(60000) Integer intervaloMs
) {}
