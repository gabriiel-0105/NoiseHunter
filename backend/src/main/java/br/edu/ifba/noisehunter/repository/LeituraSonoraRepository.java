package br.edu.ifba.noisehunter.repository;

import br.edu.ifba.noisehunter.model.LeituraSonora;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeituraSonoraRepository extends JpaRepository<LeituraSonora, Integer> {
    List<LeituraSonora> findTop100ByOrderByDataHoraDesc();
}
