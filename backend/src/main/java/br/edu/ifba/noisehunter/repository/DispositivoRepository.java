package br.edu.ifba.noisehunter.repository;

import br.edu.ifba.noisehunter.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Integer> {
    boolean existsByUsuario_Id(Integer usuarioId);
}
