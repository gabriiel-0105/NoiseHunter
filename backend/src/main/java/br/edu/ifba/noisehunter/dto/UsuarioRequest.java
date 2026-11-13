package br.edu.ifba.noisehunter.dto;

import jakarta.validation.constraints.*;

public record UsuarioRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Email @Size(max = 100) String email,
    @Size(max = 72) String senha
) {}
