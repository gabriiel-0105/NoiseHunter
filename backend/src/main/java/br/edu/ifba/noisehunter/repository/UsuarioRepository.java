package br.edu.ifba.noisehunter.repository;

import br.edu.ifba.noisehunter.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByEmailIgnoreCase(String email);
}
