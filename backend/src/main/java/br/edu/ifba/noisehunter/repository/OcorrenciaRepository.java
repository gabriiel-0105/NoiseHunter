package br.edu.ifba.noisehunter.repository;

import br.edu.ifba.noisehunter.model.Ocorrencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Integer> {
    List<Ocorrencia> findAllByOrderByDataHoraDesc();
    boolean existsByDispositivo_Id(Integer dispositivoId);
}
