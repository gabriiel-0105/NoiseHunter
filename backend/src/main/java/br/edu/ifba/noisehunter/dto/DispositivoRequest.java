package br.edu.ifba.noisehunter.dto;

import jakarta.validation.constraints.*;

public record DispositivoRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Size(max = 100) String localizacao,
    @NotBlank @Pattern(regexp = "ATIVO|INATIVO|MANUTENCAO") String status,
    Integer usuarioId
) {}
